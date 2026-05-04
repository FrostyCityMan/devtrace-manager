package com.devtrace.manager.vcs.dao;

import java.util.UUID;

public class VcsIssueKeyRow {

    private UUID changeLogId;
    private String issueKey;

    public UUID getChangeLogId() {
        return changeLogId;
    }

    public void setChangeLogId(UUID changeLogId) {
        this.changeLogId = changeLogId;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }
}
