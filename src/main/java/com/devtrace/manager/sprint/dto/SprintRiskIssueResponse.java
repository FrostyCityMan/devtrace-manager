package com.devtrace.manager.sprint.dto;

import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.issue.dto.IssueStatus;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

public class SprintRiskIssueResponse {

    private UUID issueId;
    private String issueKey;
    private String title;
    private IssueStatus status;
    private IssuePriority priority;
    private String assigneeName;
    private LocalDate dueDate;
    private Integer estimatedMinutes;
    private Integer spentMinutes;
    private boolean delayed;
    private boolean highPriority;
    private boolean overEffort;

    public String getStatusLabel() {
        return status == null ? "-" : status.getLabel();
    }

    public String getStatusCssClass() {
        return status == null ? "planned" : status.getCssClass();
    }

    public String getPriorityLabel() {
        return priority == null ? "-" : priority.getLabel();
    }

    public String getPriorityCssClass() {
        return priority == null ? "planned" : priority.getCssClass();
    }

    public String getAssigneeDisplayName() {
        if (assigneeName == null || assigneeName.isBlank()) {
            return "미지정";
        }
        return assigneeName;
    }

    public String getRiskLabel() {
        if (delayed) {
            return "지연";
        }
        if (overEffort) {
            return "공수 초과";
        }
        if (highPriority) {
            return "고우선순위";
        }
        return "주의";
    }

    public String getEffortLabel() {
        return toHoursLabel(spentMinutes) + " / " + toHoursLabel(estimatedMinutes);
    }

    private String toHoursLabel(Integer minutes) {
        int value = minutes == null ? 0 : minutes;
        if (value % 60 == 0) {
            return (value / 60) + "h";
        }
        return String.format(Locale.ROOT, "%.1fh", value / 60.0);
    }

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
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

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
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

    public boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public boolean isOverEffort() {
        return overEffort;
    }

    public void setOverEffort(boolean overEffort) {
        this.overEffort = overEffort;
    }
}
