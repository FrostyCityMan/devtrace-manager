package com.devtrace.manager.wbs.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class WbsTaskResponse {

    private UUID wbsTaskId;
    private UUID projectId;
    private String projectCode;
    private String projectName;
    private UUID parentTaskId;
    private UUID issueId;
    private String issueKey;
    private String issueTitle;
    private String wbsCode;
    private String taskName;
    private String taskDescription;
    private WbsTaskType taskType;
    private WbsTaskStatus status;
    private UUID assigneeId;
    private String assigneeName;
    private LocalDate planStartDate;
    private LocalDate planEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private Integer estimatedMinutes;
    private Integer spentMinutes;
    private Integer progressRate;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WbsTaskRequest toRequest() {
        WbsTaskRequest request = new WbsTaskRequest();
        request.setProjectId(projectId);
        request.setParentTaskId(parentTaskId);
        request.setIssueId(issueId);
        request.setTaskName(taskName);
        request.setTaskDescription(taskDescription);
        request.setTaskType(taskType);
        request.setStatus(status);
        request.setAssigneeId(assigneeId);
        request.setPlanStartDate(planStartDate);
        request.setPlanEndDate(planEndDate);
        request.setActualStartDate(actualStartDate);
        request.setActualEndDate(actualEndDate);
        request.setEstimatedMinutes(estimatedMinutes);
        request.setSpentMinutes(spentMinutes);
        request.setProgressRate(progressRate);
        return request;
    }

    public int getDepth() {
        if (wbsCode == null || wbsCode.isBlank()) {
            return 0;
        }
        return wbsCode.split("\\.").length - 1;
    }

    public int getIndentPx() {
        return getDepth() * 22;
    }

    public String getEstimatedHoursText() {
        return minutesToHours(estimatedMinutes);
    }

    public String getSpentHoursText() {
        return minutesToHours(spentMinutes);
    }

    private String minutesToHours(Integer minutes) {
        int value = minutes == null ? 0 : minutes;
        return String.format(java.util.Locale.ROOT, "%.1fh", value / 60.0);
    }

    public UUID getWbsTaskId() {
        return wbsTaskId;
    }

    public void setWbsTaskId(UUID wbsTaskId) {
        this.wbsTaskId = wbsTaskId;
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

    public UUID getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(UUID parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public String getIssueTitle() {
        return issueTitle;
    }

    public void setIssueTitle(String issueTitle) {
        this.issueTitle = issueTitle;
    }

    public String getWbsCode() {
        return wbsCode;
    }

    public void setWbsCode(String wbsCode) {
        this.wbsCode = wbsCode;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public WbsTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(WbsTaskType taskType) {
        this.taskType = taskType;
    }

    public WbsTaskStatus getStatus() {
        return status;
    }

    public void setStatus(WbsTaskStatus status) {
        this.status = status;
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

    public LocalDate getPlanStartDate() {
        return planStartDate;
    }

    public void setPlanStartDate(LocalDate planStartDate) {
        this.planStartDate = planStartDate;
    }

    public LocalDate getPlanEndDate() {
        return planEndDate;
    }

    public void setPlanEndDate(LocalDate planEndDate) {
        this.planEndDate = planEndDate;
    }

    public LocalDate getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(LocalDate actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public LocalDate getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(LocalDate actualEndDate) {
        this.actualEndDate = actualEndDate;
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

    public Integer getProgressRate() {
        return progressRate;
    }

    public void setProgressRate(Integer progressRate) {
        this.progressRate = progressRate;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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
