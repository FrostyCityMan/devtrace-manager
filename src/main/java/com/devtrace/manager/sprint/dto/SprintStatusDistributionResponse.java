package com.devtrace.manager.sprint.dto;

import com.devtrace.manager.issue.dto.IssueStatus;

public class SprintStatusDistributionResponse {

    private IssueStatus status;
    private int issueCount;
    private int issueRate;

    public String getStatusLabel() {
        return status == null ? "-" : status.getLabel();
    }

    public String getStatusCssClass() {
        return status == null ? "planned" : status.getCssClass();
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public int getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(int issueCount) {
        this.issueCount = issueCount;
    }

    public int getIssueRate() {
        return issueRate;
    }

    public void setIssueRate(int issueRate) {
        this.issueRate = issueRate;
    }
}
