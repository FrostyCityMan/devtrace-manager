package com.devtrace.manager.sprint.dto;

import java.util.Locale;

public class SprintSummaryResponse {

    private int totalIssueCount;
    private int doneIssueCount;
    private int activeIssueCount;
    private int delayedIssueCount;
    private int estimatedMinutes;
    private int spentMinutes;

    public int getProgressRate() {
        if (totalIssueCount == 0) {
            return 0;
        }
        return Math.round((doneIssueCount * 100.0f) / totalIssueCount);
    }

    public String getEstimatedHoursLabel() {
        return toHoursLabel(estimatedMinutes);
    }

    public String getSpentHoursLabel() {
        return toHoursLabel(spentMinutes);
    }

    private String toHoursLabel(int minutes) {
        if (minutes % 60 == 0) {
            return (minutes / 60) + "h";
        }
        return String.format(Locale.ROOT, "%.1fh", minutes / 60.0);
    }

    public int getTotalIssueCount() {
        return totalIssueCount;
    }

    public void setTotalIssueCount(int totalIssueCount) {
        this.totalIssueCount = totalIssueCount;
    }

    public int getDoneIssueCount() {
        return doneIssueCount;
    }

    public void setDoneIssueCount(int doneIssueCount) {
        this.doneIssueCount = doneIssueCount;
    }

    public int getActiveIssueCount() {
        return activeIssueCount;
    }

    public void setActiveIssueCount(int activeIssueCount) {
        this.activeIssueCount = activeIssueCount;
    }

    public int getDelayedIssueCount() {
        return delayedIssueCount;
    }

    public void setDelayedIssueCount(int delayedIssueCount) {
        this.delayedIssueCount = delayedIssueCount;
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
