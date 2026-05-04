package com.devtrace.manager.worklog.dto;

import java.util.UUID;

public class WorkLogSearchCondition {

    private UUID issueId;
    private UUID userId;

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
