package com.devtrace.manager.artifact.markdown;

import com.devtrace.manager.artifact.dto.DailyReportData;
import com.devtrace.manager.artifact.dto.DailyReportIssueRow;
import com.devtrace.manager.artifact.dto.WeeklyReportVcsRow;
import com.devtrace.manager.artifact.dto.WeeklyReportWorkLogRow;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 일일 업무보고 Markdown 문서를 생성합니다.
 *
 * <p>기준일 공수, 이슈 진행 현황, 변경이력, 익일 작업 대상, 위험 항목을
 * DevTrace Markdown 보고서 규칙에 맞춰 작성합니다.</p>
 */
@Component
public class DailyReportMarkdownGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 일일 업무보고 Markdown 본문을 생성합니다.
     *
     * @param data 일일 업무보고 원천 데이터
     * @return Markdown 문자열
     */
    public String generate(DailyReportData data) {
        StringBuilder markdown = new StringBuilder();
        appendLine(markdown, "## " + data.getBaseDate());
        appendLine(markdown, "");
        appendLine(markdown, "- 프로젝트: " + projectName(data));
        appendLine(markdown, "");

        appendTodayWork(markdown, data.getWorkLogs());
        appendIssueStatus(markdown, data);
        appendVcsLogs(markdown, data.getVcsLogs());
        appendSpecialNotes(markdown);
        appendTomorrowWork(markdown, data);
        appendRisks(markdown, data);
        return markdown.toString();
    }

    /**
     * 금일 업무 섹션을 작성합니다.
     *
     * @param markdown Markdown 버퍼
     * @param workLogs 기준일 공수 목록
     */
    private void appendTodayWork(StringBuilder markdown, List<WeeklyReportWorkLogRow> workLogs) {
        appendLine(markdown, "## 금일 업무");
        if (workLogs.isEmpty()) {
            appendLine(markdown, "- 없음");
        } else {
            workLogs.forEach(workLog -> appendLine(markdown, "- " + workLog.getIssueKey() + " - " + workLog.getWorkContent()));
        }
        appendLine(markdown, "");
    }

    /**
     * 진행 현황 섹션을 작성합니다.
     *
     * @param markdown Markdown 버퍼
     * @param data 일일 업무보고 원천 데이터
     */
    private void appendIssueStatus(StringBuilder markdown, DailyReportData data) {
        appendLine(markdown, "## 진행 현황");
        appendLine(markdown, "- 전체 이슈: " + data.getIssues().size());
        appendLine(markdown, "- 완료 이슈: " + data.getDoneIssueCount());
        appendLine(markdown, "- 진행 중 이슈: " + data.getActiveIssueCount());
        appendLine(markdown, "- 지연 이슈: " + data.getDelayedIssueCount());
        appendLine(markdown, "");
    }

    /**
     * 변경 이력 섹션을 작성합니다.
     *
     * @param markdown Markdown 버퍼
     * @param vcsLogs 기준일 변경이력 목록
     */
    private void appendVcsLogs(StringBuilder markdown, List<WeeklyReportVcsRow> vcsLogs) {
        appendLine(markdown, "## 변경 이력");
        if (vcsLogs.isEmpty()) {
            appendLine(markdown, "- 없음");
        } else {
            vcsLogs.forEach(log -> appendLine(markdown, "- " + revisionText(log) + " - " + nullToDash(log.getMessage())));
        }
        appendLine(markdown, "");
    }

    /**
     * 특이사항 섹션을 기본값으로 작성합니다.
     *
     * @param markdown Markdown 버퍼
     */
    private void appendSpecialNotes(StringBuilder markdown) {
        appendLine(markdown, "## 특이사항");
        appendLine(markdown, "- 없음");
        appendLine(markdown, "");
    }

    /**
     * 익일 업무 섹션을 작성합니다.
     *
     * @param markdown Markdown 버퍼
     * @param data 일일 업무보고 원천 데이터
     */
    private void appendTomorrowWork(StringBuilder markdown, DailyReportData data) {
        appendLine(markdown, "## 익일 업무");
        List<DailyReportIssueRow> targets = data.getIssues().stream()
                .filter(issue -> issue.isNextWorkTarget(data.getBaseDate()))
                .limit(10)
                .toList();
        if (targets.isEmpty()) {
            appendLine(markdown, "- 예정 작업 없음");
        } else {
            targets.forEach(issue -> appendLine(markdown, "- " + issue.getIssueKey() + " - " + issue.getTitle()));
        }
        appendLine(markdown, "");
    }

    /**
     * 예상 문제점 섹션을 작성합니다.
     *
     * @param markdown Markdown 버퍼
     * @param data 일일 업무보고 원천 데이터
     */
    private void appendRisks(StringBuilder markdown, DailyReportData data) {
        appendLine(markdown, "## 예상 문제점");
        List<DailyReportIssueRow> risks = data.getIssues().stream()
                .filter(issue -> issue.isDelayed(data.getBaseDate()) || issue.isRisk())
                .limit(10)
                .toList();
        if (risks.isEmpty()) {
            appendLine(markdown, "- 없음");
        } else {
            risks.forEach(issue -> appendLine(markdown, "- " + issue.getIssueKey() + " - " + issue.getTitle()));
        }
    }

    /**
     * 보고서에 표시할 프로젝트명을 구성합니다.
     *
     * @param data 일일 업무보고 원천 데이터
     * @return 프로젝트 표시명
     */
    private String projectName(DailyReportData data) {
        if (data.getProject() == null) {
            return "-";
        }
        return data.getProject().getProjectCode() + " - " + data.getProject().getProjectName();
    }

    /**
     * 변경이력의 리비전 표시 문자열을 구성합니다.
     *
     * @param log 변경이력 행
     * @return 리비전 표시 문자열
     */
    private String revisionText(WeeklyReportVcsRow log) {
        String vcs = log.getVcsType() == null ? "VCS" : log.getVcsType().name();
        String changedAt = log.getChangedAt() == null ? "" : " (" + DATE_TIME_FORMATTER.format(log.getChangedAt()) + ")";
        return vcs + " " + nullToDash(log.getRevisionNo()) + changedAt;
    }

    /**
     * 빈 문자열을 대시로 치환합니다.
     *
     * @param value 원본 문자열
     * @return 표시 문자열
     */
    private String nullToDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    /**
     * Markdown 버퍼에 한 줄을 추가합니다.
     *
     * @param markdown Markdown 버퍼
     * @param line 추가할 줄
     */
    private void appendLine(StringBuilder markdown, String line) {
        markdown.append(line).append(System.lineSeparator());
    }
}
