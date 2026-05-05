package com.devtrace.manager.wbs.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

public class WbsTaskRequest {

    public static final UUID DEFAULT_ADMIN_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @NotNull(message = "프로젝트는 필수입니다.")
    private UUID projectId;

    private UUID parentTaskId;
    private UUID issueId;

    @NotBlank(message = "작업명은 필수입니다.")
    @Size(max = 300, message = "작업명은 300자 이하로 입력하십시오.")
    private String taskName;

    private String taskDescription;

    @NotNull(message = "작업 유형은 필수입니다.")
    private WbsTaskType taskType = WbsTaskType.TASK;

    @NotNull(message = "상태는 필수입니다.")
    private WbsTaskStatus status = WbsTaskStatus.READY;

    private UUID assigneeId = DEFAULT_ADMIN_USER_ID;

    @NotNull(message = "계획 시작일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate planStartDate;

    @NotNull(message = "계획 종료일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate planEndDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate actualStartDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate actualEndDate;

    @Min(value = 0, message = "예상 공수는 0 이상이어야 합니다.")
    private Integer estimatedMinutes = 0;

    @Min(value = 0, message = "실제 공수는 0 이상이어야 합니다.")
    private Integer spentMinutes = 0;

    @Min(value = 0, message = "진행률은 0 이상이어야 합니다.")
    @Max(value = 100, message = "진행률은 100 이하이어야 합니다.")
    private Integer progressRate = 0;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
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
}
