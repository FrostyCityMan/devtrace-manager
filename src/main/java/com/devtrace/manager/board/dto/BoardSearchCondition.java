package com.devtrace.manager.board.dto;

import com.devtrace.manager.issue.dto.IssuePriority;
import java.util.UUID;

public class BoardSearchCondition {

    private UUID projectId;
    private UUID assigneeId;
    private IssuePriority priority;
    private String keyword;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(UUID assigneeId) {
        this.assigneeId = assigneeId;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public void setPriority(IssuePriority priority) {
        this.priority = priority;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
