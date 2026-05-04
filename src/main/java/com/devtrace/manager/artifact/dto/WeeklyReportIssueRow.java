package com.devtrace.manager.artifact.dto;

import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.issue.dto.IssueStatus;
import com.devtrace.manager.issue.dto.IssueType;
import java.time.LocalDate;

public class WeeklyReportIssueRow {

    private String issueKey;
    private IssueType issueType;
    private String title;
    private IssueStatus status;
    private IssuePriority priority;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate resolvedDate;
    private Integer estimatedMinutes;
    private Integer spentMinutes;

    public boolean isCompleted() {
        return status != null && status.isCompleted();
    }

    public boolean isActive() {
        return status != null && status.isActive();
    }

    public boolean isHold() {
        return status == IssueStatus.HOLD;
    }

    public boolean isDelayed(LocalDate baseDate) {
        return dueDate != null && baseDate != null && dueDate.isBefore(baseDate) && !isCompleted();
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public void setPriority(IssuePriority priority) {
        this.priority = priority;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(LocalDate resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public Integer getSpentMinutes() {
        return spentMinutes;
    }

    public void setSpentMinutes(Integer spentMinutes) {
        this.spentMinutes = spentMinutes;
    }
}
