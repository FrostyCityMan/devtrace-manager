package com.devtrace.manager.wbs.dto;

import java.util.UUID;

public class WbsTaskDependencySearchCondition {

    private UUID projectId;
    private UUID wbsTaskId;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getWbsTaskId() {
        return wbsTaskId;
    }

    public void setWbsTaskId(UUID wbsTaskId) {
        this.wbsTaskId = wbsTaskId;
    }
}
