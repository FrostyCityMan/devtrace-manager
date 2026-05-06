package com.devtrace.manager.board.dto;

import com.devtrace.manager.issue.dto.IssueStatus;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class BoardStatusUpdateRequest {

    @NotNull(message = "issueId is required.")
    private UUID issueId;

    @NotNull(message = "status is required.")
    private IssueStatus status;

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }
}
