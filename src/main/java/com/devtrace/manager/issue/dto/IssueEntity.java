package com.devtrace.manager.issue.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class IssueEntity {

    private UUID issueId;
    private UUID projectId;
    private String projectCode;
    private String projectName;
    private String issueKey;
    private IssueType issueType;
    private String title;
    private String description;
    private IssueStatus status;
    private IssuePriority priority;
    private UUID assigneeId;
    private UUID reporterId;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate resolvedDate;
    private Integer estimatedMinutes;
    private Integer spentMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public IssueResponse toResponse() {
        IssueResponse response = new IssueResponse();
        response.setIssueId(issueId);
        response.setProjectId(projectId);
        response.setProjectCode(projectCode);
        response.setProjectName(projectName);
        response.setIssueKey(issueKey);
        response.setIssueType(issueType);
        response.setTitle(title);
        response.setDescription(description);
        response.setStatus(status);
        response.setPriority(priority);
        response.setAssigneeId(assigneeId);
        response.setReporterId(reporterId);
        response.setStartDate(startDate);
        response.setDueDate(dueDate);
        response.setResolvedDate(resolvedDate);
        response.setEstimatedMinutes(estimatedMinutes);
        response.setSpentMinutes(spentMinutes);
        response.setCreatedAt(createdAt);
        response.setUpdatedAt(updatedAt);
        return response;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public UUID getReporterId() {
        return reporterId;
    }

    public void setReporterId(UUID reporterId) {
        this.reporterId = reporterId;
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
