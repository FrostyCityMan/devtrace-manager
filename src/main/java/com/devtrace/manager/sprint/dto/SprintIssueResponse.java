package com.devtrace.manager.sprint.dto;

import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.issue.dto.IssueStatus;
import com.devtrace.manager.issue.dto.IssueType;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

public class SprintIssueResponse {

    private UUID sprintId;
    private UUID issueId;
    private UUID projectId;
    private String projectCode;
    private String issueKey;
    private IssueType issueType;
    private String title;
    private IssueStatus status;
    private IssuePriority priority;
    private UUID assigneeId;
    private String assigneeName;
    private LocalDate dueDate;
    private Integer estimatedMinutes;
    private Integer spentMinutes;
    private Integer displayOrder;
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

    public String getStatusLabel() {
        return status == null ? "-" : status.getLabel();
    }

    public String getStatusCssClass() {
        return status == null ? "planned" : status.getCssClass();
    }

    public String getPriorityLabel() {
        return priority == null ? "-" : priority.getLabel();
    }

    public String getPriorityCssClass() {
        return priority == null ? "planned" : priority.getCssClass();
    }

    private String toHoursLabel(Integer minutes) {
        int value = minutes == null ? 0 : minutes;
        if (value % 60 == 0) {
            return (value / 60) + "h";
        }
        return String.format(Locale.ROOT, "%.1fh", value / 60.0);
    }

    public UUID getSprintId() {
        return sprintId;
    }

    public void setSprintId(UUID sprintId) {
        this.sprintId = sprintId;
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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }
}
