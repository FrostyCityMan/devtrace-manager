package com.devtrace.manager.board.dto;

import java.util.UUID;

public class BoardAssigneeResponse {

    private UUID assigneeId;
    private String assigneeName;

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
}
