package com.devtrace.manager.vcs.parser;

import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.vcs.dto.VcsChangeFileEntity;
import com.devtrace.manager.vcs.dto.VcsChangeLogEntity;
import com.devtrace.manager.vcs.dto.VcsType;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class GitLogParser implements VcsLogParser {

    @Override
    public List<VcsChangeLogEntity> parse(UUID projectId, String logText) {
        if (logText == null || logText.isBlank()) {
            throw new BusinessException("Git 로그 텍스트는 필수입니다.", "GIT_LOG_REQUIRED");
        }
        String normalized = logText.replace("\r\n", "\n").trim();
        List<VcsChangeLogEntity> logs = normalized.contains("commit|")
                ? parseNameStatus(projectId, normalized)
                : parsePipeLines(projectId, normalized);
        if (logs.isEmpty()) {
            throw new BusinessException("파싱 가능한 Git 로그가 없습니다.", "GIT_LOG_PARSE_EMPTY");
        }
        return logs;
    }

    private List<VcsChangeLogEntity> parsePipeLines(UUID projectId, String logText) {
        List<VcsChangeLogEntity> logs = new ArrayList<>();
        for (String line : logText.split("\\n")) {
            if (line.isBlank()) {
                continue;
            }
            String[] parts = line.split("\\|", 4);
            if (parts.length < 4) {
                continue;
            }
            logs.add(createLog(projectId, parts[0], parts[1], parts[2], parts[3]));
        }
        return logs;
    }

    private List<VcsChangeLogEntity> parseNameStatus(UUID projectId, String logText) {
        List<VcsChangeLogEntity> logs = new ArrayList<>();
        VcsChangeLogEntity current = null;
        for (String rawLine : logText.split("\\n")) {
            String line = rawLine.trim();
            if (line.isBlank()) {
                continue;
            }
            if (line.startsWith("commit|")) {
                String[] parts = line.split("\\|", 5);
                if (parts.length >= 5) {
                    current = createLog(projectId, parts[1], parts[2], parts[3], parts[4]);
                    logs.add(current);
                }
                continue;
            }
            if (current != null) {
                VcsChangeFileEntity file = parseGitFile(line);
                if (file != null) {
                    current.getChangedFiles().add(file);
                }
            }
        }
        return logs;
    }

    private VcsChangeFileEntity parseGitFile(String line) {
        String[] parts = line.split("\\s+", 3);
        if (parts.length < 2 || !parts[0].matches("[AMDR].*")) {
            return null;
        }
        VcsChangeFileEntity file = new VcsChangeFileEntity();
        file.setChangeType(parts[0].substring(0, 1));
        file.setFilePath(parts.length == 3 ? parts[2] : parts[1]);
        return file;
    }

    private VcsChangeLogEntity createLog(UUID projectId, String revisionNo, String author, String changedAt, String message) {
        VcsChangeLogEntity log = new VcsChangeLogEntity();
        log.setProjectId(projectId);
        log.setVcsType(VcsType.GIT);
        log.setRevisionNo(revisionNo.trim());
        log.setAuthor(author.trim());
        log.setChangedAt(parseDateTime(changedAt.trim()));
        log.setMessage(message.trim());
        log.setIssueKeys(IssueKeyMatcher.extractIssueKeys(message));
        return log;
    }

    private LocalDateTime parseDateTime(String value) {
        try {
            return OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException ignoredAgain) {
                String normalized = value.replace(" ", "T");
                try {
                    return OffsetDateTime.parse(normalized, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
                } catch (DateTimeParseException ignoredThird) {
                    return LocalDateTime.parse(normalized.substring(0, Math.min(19, normalized.length())));
                }
            }
        }
    }
}
