package com.devtrace.manager.artifact.markdown;

import com.devtrace.manager.artifact.dto.WeeklyReportData;
import com.devtrace.manager.artifact.dto.WeeklyReportIssueRow;
import com.devtrace.manager.artifact.dto.WeeklyReportVcsRow;
import com.devtrace.manager.artifact.dto.WeeklyReportWorkLogRow;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class WeeklyReportMarkdownGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String generate(WeeklyReportData data) {
        StringBuilder markdown = new StringBuilder();
        appendLine(markdown, "## " + data.getStartDate() + " ~ " + data.getEndDate());
        appendLine(markdown, "");
        appendLine(markdown, "- 프로젝트: " + projectName(data));
        appendLine(markdown, "");

        appendAssignedWork(markdown, data.getIssues());
        appendPerformedWork(markdown, data.getWorkLogs(), data.getIssues());
        appendIssueStatus(markdown, data);
        appendEffortStatus(markdown, data);
        appendVcsLogs(markdown, data.getVcsLogs());
        appendNextWeekPlan(markdown, data.getIssues());
        appendRisks(markdown, data.getIssues(), data.getEndDate());
        return markdown.toString();
    }

    private void appendAssignedWork(StringBuilder markdown, List<WeeklyReportIssueRow> issues) {
        appendLine(markdown, "## 금주 배정 업무");
        if (issues.isEmpty()) {
            appendLine(markdown, "- 없음");
        } else {
            issues.stream()
                    .limit(10)
                    .forEach(issue -> appendLine(markdown, "- " + issue.getIssueKey() + " - " + issue.getTitle() + " (" + statusLabel(issue) + ")"));
        }
        appendLine(markdown, "");
    }

    private void appendPerformedWork(StringBuilder markdown, List<WeeklyReportWorkLogRow> workLogs, List<WeeklyReportIssueRow> issues) {
        appendLine(markdown, "## 금주 수행 업무");
        List<WeeklyReportIssueRow> completedIssues = issues.stream()
                .filter(WeeklyReportIssueRow::isCompleted)
                .toList();
        if (completedIssues.isEmpty() && workLogs.isEmpty()) {
            appendLine(markdown, "- 없음");
        } else {
            completedIssues.forEach(issue -> appendLine(markdown, "- 완료: " + issue.getIssueKey() + " - " + issue.getTitle()));
            workLogs.stream()
                    .limit(12)
                    .forEach(workLog -> appendLine(markdown, "- " + workLog.getIssueKey() + " - " + workLog.getWorkContent() + " (" + minutesToHours(workLog.getSpentMinutes()) + ")"));
        }
        appendLine(markdown, "");
    }

    private void appendIssueStatus(StringBuilder markdown, WeeklyReportData data) {
        long doneCount = data.getIssues().stream().filter(WeeklyReportIssueRow::isCompleted).count();
        long activeCount = data.getIssues().stream().filter(WeeklyReportIssueRow::isActive).count();
        long holdCount = data.getIssues().stream().filter(WeeklyReportIssueRow::isHold).count();
        long delayedCount = data.getIssues().stream().filter(issue -> issue.isDelayed(data.getEndDate())).count();

        appendLine(markdown, "## 이슈 처리 현황");
        appendLine(markdown, "- 전체: " + data.getIssues().size());
        appendLine(markdown, "- 완료: " + doneCount);
        appendLine(markdown, "- 진행 중: " + activeCount);
        appendLine(markdown, "- 보류: " + holdCount);
        appendLine(markdown, "- 지연: " + delayedCount);
        appendLine(markdown, "");
    }

    private void appendEffortStatus(StringBuilder markdown, WeeklyReportData data) {
        int estimated = data.getEstimatedMinutesTotal();
        int spent = data.getSpentMinutesTotal();
        appendLine(markdown, "## 공수 현황");
        appendLine(markdown, "- 예상 공수: " + minutesToHours(estimated));
        appendLine(markdown, "- 실제 공수: " + minutesToHours(spent));
        appendLine(markdown, "- 차이: " + minutesToHours(spent - estimated));
        appendLine(markdown, "");
    }

    private void appendVcsLogs(StringBuilder markdown, List<WeeklyReportVcsRow> vcsLogs) {
        appendLine(markdown, "## 변경 이력");
        if (vcsLogs.isEmpty()) {
            appendLine(markdown, "- 없음");
        } else {
            vcsLogs.stream()
                    .limit(10)
                    .forEach(log -> appendLine(markdown, "- " + vcsLabel(log) + " " + log.getRevisionNo() + " - " + issueKeyText(log) + log.getMessage() + changedAtText(log)));
        }
        appendLine(markdown, "");
    }

    private void appendNextWeekPlan(StringBuilder markdown, List<WeeklyReportIssueRow> issues) {
        appendLine(markdown, "## 차주 업무계획");
        List<WeeklyReportIssueRow> targets = issues.stream()
                .filter(issue -> !issue.isCompleted())
                .limit(8)
                .toList();
        if (targets.isEmpty()) {
            appendLine(markdown, "- 예정 업무 없음");
        } else {
            targets.forEach(issue -> appendLine(markdown, "- " + issue.getIssueKey() + " - " + issue.getTitle()));
        }
        appendLine(markdown, "");
    }

    private void appendRisks(StringBuilder markdown, List<WeeklyReportIssueRow> issues, java.time.LocalDate endDate) {
        appendLine(markdown, "## 예상 문제점");
        List<WeeklyReportIssueRow> risks = issues.stream()
                .filter(issue -> issue.isHold() || issue.isDelayed(endDate))
                .limit(8)
                .toList();
        if (risks.isEmpty()) {
            appendLine(markdown, "- 없음");
        } else {
            risks.forEach(issue -> appendLine(markdown, "- " + issue.getIssueKey() + " - " + issue.getTitle() + " (" + statusLabel(issue) + ")"));
        }
    }

    private String projectName(WeeklyReportData data) {
        if (data.getProject() == null) {
            return "-";
        }
        return data.getProject().getProjectCode() + " - " + data.getProject().getProjectName();
    }

    private String statusLabel(WeeklyReportIssueRow issue) {
        return issue.getStatus() == null ? "-" : issue.getStatus().getLabel();
    }

    private String vcsLabel(WeeklyReportVcsRow log) {
        return log.getVcsType() == null ? "VCS" : log.getVcsType().name();
    }

    private String issueKeyText(WeeklyReportVcsRow log) {
        return log.getIssueKeyText() == null || log.getIssueKeyText().isBlank() ? "" : "[" + log.getIssueKeyText() + "] ";
    }

    private String changedAtText(WeeklyReportVcsRow log) {
        return log.getChangedAt() == null ? "" : " (" + DATE_TIME_FORMATTER.format(log.getChangedAt()) + ")";
    }

    private String minutesToHours(Integer minutes) {
        int value = minutes == null ? 0 : minutes;
        return String.format(Locale.ROOT, "%.1fh", value / 60.0);
    }

    private void appendLine(StringBuilder markdown, String line) {
        markdown.append(line).append(System.lineSeparator());
    }
}
