package com.devtrace.manager.wbs.dto;

import java.util.UUID;

public class WbsTaskSearchCondition {

    private UUID projectId;
    private UUID parentTaskId;
    private WbsTaskStatus status;
    private WbsTaskType taskType;
    private String keyword;

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

    public WbsTaskStatus getStatus() {
        return status;
    }

    public void setStatus(WbsTaskStatus status) {
        this.status = status;
    }

    public WbsTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(WbsTaskType taskType) {
        this.taskType = taskType;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
