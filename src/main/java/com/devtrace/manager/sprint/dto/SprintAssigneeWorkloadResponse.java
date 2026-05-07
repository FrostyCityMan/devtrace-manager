package com.devtrace.manager.sprint.dto;

import java.util.Locale;
import java.util.UUID;

public class SprintAssigneeWorkloadResponse {

    private UUID assigneeId;
    private String assigneeName;
    private int issueCount;
    private int estimatedMinutes;
    private int spentMinutes;

    public String getAssigneeDisplayName() {
        if (assigneeName == null || assigneeName.isBlank()) {
            return "미지정";
        }
        return assigneeName;
    }

    public String getEstimatedHoursLabel() {
        return toHoursLabel(estimatedMinutes);
    }

    public String getSpentHoursLabel() {
        return toHoursLabel(spentMinutes);
    }

    public boolean isOverEffort() {
        return estimatedMinutes > 0 && spentMinutes > estimatedMinutes;
    }

    private String toHoursLabel(int minutes) {
        if (minutes % 60 == 0) {
            return (minutes / 60) + "h";
        }
        return String.format(Locale.ROOT, "%.1fh", minutes / 60.0);
    }

    public UUID getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(UUID assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public int getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(int issueCount) {
        this.issueCount = issueCount;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public int getSpentMinutes() {
        return spentMinutes;
    }

    public void setSpentMinutes(int spentMinutes) {
        this.spentMinutes = spentMinutes;
    }
}
