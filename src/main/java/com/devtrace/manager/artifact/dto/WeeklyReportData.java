package com.devtrace.manager.artifact.dto;

import com.devtrace.manager.project.dto.ProjectResponse;
import java.time.LocalDate;
import java.util.List;

public class WeeklyReportData {

    private ProjectResponse project;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<WeeklyReportIssueRow> issues;
    private List<WeeklyReportWorkLogRow> workLogs;
    private List<WeeklyReportVcsRow> vcsLogs;

    public int getEstimatedMinutesTotal() {
        if (issues == null) {
            return 0;
        }
        return issues.stream().mapToInt(issue -> issue.getEstimatedMinutes() == null ? 0 : issue.getEstimatedMinutes()).sum();
    }

    public int getSpentMinutesTotal() {
        if (workLogs == null) {
            return 0;
        }
        return workLogs.stream().mapToInt(workLog -> workLog.getSpentMinutes() == null ? 0 : workLog.getSpentMinutes()).sum();
    }

    public ProjectResponse getProject() {
        return project;
    }

    public void setProject(ProjectResponse project) {
        this.project = project;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<WeeklyReportIssueRow> getIssues() {
        return issues;
    }

    public void setIssues(List<WeeklyReportIssueRow> issues) {
        this.issues = issues;
    }

    public List<WeeklyReportWorkLogRow> getWorkLogs() {
        return workLogs;
    }

    public void setWorkLogs(List<WeeklyReportWorkLogRow> workLogs) {
        this.workLogs = workLogs;
    }

    public List<WeeklyReportVcsRow> getVcsLogs() {
        return vcsLogs;
    }

    public void setVcsLogs(List<WeeklyReportVcsRow> vcsLogs) {
        this.vcsLogs = vcsLogs;
    }
}
