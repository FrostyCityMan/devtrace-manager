package com.devtrace.manager.vcs.dto;

import java.util.UUID;

public class VcsChangeFileResponse {

    private UUID changeFileId;
    private UUID changeLogId;
    private String filePath;
    private String changeType;

    public UUID getChangeFileId() {
        return changeFileId;
    }

    public void setChangeFileId(UUID changeFileId) {
        this.changeFileId = changeFileId;
    }

    public UUID getChangeLogId() {
        return changeLogId;
    }

    public void setChangeLogId(UUID changeLogId) {
        this.changeLogId = changeLogId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }
}
