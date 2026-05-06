package com.devtrace.manager.dashboard.dto;

import com.devtrace.manager.project.dto.ProjectStatus;
import java.util.Locale;
import java.util.UUID;

public class DashboardProjectHealthResponse {

    private UUID projectId;
    private String projectCode;
    private String projectName;
    private ProjectStatus status;
    private int totalIssueCount;
    private int doneIssueCount;
    private int delayedIssueCount;
    private int estimatedMinutes;
    private int spentMinutes;
    private int testTotalCount;
    private int testSuccessCount;
    private int artifactCount;

    public int getIssueProgressRate() {
        if (totalIssueCount == 0) {
            return 0;
        }
        return Math.round((doneIssueCount * 100.0f) / totalIssueCount);
    }

    public int getTestSuccessRate() {
        if (testTotalCount == 0) {
            return 0;
        }
        return Math.round((testSuccessCount * 100.0f) / testTotalCount);
    }

    public boolean isOverEffort() {
        return estimatedMinutes > 0 && spentMinutes > estimatedMinutes;
    }

    public String getEstimatedHoursText() {
        return formatHours(estimatedMinutes);
    }

    public String getSpentHoursText() {
        return formatHours(spentMinutes);
    }

    public String getStatusLabel() {
        return status == null ? "-" : status.name();
    }

    private String formatHours(int minutes) {
        return String.format(Locale.ROOT, "%.1fh", minutes / 60.0);
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

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
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

    public int getTestTotalCount() {
        return testTotalCount;
    }

    public void setTestTotalCount(int testTotalCount) {
        this.testTotalCount = testTotalCount;
    }

    public int getTestSuccessCount() {
        return testSuccessCount;
    }

    public void setTestSuccessCount(int testSuccessCount) {
        this.testSuccessCount = testSuccessCount;
    }

    public int getArtifactCount() {
        return artifactCount;
    }

    public void setArtifactCount(int artifactCount) {
        this.artifactCount = artifactCount;
    }
}
