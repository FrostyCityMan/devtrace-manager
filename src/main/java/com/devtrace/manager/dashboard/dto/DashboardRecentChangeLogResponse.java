package com.devtrace.manager.dashboard.dto;

import com.devtrace.manager.vcs.dto.VcsType;
import java.time.LocalDateTime;
import java.util.UUID;

public class DashboardRecentChangeLogResponse {

    private UUID changeLogId;
    private UUID projectId;
    private String projectCode;
    private VcsType vcsType;
    private String revisionNo;
    private String author;
    private LocalDateTime changedAt;
    private String message;
    private String issueKeyText;

    public UUID getChangeLogId() {
        return changeLogId;
    }

    public void setChangeLogId(UUID changeLogId) {
        this.changeLogId = changeLogId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public VcsType getVcsType() {
        return vcsType;
    }

    public void setVcsType(VcsType vcsType) {
        this.vcsType = vcsType;
    }

    public String getRevisionNo() {
        return revisionNo;
    }

    public void setRevisionNo(String revisionNo) {
        this.revisionNo = revisionNo;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIssueKeyText() {
        return issueKeyText;
    }

    public void setIssueKeyText(String issueKeyText) {
        this.issueKeyText = issueKeyText;
    }
}
