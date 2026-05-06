package com.devtrace.manager.artifact.dto;

import com.devtrace.manager.project.dto.ProjectResponse;
import java.time.LocalDate;
import java.util.List;

public class DailyReportData {

    private ProjectResponse project;
    private LocalDate baseDate;
    private List<DailyReportIssueRow> issues = List.of();
    private List<WeeklyReportWorkLogRow> workLogs = List.of();
    private List<WeeklyReportVcsRow> vcsLogs = List.of();

    public int getSpentMinutesTotal() {
        return workLogs.stream()
                .mapToInt(workLog -> workLog.getSpentMinutes() == null ? 0 : workLog.getSpentMinutes())
                .sum();
    }

    public long getDoneIssueCount() {
        return issues.stream().filter(DailyReportIssueRow::isCompleted).count();
    }

    public long getActiveIssueCount() {
        return issues.stream().filter(DailyReportIssueRow::isActive).count();
    }

    public long getDelayedIssueCount() {
        return issues.stream().filter(issue -> issue.isDelayed(baseDate)).count();
    }

    public ProjectResponse getProject() {
        return project;
    }

    public void setProject(ProjectResponse project) {
        this.project = project;
    }

    public LocalDate getBaseDate() {
        return baseDate;
    }

    public void setBaseDate(LocalDate baseDate) {
        this.baseDate = baseDate;
    }

    public List<DailyReportIssueRow> getIssues() {
        return issues;
    }

    public void setIssues(List<DailyReportIssueRow> issues) {
        this.issues = issues == null ? List.of() : issues;
    }

    public List<WeeklyReportWorkLogRow> getWorkLogs() {
        return workLogs;
    }

    public void setWorkLogs(List<WeeklyReportWorkLogRow> workLogs) {
        this.workLogs = workLogs == null ? List.of() : workLogs;
    }

    public List<WeeklyReportVcsRow> getVcsLogs() {
        return vcsLogs;
    }

    public void setVcsLogs(List<WeeklyReportVcsRow> vcsLogs) {
        this.vcsLogs = vcsLogs == null ? List.of() : vcsLogs;
    }
}
