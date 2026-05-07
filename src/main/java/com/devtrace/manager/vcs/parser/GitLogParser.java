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

/**
 * Git 로그 텍스트를 변경이력 모델로 파싱합니다.
 *
 * <p>지원 입력은 {@code hash|author|date|message} 형식과
 * {@code commit|hash|author|date|message}에 파일 상태가 뒤따르는 {@code --name-status}
 * 형식입니다.</p>
 */
@Component
public class GitLogParser implements VcsLogParser {

    /**
     * {@inheritDoc}
     */
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

    /**
     * 파이프 구분 한 줄 Git 로그를 파싱합니다.
     *
     * @param projectId 프로젝트 ID
     * @param logText Git 로그 문자열
     * @return 변경이력 목록
     */
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

    /**
     * {@code --name-status} 형식의 Git 로그를 파싱합니다.
     *
     * @param projectId 프로젝트 ID
     * @param logText Git 로그 문자열
     * @return 변경 파일이 포함된 변경이력 목록
     */
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

    /**
     * Git 변경 파일 행을 변경 파일 엔티티로 변환합니다.
     *
     * @param line 파일 상태 행
     * @return 변경 파일 엔티티, 인식할 수 없으면 {@code null}
     */
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

    /**
     * Git 커밋 정보를 공통 변경이력 엔티티로 생성합니다.
     *
     * @param projectId 프로젝트 ID
     * @param revisionNo 커밋 해시
     * @param author 작성자
     * @param changedAt 변경 일시 문자열
     * @param message 커밋 메시지
     * @return 변경이력 엔티티
     */
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

    /**
     * Git 로그의 ISO 계열 일시 문자열을 {@link LocalDateTime}으로 변환합니다.
     *
     * @param value 일시 문자열
     * @return 로컬 일시
     */
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
