package com.devtrace.manager.sprint.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SprintIssueOrderRequest {

    @NotNull(message = "표시 순서는 필수입니다.")
    @Min(value = 0, message = "표시 순서는 0 이상이어야 합니다.")
    private Integer displayOrder;

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
