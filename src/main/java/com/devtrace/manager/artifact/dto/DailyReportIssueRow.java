package com.devtrace.manager.artifact.dto;

import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.issue.dto.IssueStatus;
import com.devtrace.manager.issue.dto.IssueType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyReportIssueRow {

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isCompleted() {
        return status != null && status.isCompleted();
    }

    public boolean isActive() {
        return status != null && status.isActive();
    }

    public boolean isRisk() {
        return status == IssueStatus.HOLD || status == IssueStatus.REJECTED;
    }

    public boolean isChangedOn(LocalDate baseDate) {
        if (baseDate == null) {
            return false;
        }
        return isSameDate(createdAt, baseDate)
                || isSameDate(updatedAt, baseDate)
                || baseDate.equals(resolvedDate);
    }

    public boolean isDelayed(LocalDate baseDate) {
        return dueDate != null && baseDate != null && dueDate.isBefore(baseDate) && !isCompleted();
    }

    public boolean isNextWorkTarget(LocalDate baseDate) {
        if (baseDate == null || isCompleted()) {
            return false;
        }
        LocalDate nextDate = baseDate.plusDays(1);
        return (startDate != null && !startDate.isBefore(nextDate))
                || (dueDate != null && !dueDate.isBefore(nextDate));
    }

    private boolean isSameDate(LocalDateTime dateTime, LocalDate date) {
        return dateTime != null && dateTime.toLocalDate().equals(date);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
