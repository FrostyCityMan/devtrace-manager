package com.devtrace.manager.worklog.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

public class WorkLogResponse {

    private UUID workLogId;
    private UUID issueId;
    private UUID userId;
    private LocalDate workDate;
    private String workContent;
    private Integer spentMinutes;
    private LocalDateTime createdAt;

    public WorkLogRequest toRequest() {
        WorkLogRequest request = new WorkLogRequest();
        request.setIssueId(issueId);
        request.setUserId(userId);
        request.setWorkDate(workDate);
        request.setWorkContent(workContent);
        request.setSpentMinutes(spentMinutes);
        return request;
    }

    public String getSpentHoursLabel() {
        int minutes = spentMinutes == null ? 0 : spentMinutes;
        return String.format(Locale.ROOT, "%.1fh", minutes / 60.0);
    }

    public UUID getWorkLogId() {
        return workLogId;
    }

    public void setWorkLogId(UUID workLogId) {
        this.workLogId = workLogId;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
