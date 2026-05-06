package com.devtrace.manager.sprint.dto;

import java.util.UUID;

public class SprintBacklogSearchCondition {

    private UUID projectId;
    private String keyword;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
