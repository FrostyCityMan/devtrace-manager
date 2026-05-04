package com.devtrace.manager.vcs.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VcsChangeLogEntity {

    private UUID changeLogId;
    private UUID projectId;
    private VcsType vcsType;
    private String revisionNo;
    private String author;
    private LocalDateTime changedAt;
    private String message;
    private LocalDateTime createdAt;
    private List<VcsChangeFileEntity> changedFiles = new ArrayList<>();
    private List<String> issueKeys = new ArrayList<>();

    public VcsChangeLogResponse toResponse() {
        VcsChangeLogResponse response = new VcsChangeLogResponse();
        response.setChangeLogId(changeLogId);
        response.setProjectId(projectId);
        response.setVcsType(vcsType);
        response.setRevisionNo(revisionNo);
        response.setAuthor(author);
        response.setChangedAt(changedAt);
        response.setMessage(message);
        response.setCreatedAt(createdAt);
        response.setChangedFiles(changedFiles.stream().map(VcsChangeFileEntity::toResponse).toList());
        response.setIssueKeys(issueKeys);
        return response;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<VcsChangeFileEntity> getChangedFiles() {
        return changedFiles;
    }

    public void setChangedFiles(List<VcsChangeFileEntity> changedFiles) {
        this.changedFiles = changedFiles;
    }

    public List<String> getIssueKeys() {
        return issueKeys;
    }

    public void setIssueKeys(List<String> issueKeys) {
        this.issueKeys = issueKeys;
    }
}
