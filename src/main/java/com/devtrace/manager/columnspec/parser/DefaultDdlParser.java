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

/**
 * PostgreSQL, Oracle, MySQL의 기본 {@code CREATE TABLE} DDL을 해석하는 기본 파서입니다.
 *
 * <p>테이블/컬럼 주석, 인라인 PK/FK, 테이블 제약 PK/FK, 기본값, 타입 길이를 추출하여
 * 컬럼명세 Excel 생성에 필요한 표준 모델로 변환합니다.</p>
 */
@Component
public class DefaultDdlParser implements DdlParser {

    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile("(?is)CREATE\\s+TABLE\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?([^\\s(]+)\\s*\\(");
    private static final Pattern COMMENT_ON_TABLE_PATTERN = Pattern.compile("(?is)COMMENT\\s+ON\\s+TABLE\\s+([^\\s]+)\\s+IS\\s+'((?:''|[^'])*)'");
    private static final Pattern COMMENT_ON_COLUMN_PATTERN = Pattern.compile("(?is)COMMENT\\s+ON\\s+COLUMN\\s+([^\\s]+)\\.([^\\s.]+)\\s+IS\\s+'((?:''|[^'])*)'");
    private static final Pattern INLINE_COMMENT_PATTERN = Pattern.compile("(?is)\\bCOMMENT\\s+'((?:''|[^'])*)'");
    private static final Pattern TABLE_OPTION_COMMENT_PATTERN = Pattern.compile("(?is)\\bCOMMENT\\s*=\\s*'((?:''|[^'])*)'");
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("(?is)\\bDEFAULT\\s+(.+?)(?=\\s+(?:NOT\\s+NULL|NULL|PRIMARY\\s+KEY|UNIQUE|COMMENT|CONSTRAINT|REFERENCES)\\b|$)");

    /**
     * {@inheritDoc}
     */
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

    /**
     * 단일 테이블 정의 본문을 컬럼명세 목록으로 변환합니다.
     *
     * @param projectId 프로젝트 ID
     * @param databaseType DB 유형
     * @param tableName 테이블명
     * @param tableComment 테이블 설명
     * @param body 괄호 내부 컬럼 및 제약 정의
     * @param columnComments COMMENT ON COLUMN 문에서 추출한 컬럼 설명 맵
     * @return 테이블 내 컬럼명세 목록
     */
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

    /**
     * {@code COMMENT ON TABLE} 구문에서 테이블 설명을 추출합니다.
     *
     * @param ddl 전체 DDL 문자열
     * @return 정규화된 테이블명별 설명 맵
     */
    private Map<String, String> extractTableComments(String ddl) {
        Map<String, String> comments = new LinkedHashMap<>();
        Matcher matcher = COMMENT_ON_TABLE_PATTERN.matcher(ddl);
        while (matcher.find()) {
            comments.put(normalizeName(cleanName(matcher.group(1))), unescapeSqlString(matcher.group(2)));
        }
        return comments;
    }

    /**
     * {@code COMMENT ON COLUMN} 구문에서 컬럼 설명을 추출합니다.
     *
     * @param ddl 전체 DDL 문자열
     * @return 정규화된 테이블명과 컬럼명별 설명 맵
     */
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

    /**
     * CREATE TABLE 시작 괄호와 짝이 되는 닫는 괄호 위치를 찾습니다.
     *
     * @param text 전체 DDL 문자열
     * @param openParenIndex 시작 괄호 위치
     * @return 닫는 괄호 위치, 찾지 못하면 -1
     */
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

    /**
     * 최상위 콤마만 기준으로 컬럼/제약 정의를 분리합니다.
     *
     * @param body CREATE TABLE 괄호 내부 문자열
     * @return 분리된 정의 목록
     */
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

    /**
     * 주어진 정의가 테이블 수준 제약 조건인지 판단합니다.
     *
     * @param definition 컬럼 또는 제약 정의
     * @return 테이블 수준 제약이면 true
     */
    private boolean isTableConstraint(String definition) {
        String upper = definition.trim().toUpperCase(Locale.ROOT);
        return upper.startsWith("PRIMARY KEY")
                || upper.startsWith("FOREIGN KEY")
                || upper.startsWith("UNIQUE")
                || upper.startsWith("CHECK")
                || upper.startsWith("CONSTRAINT");
    }

    /**
     * 컬럼 정의에서 컬럼명 토큰과 나머지 정의를 분리합니다.
     *
     * @param definition 컬럼 정의 문자열
     * @return 컬럼 토큰, 해석할 수 없으면 {@code null}
     */
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

    /**
     * 컬럼 정의에서 데이터 타입과 길이 표현을 추출합니다.
     *
     * @param definition 컬럼명 이후 정의 문자열
     * @param databaseType DB 유형
     * @return 데이터 타입 토큰
     */
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

