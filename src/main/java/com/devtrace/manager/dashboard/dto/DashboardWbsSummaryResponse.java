package com.devtrace.manager.dashboard.dto;

import com.devtrace.manager.wbs.dto.WbsTaskStatus;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

public class DashboardWbsSummaryResponse {

    private UUID wbsTaskId;
    private UUID projectId;
    private String projectCode;
    private String projectName;
    private String wbsCode;
    private String taskName;
    private WbsTaskStatus status;
    private LocalDate planEndDate;
    private Integer progressRate;
    private Integer estimatedMinutes;
    private Integer spentMinutes;

    public String getStatusLabel() {
        if (status == null) {
            return "-";
        }
        return switch (status) {
            case READY -> "대기";
            case IN_PROGRESS -> "진행 중";
            case DONE -> "완료";
            case HOLD -> "보류";
        };
    }

    public String getStatusCssClass() {
        return status == null ? "planned" : status.getCssClass();
    }

    public boolean isOverEffort() {
        return safeMinutes(estimatedMinutes) > 0 && safeMinutes(spentMinutes) > safeMinutes(estimatedMinutes);
    }

    public String getEffortText() {
        return formatHours(spentMinutes) + " / " + formatHours(estimatedMinutes);
    }

    private String formatHours(Integer minutes) {
        return String.format(Locale.ROOT, "%.1fh", safeMinutes(minutes) / 60.0);
    }

    private int safeMinutes(Integer minutes) {
        return minutes == null ? 0 : minutes;
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

    public WbsTaskStatus getStatus() {
        return status;
    }

    public void setStatus(WbsTaskStatus status) {
        this.status = status;
    }

    public LocalDate getPlanEndDate() {
        return planEndDate;
    }

    public void setPlanEndDate(LocalDate planEndDate) {
        this.planEndDate = planEndDate;
    }

    public Integer getProgressRate() {
        return progressRate;
    }

    public void setProgressRate(Integer progressRate) {
        this.progressRate = progressRate;
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
