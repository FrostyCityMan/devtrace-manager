package com.devtrace.manager.artifact.dto;

import java.util.Locale;

public class ArtifactMarkdownResponse {

    private String fileName;
    private String content;
    private int issueCount;
    private int workLogCount;
    private int vcsLogCount;
    private int estimatedMinutes;
    private int spentMinutes;

    public ArtifactMarkdownResponse(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
    }

    public ArtifactMarkdownResponse(
            String fileName,
            String content,
            int issueCount,
            int workLogCount,
            int vcsLogCount,
            int estimatedMinutes,
            int spentMinutes
    ) {
        this.fileName = fileName;
        this.content = content;
        this.issueCount = issueCount;
        this.workLogCount = workLogCount;
        this.vcsLogCount = vcsLogCount;
        this.estimatedMinutes = estimatedMinutes;
        this.spentMinutes = spentMinutes;
    }

    public static ArtifactMarkdownResponse empty() {
        return new ArtifactMarkdownResponse("", "");
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(int issueCount) {
        this.issueCount = issueCount;
    }

    public int getWorkLogCount() {
        return workLogCount;
    }

    public void setWorkLogCount(int workLogCount) {
        this.workLogCount = workLogCount;
    }

    public int getVcsLogCount() {
        return vcsLogCount;
    }

    public void setVcsLogCount(int vcsLogCount) {
        this.vcsLogCount = vcsLogCount;
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

    public String getEstimatedHoursText() {
        return formatHours(estimatedMinutes);
    }

    public String getSpentHoursText() {
        return formatHours(spentMinutes);
    }

    private String formatHours(int minutes) {
        return String.format(Locale.KOREA, "%.1fh", minutes / 60.0);
    }
}