    /**
     * 컬럼 타입 뒤에 올 수 있는 컬럼 제약/수식어인지 판단합니다.
     *
     * @param text 타입 이후 문자열
     * @return 컬럼 수식어이면 true
     */
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

    /**
     * DB 유형 차이를 반영하여 데이터 타입명을 정규화합니다.
     *
     * @param dataType 원본 데이터 타입
     * @param databaseType DB 유형
     * @return 정규화된 데이터 타입
     */
    private String normalizeTypeName(String dataType, DatabaseType databaseType) {
        String normalized = dataType.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
        if (databaseType == DatabaseType.MYSQL && normalized.equals("INTEGER")) {
            return "INT";
        }
        return normalized;
    }

    /**
     * 괄호 안 컬럼명 목록을 추출합니다.
     *
     * @param definition PRIMARY KEY 또는 FOREIGN KEY 정의
     * @return 정규화된 컬럼명 목록
     */
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

    /**
     * 컬럼 정의에 포함된 인라인 COMMENT 값을 추출합니다.
     *
     * @param definition 컬럼 정의 문자열
     * @return 컬럼 설명, 없으면 {@code null}
     */
    private String extractInlineComment(String definition) {
        Matcher matcher = INLINE_COMMENT_PATTERN.matcher(definition);
        return matcher.find() ? unescapeSqlString(matcher.group(1)) : null;
    }

    /**
     * MySQL 테이블 옵션 COMMENT 값을 추출합니다.
     *
     * @param tableOptions CREATE TABLE 닫는 괄호 뒤 옵션 문자열
     * @return 테이블 설명, 없으면 {@code null}
     */
    private String extractTableOptionComment(String tableOptions) {
        Matcher matcher = TABLE_OPTION_COMMENT_PATTERN.matcher(tableOptions);
        return matcher.find() ? unescapeSqlString(matcher.group(1)) : null;
    }

    /**
     * 컬럼 정의에서 DEFAULT 값을 추출합니다.
     *
     * @param definition 컬럼 정의 문자열
     * @return 기본값, 없으면 {@code null}
     */
    private String extractDefaultValue(String definition) {
        Matcher matcher = DEFAULT_PATTERN.matcher(definition);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1).trim().replaceAll(",$", "");
    }

    /**
     * 지정 위치부터 세미콜론까지의 테이블 옵션 문자열을 읽습니다.
     *
     * @param text 전체 DDL 문자열
     * @param start 읽기 시작 위치
     * @return 테이블 옵션 문자열
     */
    private String readUntilSemicolon(String text, int start) {
        int end = text.indexOf(';', start);
        if (end < 0) {
            end = Math.min(text.length(), start + 500);
        }
        return text.substring(start, end);
    }

    /**
     * 공백 차이를 허용하여 특정 단어 시퀀스 포함 여부를 판단합니다.
     *
     * @param text 검사 대상 문자열
     * @param sequence 검사할 단어 시퀀스
     * @return 포함되어 있으면 true
     */
    private boolean containsWordSequence(String text, String sequence) {
        String regex = "(?is).*\\b" + sequence.replace(" ", "\\s+") + "\\b.*";
        return text.matches(regex);
    }

    /**
     * 스키마명과 인용 부호를 제거하여 객체명을 정리합니다.
     *
     * @param name 원본 객체명
     * @return 정리된 객체명
     */
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

    /**
     * 비교용 객체명을 대문자로 정규화합니다.
     *
     * @param name 원본 객체명
     * @return 정규화된 객체명
     */
    private String normalizeName(String name) {
        return cleanName(name).toUpperCase(Locale.ROOT);
    }

    /**
     * SQL 문자열 리터럴의 이스케이프된 작은따옴표를 복원합니다.
     *
     * @param value SQL 문자열 값
     * @return 복원된 문자열
     */
    private String unescapeSqlString(String value) {
        return value == null ? null : value.replace("''", "'");
    }

    /**
     * 첫 번째 문자열이 비어 있으면 두 번째 문자열을 사용합니다.
     *
     * @param first 우선 사용할 문자열
     * @param second 대체 문자열
     * @return 공백이 아닌 문자열 또는 {@code null}
     */
    private String firstNotBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second == null || second.isBlank() ? null : second;
    }

    /**
     * 컬럼명과 나머지 컬럼 정의를 함께 보관하는 내부 토큰입니다.
     *
     * @param columnName 컬럼명
     * @param remainingDefinition 컬럼명 이후 정의
     */
    private record ColumnToken(String columnName, String remainingDefinition) {
    }

    /**
     * 데이터 타입명과 길이 표현을 함께 보관하는 내부 토큰입니다.
     *
     * @param dataType 데이터 타입명
     * @param dataLength 길이 또는 정밀도 표현
     */
    private record TypeToken(String dataType, String dataLength) {
    }
}
