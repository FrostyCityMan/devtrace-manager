package com.devtrace.manager.columnspec.parser;

import com.devtrace.manager.columnspec.dto.ColumnSpecEntity;
import com.devtrace.manager.columnspec.dto.DatabaseType;
import com.devtrace.manager.common.exception.BusinessException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class DefaultDdlParser implements DdlParser {

    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile("(?is)CREATE\\s+TABLE\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?([^\\s(]+)\\s*\\(");
    private static final Pattern COMMENT_ON_TABLE_PATTERN = Pattern.compile("(?is)COMMENT\\s+ON\\s+TABLE\\s+([^\\s]+)\\s+IS\\s+'((?:''|[^'])*)'");
    private static final Pattern COMMENT_ON_COLUMN_PATTERN = Pattern.compile("(?is)COMMENT\\s+ON\\s+COLUMN\\s+([^\\s]+)\\.([^\\s.]+)\\s+IS\\s+'((?:''|[^'])*)'");
    private static final Pattern INLINE_COMMENT_PATTERN = Pattern.compile("(?is)\\bCOMMENT\\s+'((?:''|[^'])*)'");
    private static final Pattern TABLE_OPTION_COMMENT_PATTERN = Pattern.compile("(?is)\\bCOMMENT\\s*=\\s*'((?:''|[^'])*)'");
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("(?is)\\bDEFAULT\\s+(.+?)(?=\\s+(?:NOT\\s+NULL|NULL|PRIMARY\\s+KEY|UNIQUE|COMMENT|CONSTRAINT|REFERENCES)\\b|$)");

    @Override
    public List<ColumnSpecEntity> parse(UUID projectId, DatabaseType databaseType, String ddl) {
        if (projectId == null) {
            throw new BusinessException("프로젝트는 필수입니다.", "COLUMN_SPEC_PROJECT_REQUIRED");
        }
        if (ddl == null || ddl.isBlank()) {
            throw new BusinessException("DDL은 필수입니다.", "DDL_REQUIRED");
        }

        String normalizedDdl = ddl.replace("\r\n", "\n");
        Map<String, String> tableComments = extractTableComments(normalizedDdl);
        Map<String, Map<String, String>> columnComments = extractColumnComments(normalizedDdl);
        List<ColumnSpecEntity> result = new ArrayList<>();

        Matcher matcher = CREATE_TABLE_PATTERN.matcher(normalizedDdl);
        while (matcher.find()) {
            String tableName = cleanName(matcher.group(1));
            int openParenIndex = matcher.end() - 1;
            int closeParenIndex = findMatchingParen(normalizedDdl, openParenIndex);
            if (closeParenIndex < 0) {
                throw new BusinessException("CREATE TABLE 괄호를 해석할 수 없습니다.", "DDL_PARSE_ERROR");
            }

            String body = normalizedDdl.substring(openParenIndex + 1, closeParenIndex);
            String tableOptions = readUntilSemicolon(normalizedDdl, closeParenIndex + 1);
            String tableComment = firstNotBlank(tableComments.get(normalizeName(tableName)), extractTableOptionComment(tableOptions));
            result.addAll(parseTableBody(projectId, databaseType, tableName, tableComment, body, columnComments));
        }

        if (result.isEmpty()) {
            throw new BusinessException("파싱 가능한 CREATE TABLE 구문이 없습니다.", "DDL_PARSE_EMPTY");
        }
        return result;
    }

    private List<ColumnSpecEntity> parseTableBody(
            UUID projectId,
            DatabaseType databaseType,
            String tableName,
            String tableComment,
            String body,
            Map<String, Map<String, String>> columnComments
    ) {
        List<String> definitions = splitTopLevel(body);
        Set<String> tablePrimaryKeys = new LinkedHashSet<>();
        Set<String> tableForeignKeys = new LinkedHashSet<>();

        for (String definition : definitions) {
            String upper = definition.trim().toUpperCase(Locale.ROOT);
            if (upper.startsWith("PRIMARY KEY") || upper.startsWith("CONSTRAINT") && upper.contains(" PRIMARY KEY")) {
                tablePrimaryKeys.addAll(extractNamesInParentheses(definition));
            }
            if (upper.startsWith("FOREIGN KEY") || upper.startsWith("CONSTRAINT") && upper.contains(" FOREIGN KEY")) {
                tableForeignKeys.addAll(extractNamesInParentheses(definition));
            }
        }

        List<ColumnSpecEntity> columns = new ArrayList<>();
        for (String definition : definitions) {
            String trimmed = definition.trim();
            if (trimmed.isBlank() || isTableConstraint(trimmed)) {
                continue;
            }

            ColumnToken columnToken = readColumnToken(trimmed);
            if (columnToken == null) {
                continue;
            }

            TypeToken typeToken = readTypeToken(columnToken.remainingDefinition(), databaseType);
            String normalizedColumnName = normalizeName(columnToken.columnName());
            boolean inlinePk = containsWordSequence(trimmed, "PRIMARY KEY");
            boolean isPk = inlinePk || tablePrimaryKeys.contains(normalizedColumnName);
            boolean isFk = containsWordSequence(trimmed, "REFERENCES") || tableForeignKeys.contains(normalizedColumnName);

            ColumnSpecEntity spec = new ColumnSpecEntity();
            spec.setProjectId(projectId);
            spec.setTableName(tableName);
            spec.setTableComment(tableComment);
            spec.setColumnName(columnToken.columnName());
            spec.setColumnComment(firstNotBlank(
                    extractInlineComment(trimmed),
                    columnComments.getOrDefault(normalizeName(tableName), Map.of()).get(normalizedColumnName)
            ));
            spec.setDataType(typeToken.dataType());
            spec.setDataLength(typeToken.dataLength());
            spec.setIsNullable(isPk || containsWordSequence(trimmed, "NOT NULL") ? "N" : "Y");
            spec.setIsPk(isPk ? "Y" : "N");
            spec.setIsFk(isFk ? "Y" : "N");
            spec.setDefaultValue(extractDefaultValue(trimmed));
            spec.setRemark(null);
            columns.add(spec);
        }
        return columns;
    }

    private Map<String, String> extractTableComments(String ddl) {
        Map<String, String> comments = new LinkedHashMap<>();
        Matcher matcher = COMMENT_ON_TABLE_PATTERN.matcher(ddl);
        while (matcher.find()) {
            comments.put(normalizeName(cleanName(matcher.group(1))), unescapeSqlString(matcher.group(2)));
        }
        return comments;
    }

    private Map<String, Map<String, String>> extractColumnComments(String ddl) {
        Map<String, Map<String, String>> comments = new LinkedHashMap<>();
        Matcher matcher = COMMENT_ON_COLUMN_PATTERN.matcher(ddl);
        while (matcher.find()) {
            String tableName = normalizeName(cleanName(matcher.group(1)));
            String columnName = normalizeName(cleanName(matcher.group(2)));
            comments.computeIfAbsent(tableName, key -> new LinkedHashMap<>())
                    .put(columnName, unescapeSqlString(matcher.group(3)));
        }
        return comments;
    }

    private int findMatchingParen(String text, int openParenIndex) {
        int depth = 0;
        boolean inSingleQuote = false;
        for (int i = openParenIndex; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current == '\'' && (i + 1 >= text.length() || text.charAt(i + 1) != '\'')) {
                inSingleQuote = !inSingleQuote;
            } else if (current == '\'' && i + 1 < text.length() && text.charAt(i + 1) == '\'') {
                i++;
            }
            if (inSingleQuote) {
                continue;
            }
            if (current == '(') {
                depth++;
            } else if (current == ')') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private List<String> splitTopLevel(String body) {
        List<String> parts = new ArrayList<>();
        int depth = 0;
        boolean inSingleQuote = false;
        int start = 0;
        for (int i = 0; i < body.length(); i++) {
            char current = body.charAt(i);
            if (current == '\'' && (i + 1 >= body.length() || body.charAt(i + 1) != '\'')) {
                inSingleQuote = !inSingleQuote;
            } else if (current == '\'' && i + 1 < body.length() && body.charAt(i + 1) == '\'') {
                i++;
            }
            if (inSingleQuote) {
                continue;
            }
            if (current == '(') {
                depth++;
            } else if (current == ')') {
                depth--;
            } else if (current == ',' && depth == 0) {
                parts.add(body.substring(start, i));
                start = i + 1;
            }
        }
        parts.add(body.substring(start));
        return parts;
    }

    private boolean isTableConstraint(String definition) {
        String upper = definition.trim().toUpperCase(Locale.ROOT);
        return upper.startsWith("PRIMARY KEY")
                || upper.startsWith("FOREIGN KEY")
                || upper.startsWith("UNIQUE")
                || upper.startsWith("CHECK")
                || upper.startsWith("CONSTRAINT");
    }

    private ColumnToken readColumnToken(String definition) {
        String trimmed = definition.trim();
        if (trimmed.isBlank()) {
            return null;
        }
        if (trimmed.charAt(0) == '"' || trimmed.charAt(0) == '`' || trimmed.charAt(0) == '[') {
            char close = trimmed.charAt(0) == '[' ? ']' : trimmed.charAt(0);
            int end = trimmed.indexOf(close, 1);
            if (end < 0) {
                return null;
            }
            return new ColumnToken(cleanName(trimmed.substring(0, end + 1)), trimmed.substring(end + 1).trim());
        }

        int end = 0;
        while (end < trimmed.length() && !Character.isWhitespace(trimmed.charAt(end))) {
            end++;
        }
        if (end == 0 || end >= trimmed.length()) {
            return null;
        }
        return new ColumnToken(cleanName(trimmed.substring(0, end)), trimmed.substring(end).trim());
    }

    private TypeToken readTypeToken(String definition, DatabaseType databaseType) {
        String trimmed = definition.trim();
        int end = 0;
        int depth = 0;
        while (end < trimmed.length()) {
            char current = trimmed.charAt(end);
            if (current == '(') {
                depth++;
            } else if (current == ')') {
                depth--;
            } else if (Character.isWhitespace(current) && depth == 0) {
                String next = trimmed.substring(end).trim().toUpperCase(Locale.ROOT);
                if (isColumnModifier(next)) {
                    break;
                }
            }
            end++;
        }

        String rawType = trimmed.substring(0, end).trim();
        if (rawType.isBlank()) {
            return new TypeToken(null, null);
        }

        Matcher lengthMatcher = Pattern.compile("(?is)^(.+?)\\((.+)\\)$").matcher(rawType);
        if (lengthMatcher.find()) {
            return new TypeToken(normalizeTypeName(lengthMatcher.group(1), databaseType), lengthMatcher.group(2).trim());
        }
        return new TypeToken(normalizeTypeName(rawType, databaseType), null);
    }

    private boolean isColumnModifier(String text) {
        return text.startsWith("NOT NULL")
                || text.startsWith("NULL")
                || text.startsWith("DEFAULT")
                || text.startsWith("PRIMARY KEY")
                || text.startsWith("UNIQUE")
                || text.startsWith("REFERENCES")
                || text.startsWith("COMMENT")
                || text.startsWith("CONSTRAINT")
                || text.startsWith("COLLATE");
    }

    private String normalizeTypeName(String dataType, DatabaseType databaseType) {
        String normalized = dataType.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
        if (databaseType == DatabaseType.MYSQL && normalized.equals("INTEGER")) {
            return "INT";
        }
        return normalized;
    }

    private List<String> extractNamesInParentheses(String definition) {
        int open = definition.indexOf('(');
        int close = definition.indexOf(')', open + 1);
        if (open < 0 || close < 0) {
            return List.of();
        }
        List<String> names = new ArrayList<>();
        for (String name : definition.substring(open + 1, close).split(",")) {
            names.add(normalizeName(cleanName(name)));
        }
        return names;
    }

    private String extractInlineComment(String definition) {
        Matcher matcher = INLINE_COMMENT_PATTERN.matcher(definition);
        return matcher.find() ? unescapeSqlString(matcher.group(1)) : null;
    }

    private String extractTableOptionComment(String tableOptions) {
        Matcher matcher = TABLE_OPTION_COMMENT_PATTERN.matcher(tableOptions);
        return matcher.find() ? unescapeSqlString(matcher.group(1)) : null;
    }

    private String extractDefaultValue(String definition) {
        Matcher matcher = DEFAULT_PATTERN.matcher(definition);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1).trim().replaceAll(",$", "");
    }

    private String readUntilSemicolon(String text, int start) {
        int end = text.indexOf(';', start);
        if (end < 0) {
            end = Math.min(text.length(), start + 500);
        }
        return text.substring(start, end);
    }

    private boolean containsWordSequence(String text, String sequence) {
        String regex = "(?is).*\\b" + sequence.replace(" ", "\\s+") + "\\b.*";
        return text.matches(regex);
    }

    private String cleanName(String name) {
        String cleaned = name.trim();
        if (cleaned.contains(".")) {
            cleaned = cleaned.substring(cleaned.lastIndexOf('.') + 1);
        }
        if ((cleaned.startsWith("\"") && cleaned.endsWith("\""))
                || (cleaned.startsWith("`") && cleaned.endsWith("`"))
                || (cleaned.startsWith("[") && cleaned.endsWith("]"))) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        return cleaned.trim();
    }

    private String normalizeName(String name) {
        return cleanName(name).toUpperCase(Locale.ROOT);
    }

    private String unescapeSqlString(String value) {
        return value == null ? null : value.replace("''", "'");
    }

    private String firstNotBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second == null || second.isBlank() ? null : second;
    }

    private record ColumnToken(String columnName, String remainingDefinition) {
    }

    private record TypeToken(String dataType, String dataLength) {
    }
}
