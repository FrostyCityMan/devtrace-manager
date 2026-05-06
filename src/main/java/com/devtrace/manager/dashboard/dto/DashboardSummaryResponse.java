package com.devtrace.manager.dashboard.dto;

public class DashboardSummaryResponse {

    private int totalProjectCount;
    private int activeProjectCount;
    private int totalIssueCount;
    private int delayedIssueCount;
    private int failedTestCount;
    private int overEffortIssueCount;

    public int getTotalProjectCount() {
        return totalProjectCount;
    }

    public void setTotalProjectCount(int totalProjectCount) {
        this.totalProjectCount = totalProjectCount;
    }

    public int getActiveProjectCount() {
        return activeProjectCount;
    }

    public void setActiveProjectCount(int activeProjectCount) {
        this.activeProjectCount = activeProjectCount;
    }

    public int getTotalIssueCount() {
        return totalIssueCount;
    }

    public void setTotalIssueCount(int totalIssueCount) {
        this.totalIssueCount = totalIssueCount;
    }

    public int getDelayedIssueCount() {
        return delayedIssueCount;
    }

    public void setDelayedIssueCount(int delayedIssueCount) {
        this.delayedIssueCount = delayedIssueCount;
    }

    public int getFailedTestCount() {
        return failedTestCount;
    }

    public void setFailedTestCount(int failedTestCount) {
        this.failedTestCount = failedTestCount;
    }

    public int getOverEffortIssueCount() {
        return overEffortIssueCount;
    }

    public void setOverEffortIssueCount(int overEffortIssueCount) {
        this.overEffortIssueCount = overEffortIssueCount;
    }
}
