package com.devtrace.manager.board.dto;

import com.devtrace.manager.issue.dto.IssueStatus;
import java.util.List;

public class BoardColumnResponse {

    private IssueStatus status;
    private String statusLabel;
    private int issueCount;
    private List<BoardIssueCardResponse> issues = List.of();

    public BoardColumnResponse() {
    }

    public BoardColumnResponse(IssueStatus status, List<BoardIssueCardResponse> issues) {
        this.status = status;
        this.statusLabel = status.getLabel();
        this.issues = issues;
        this.issueCount = issues.size();
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public int getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(int issueCount) {
        this.issueCount = issueCount;
    }

    public List<BoardIssueCardResponse> getIssues() {
        return issues;
    }

    public void setIssues(List<BoardIssueCardResponse> issues) {
        this.issues = issues;
    }
}
