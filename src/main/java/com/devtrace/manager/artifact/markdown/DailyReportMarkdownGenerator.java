package com.devtrace.manager.artifact.markdown;

import com.devtrace.manager.artifact.dto.DailyReportData;
import com.devtrace.manager.artifact.dto.DailyReportIssueRow;
import com.devtrace.manager.artifact.dto.WeeklyReportVcsRow;
import com.devtrace.manager.artifact.dto.WeeklyReportWorkLogRow;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DailyReportMarkdownGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    private void appendTodayWork(StringBuilder markdown, List<WeeklyReportWorkLogRow> workLogs) {
        appendLine(markdown, "## 금일 업무");
        if (workLogs.isEmpty()) {
            appendLine(markdown, "- 없음");
        } else {
            workLogs.forEach(workLog -> appendLine(markdown, "- " + workLog.getIssueKey() + " - " + workLog.getWorkContent()));
        }
        appendLine(markdown, "");
    }

    private void appendIssueStatus(StringBuilder markdown, DailyReportData data) {
        appendLine(markdown, "## 진행 현황");
        appendLine(markdown, "- 전체 이슈: " + data.getIssues().size());
        appendLine(markdown, "- 완료 이슈: " + data.getDoneIssueCount());
        appendLine(markdown, "- 진행 중 이슈: " + data.getActiveIssueCount());
        appendLine(markdown, "- 지연 이슈: " + data.getDelayedIssueCount());
        appendLine(markdown, "");
    }

    private void appendVcsLogs(StringBuilder markdown, List<WeeklyReportVcsRow> vcsLogs) {
        appendLine(markdown, "## 변경 이력");
        if (vcsLogs.isEmpty()) {
            appendLine(markdown, "- 없음");
        } else {
            vcsLogs.forEach(log -> appendLine(markdown, "- " + revisionText(log) + " - " + nullToDash(log.getMessage())));
        }
        appendLine(markdown, "");
    }

    private void appendSpecialNotes(StringBuilder markdown) {
        appendLine(markdown, "## 특이사항");
        appendLine(markdown, "- 없음");
        appendLine(markdown, "");
    }

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

    private String projectName(DailyReportData data) {
        if (data.getProject() == null) {
            return "-";
        }
        return data.getProject().getProjectCode() + " - " + data.getProject().getProjectName();
    }

    private String revisionText(WeeklyReportVcsRow log) {
        String vcs = log.getVcsType() == null ? "VCS" : log.getVcsType().name();
        String changedAt = log.getChangedAt() == null ? "" : " (" + DATE_TIME_FORMATTER.format(log.getChangedAt()) + ")";
        return vcs + " " + nullToDash(log.getRevisionNo()) + changedAt;
    }

    private String nullToDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void appendLine(StringBuilder markdown, String line) {
        markdown.append(line).append(System.lineSeparator());
    }
}
