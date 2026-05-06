package com.devtrace.manager.dashboard.dto;

import com.devtrace.manager.testevidence.dto.TestEvidenceResult;
import java.time.LocalDateTime;
import java.util.UUID;

public class DashboardTestEvidenceSummaryResponse {

    private UUID testEvidenceId;
    private UUID projectId;
    private String projectCode;
    private String projectName;
    private String issueKey;
    private String issueTitle;
    private String testName;
    private String testTarget;
    private TestEvidenceResult resultStatus;
    private String testerName;
    private LocalDateTime testedAt;
    private String screenshotFileName;

    public String getResultLabel() {
        if (resultStatus == null) {
            return "-";
        }
        return switch (resultStatus) {
            case SUCCESS -> "성공";
            case FAIL -> "실패";
            case BLOCKED -> "차단";
        };
    }

    public String getResultCssClass() {
        return resultStatus == null ? "planned" : resultStatus.getCssClass();
    }

    public boolean isHasScreenshot() {
        return screenshotFileName != null && !screenshotFileName.isBlank();
    }

    public UUID getTestEvidenceId() {
        return testEvidenceId;
    }

    public void setTestEvidenceId(UUID testEvidenceId) {
        this.testEvidenceId = testEvidenceId;
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

    public String getIssueTitle() {
        return issueTitle;
    }

    public void setIssueTitle(String issueTitle) {
        this.issueTitle = issueTitle;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestTarget() {
        return testTarget;
    }

    public void setTestTarget(String testTarget) {
        this.testTarget = testTarget;
    }

    public TestEvidenceResult getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(TestEvidenceResult resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getTesterName() {
        return testerName;
    }

    public void setTesterName(String testerName) {
        this.testerName = testerName;
    }

    public LocalDateTime getTestedAt() {
        return testedAt;
    }

    public void setTestedAt(LocalDateTime testedAt) {
        this.testedAt = testedAt;
    }

    public String getScreenshotFileName() {
        return screenshotFileName;
    }

    public void setScreenshotFileName(String screenshotFileName) {
        this.screenshotFileName = screenshotFileName;
    }
}
