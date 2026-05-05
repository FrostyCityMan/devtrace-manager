package com.devtrace.manager.wbs.dto;

import java.time.LocalDate;
import java.util.UUID;

public class WbsGanttTaskResponse {

    private UUID wbsTaskId;
    private UUID parentTaskId;
    private String wbsCode;
    private String taskName;
    private WbsTaskType taskType;
    private WbsTaskStatus status;
    private LocalDate planStartDate;
    private LocalDate planEndDate;
    private Integer estimatedMinutes;
    private Integer spentMinutes;
    private Integer progressRate;
    private int depth;
    private boolean delayed;
    private boolean overEffort;
    private boolean predecessorBlocked;
    private double leftPercent;
    private double widthPercent;

    public static WbsGanttTaskResponse from(WbsTaskResponse task) {
        WbsGanttTaskResponse response = new WbsGanttTaskResponse();
        response.setWbsTaskId(task.getWbsTaskId());
        response.setParentTaskId(task.getParentTaskId());
        response.setWbsCode(task.getWbsCode());
        response.setTaskName(task.getTaskName());
        response.setTaskType(task.getTaskType());
        response.setStatus(task.getStatus());
        response.setPlanStartDate(task.getPlanStartDate());
        response.setPlanEndDate(task.getPlanEndDate());
        response.setEstimatedMinutes(task.getEstimatedMinutes());
        response.setSpentMinutes(task.getSpentMinutes());
        response.setProgressRate(task.getProgressRate());
        response.setDepth(task.getDepth());
        return response;
    }

    public int getIndentPx() {
        return depth * 22;
    }

    public String getRiskClass() {
        if (predecessorBlocked) {
            return "blocked";
        }
        if (delayed || overEffort) {
            return "danger";
        }
        if (status != null && status.isCompleted()) {
            return "done";
        }
        return "progress";
    }

    public UUID getWbsTaskId() {
        return wbsTaskId;
    }

    public void setWbsTaskId(UUID wbsTaskId) {
        this.wbsTaskId = wbsTaskId;
    }

    public UUID getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(UUID parentTaskId) {
        this.parentTaskId = parentTaskId;
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

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }

    public boolean isOverEffort() {
        return overEffort;
    }

    public void setOverEffort(boolean overEffort) {
        this.overEffort = overEffort;
    }

    public boolean isPredecessorBlocked() {
        return predecessorBlocked;
    }

    public void setPredecessorBlocked(boolean predecessorBlocked) {
        this.predecessorBlocked = predecessorBlocked;
    }

    public double getLeftPercent() {
        return leftPercent;
    }

    public void setLeftPercent(double leftPercent) {
        this.leftPercent = leftPercent;
    }

    public double getWidthPercent() {
        return widthPercent;
    }

    public void setWidthPercent(double widthPercent) {
        this.widthPercent = widthPercent;
    }
}
