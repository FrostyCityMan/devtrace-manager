package com.devtrace.manager.dashboard.dto;

import com.devtrace.manager.issue.dto.IssueStatus;

public class DashboardBoardSummaryResponse {

    private IssueStatus status;
    private String statusLabel;
    private int issueCount;

    public DashboardBoardSummaryResponse() {
    }

    public DashboardBoardSummaryResponse(IssueStatus status, int issueCount) {
        this.status = status;
        this.statusLabel = statusLabel(status);
        this.issueCount = issueCount;
    }

    private String statusLabel(IssueStatus status) {
        if (status == null) {
            return "-";
        }
        return switch (status) {
            case REGISTERED -> "등록";
            case ANALYZING -> "분석 중";
            case IN_PROGRESS -> "진행 중";
            case DEV_DONE -> "개발 완료";
            case REVIEWING -> "검토 중";
            case TESTING -> "테스트 중";
            case REJECTED -> "반려";
            case DONE -> "완료";
            case HOLD -> "보류";
            case CLOSED -> "종료";
        };
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
        this.statusLabel = statusLabel(status);
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
}
