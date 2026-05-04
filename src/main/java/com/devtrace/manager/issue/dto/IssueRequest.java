package com.devtrace.manager.issue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public class IssueRequest {

    @NotNull(message = "프로젝트는 필수입니다.")
    private UUID projectId;

    @NotBlank(message = "이슈 키는 필수입니다.")
    @Size(max = 50, message = "이슈 키는 50자 이하여야 합니다.")
    private String issueKey;

    @NotNull(message = "이슈 유형은 필수입니다.")
    private IssueType issueType = IssueType.FEATURE;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 500, message = "제목은 500자 이하여야 합니다.")
    private String title;

    private String description;

    @NotNull(message = "상태는 필수입니다.")
    private IssueStatus status = IssueStatus.REGISTERED;

    @NotNull(message = "우선순위는 필수입니다.")
    private IssuePriority priority = IssuePriority.NORMAL;

    private UUID assigneeId;
    private UUID reporterId;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate resolvedDate;

    @PositiveOrZero(message = "예상 공수는 0 이상이어야 합니다.")
    private Integer estimatedMinutes = 0;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
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
}
