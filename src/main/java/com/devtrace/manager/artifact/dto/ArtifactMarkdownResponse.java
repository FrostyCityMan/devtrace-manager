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
    private int testCount;
    private int successCount;
    private int failCount;
    private int blockedCount;
    private int screenshotCount;

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

    /**
     * 미리보기 실패 또는 최초 화면 표시 시 사용할 빈 Markdown 응답을 생성합니다.
     *
     * @return 빈 Markdown 응답
     */
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

    public int getTestCount() {
        return testCount;
    }

    public void setTestCount(int testCount) {
        this.testCount = testCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public int getBlockedCount() {
        return blockedCount;
    }

    public void setBlockedCount(int blockedCount) {
        this.blockedCount = blockedCount;
    }

    public int getScreenshotCount() {
        return screenshotCount;
    }

    public void setScreenshotCount(int screenshotCount) {
        this.screenshotCount = screenshotCount;
    }

    public String getEstimatedHoursText() {
        return formatHours(estimatedMinutes);
    }

    public String getSpentHoursText() {
        return formatHours(spentMinutes);
    }

    public String getSuccessRateText() {
        if (testCount == 0) {
            return "0.0%";
        }
        return String.format(Locale.ROOT, "%.1f%%", successCount * 100.0 / testCount);
    }

    private String formatHours(int minutes) {
        return String.format(Locale.KOREA, "%.1fh", minutes / 60.0);
    }
}
