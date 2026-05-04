package com.devtrace.manager.artifact.dto;

import com.devtrace.manager.vcs.dto.VcsType;
import java.time.LocalDateTime;

public class WeeklyReportVcsRow {

    private VcsType vcsType;
    private String revisionNo;
    private String author;
    private LocalDateTime changedAt;
    private String message;
    private String issueKeyText;

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
