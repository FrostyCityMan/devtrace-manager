package com.devtrace.manager.worklog.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class WorkLogEntity {

    private UUID workLogId;
    private UUID issueId;
    private UUID userId;
    private LocalDate workDate;
    private String workContent;
    private Integer spentMinutes;
    private LocalDateTime createdAt;

    /**
     * 작업 공수 엔티티를 화면/API 응답 DTO로 변환합니다.
     *
     * @return 작업 공수 응답 DTO
     */
    public WorkLogResponse toResponse() {
        WorkLogResponse response = new WorkLogResponse();
        response.setWorkLogId(workLogId);
        response.setIssueId(issueId);
        response.setUserId(userId);
        response.setWorkDate(workDate);
        response.setWorkContent(workContent);
        response.setSpentMinutes(spentMinutes);
        response.setCreatedAt(createdAt);
        return response;
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
