package com.devtrace.manager.dashboard.dto;

import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.issue.dto.IssueStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

public class DashboardRiskIssueResponse {

    private UUID issueId;
    private UUID projectId;
    private String projectCode;
    private String projectName;
    private String issueKey;
    private String title;
    private IssueStatus status;
    private IssuePriority priority;
    private LocalDate dueDate;
    private Integer estimatedMinutes;
    private Integer spentMinutes;
    private LocalDateTime updatedAt;

    public String getStatusLabel() {
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

    public String getStatusCssClass() {
        return status == null ? "planned" : status.getCssClass();
    }

    public String getPriorityLabel() {
        if (priority == null) {
            return "-";
        }
        return switch (priority) {
            case URGENT -> "긴급";
            case HIGH -> "높음";
            case NORMAL -> "보통";
            case LOW -> "낮음";
        };
    }

    public String getPriorityCssClass() {
        return priority == null ? "planned" : priority.getCssClass();
    }

    public String getPriorityRowClass() {
        return priority == null ? "stripe-teal" : priority.getRowClass();
    }

    public boolean isOverEffort() {
        return safeMinutes(estimatedMinutes) > 0 && safeMinutes(spentMinutes) > safeMinutes(estimatedMinutes);
    }

    public String getEffortText() {
        return formatHours(spentMinutes) + " / " + formatHours(estimatedMinutes);
    }

    private String formatHours(Integer minutes) {
        return String.format(Locale.ROOT, "%.1fh", safeMinutes(minutes) / 60.0);
    }

    private int safeMinutes(Integer minutes) {
        return minutes == null ? 0 : minutes;
    }

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public void setPriority(IssuePriority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public Integer getSpentMinutes() {
        return spentMinutes;
    }

    public void setSpentMinutes(Integer spentMinutes) {
        this.spentMinutes = spentMinutes;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
