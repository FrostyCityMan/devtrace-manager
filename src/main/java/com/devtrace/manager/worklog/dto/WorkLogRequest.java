package com.devtrace.manager.worklog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.UUID;

public class WorkLogRequest {

    public static final UUID DEFAULT_ADMIN_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @NotNull(message = "이슈는 필수입니다.")
    private UUID issueId;

    @NotNull(message = "사용자는 필수입니다.")
    private UUID userId = DEFAULT_ADMIN_USER_ID;

    @NotNull(message = "작업일자는 필수입니다.")
    private LocalDate workDate = LocalDate.now();

    @NotBlank(message = "작업 내용은 필수입니다.")
    private String workContent;

    @Positive(message = "소요 시간은 1분 이상이어야 합니다.")
    private Integer spentMinutes;

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public String getWorkContent() {
        return workContent;
    }

    public void setWorkContent(String workContent) {
        this.workContent = workContent;
    }

    public Integer getSpentMinutes() {
        return spentMinutes;
    }

    public void setSpentMinutes(Integer spentMinutes) {
        this.spentMinutes = spentMinutes;
    }
}
