package com.devtrace.manager.vcs.dto;

import java.util.UUID;

public class VcsChangeFileEntity {

    private UUID changeFileId;
    private UUID changeLogId;
    private String filePath;
    private String changeType;

    /**
     * 변경 파일 엔티티를 화면/API 응답 DTO로 변환합니다.
     *
     * @return 변경 파일 응답 DTO
     */
    public VcsChangeFileResponse toResponse() {
        VcsChangeFileResponse response = new VcsChangeFileResponse();
        response.setChangeFileId(changeFileId);
        response.setChangeLogId(changeLogId);
        response.setFilePath(filePath);
        response.setChangeType(changeType);
        return response;
    }

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
