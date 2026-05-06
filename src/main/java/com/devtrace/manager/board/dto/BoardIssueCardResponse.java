package com.devtrace.manager.board.dto;

import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.issue.dto.IssueStatus;
import com.devtrace.manager.issue.dto.IssueType;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

public class BoardIssueCardResponse {

    private UUID issueId;
    private UUID projectId;
    private String projectCode;
    private String projectName;
    private String issueKey;
    private String title;
    private IssueType issueType;
    private IssueStatus status;
    private IssuePriority priority;
    private UUID assigneeId;
    private String assigneeName;
    private LocalDate dueDate;
    private Integer estimatedMinutes;
    private Integer spentMinutes;
    private boolean delayed;

    public String getAssigneeDisplayName() {
        if (assigneeName == null || assigneeName.isBlank()) {
            return "미지정";
        }
        return assigneeName;
    }

    public String getEstimatedHoursLabel() {
        return toHoursLabel(estimatedMinutes);
    }

    public String getSpentHoursLabel() {
        return toHoursLabel(spentMinutes);
    }

    private String toHoursLabel(Integer minutes) {
        int value = minutes == null ? 0 : minutes;
        if (value % 60 == 0) {
            return (value / 60) + "h";
        }
        return String.format(Locale.ROOT, "%.1fh", value / 60.0);
    }

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
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

    public UUID getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(UUID assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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

    public boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }
}
