package com.devtrace.manager.sprint.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class SprintIssueRequest {

    @NotNull(message = "이슈는 필수입니다.")
    private UUID issueId;

    private Integer displayOrder;

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
