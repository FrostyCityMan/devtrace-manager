package com.devtrace.manager.issue.dto;

import java.time.LocalDate;
import java.util.UUID;

public class IssueSearchCondition {

    private UUID projectId;
    private IssueStatus status;
    private IssuePriority priority;
    private UUID assigneeId;
    private LocalDate dueDateFrom;
    private LocalDate dueDateTo;
    private String keyword;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
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

    public LocalDate getDueDateFrom() {
        return dueDateFrom;
    }

    public void setDueDateFrom(LocalDate dueDateFrom) {
        this.dueDateFrom = dueDateFrom;
    }

    public LocalDate getDueDateTo() {
        return dueDateTo;
    }

    public void setDueDateTo(LocalDate dueDateTo) {
        this.dueDateTo = dueDateTo;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
