package com.devtrace.manager.issue.dto;

import jakarta.validation.constraints.NotNull;

public class IssueStatusUpdateRequest {

    @NotNull(message = "이슈 상태는 필수입니다.")
    private IssueStatus status;

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }
}
