package com.devtrace.manager.vcs.dao;

import java.util.UUID;

/**
 * 변경이력과 이슈 키 매핑 조회를 위한 내부 행 DTO입니다.
 */
public class VcsIssueKeyRow {

    private UUID changeLogId;
    private String issueKey;

    /**
     * 변경이력 ID를 반환합니다.
     *
     * @return 변경이력 ID
     */
    public UUID getChangeLogId() {
        return changeLogId;
    }

    /**
     * 변경이력 ID를 설정합니다.
     *
     * @param changeLogId 변경이력 ID
     */
    public void setChangeLogId(UUID changeLogId) {
        this.changeLogId = changeLogId;
    }

    /**
     * 이슈 키를 반환합니다.
     *
     * @return 이슈 키
     */
    public String getIssueKey() {
        return issueKey;
    }

    /**
     * 이슈 키를 설정합니다.
     *
     * @param issueKey 이슈 키
     */
    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }
}
