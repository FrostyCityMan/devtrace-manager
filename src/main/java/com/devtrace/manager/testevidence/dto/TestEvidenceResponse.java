package com.devtrace.manager.testevidence.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class TestEvidenceResponse {

    private UUID testEvidenceId;
    private UUID projectId;
    private String projectCode;
    private String projectName;
    private UUID issueId;
    private String issueKey;
    private String issueTitle;
    private String testName;
    private String testTarget;
    private String testProcedure;
    private String expectedResult;
    private String actualResult;
    private TestEvidenceResult resultStatus;
    private UUID testerId;
    private String testerName;
    private LocalDateTime testedAt;
    private String screenshotFileName;
    private String screenshotFilePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TestEvidenceRequest toRequest() {
        TestEvidenceRequest request = new TestEvidenceRequest();
        request.setProjectId(projectId);
        request.setIssueId(issueId);
        request.setTestName(testName);
        request.setTestTarget(testTarget);
        request.setTestProcedure(testProcedure);
        request.setExpectedResult(expectedResult);
        request.setActualResult(actualResult);
        request.setResultStatus(resultStatus);
        request.setTesterId(testerId);
        request.setTestedAt(testedAt);
        return request;
    }

    public boolean isHasScreenshot() {
        return screenshotFilePath != null && !screenshotFilePath.isBlank();
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

    public String getTestProcedure() {
        return testProcedure;
    }

    public void setTestProcedure(String testProcedure) {
        this.testProcedure = testProcedure;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getActualResult() {
        return actualResult;
    }

    public void setActualResult(String actualResult) {
        this.actualResult = actualResult;
    }

    public TestEvidenceResult getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(TestEvidenceResult resultStatus) {
        this.resultStatus = resultStatus;
    }

    public UUID getTesterId() {
        return testerId;
    }

    public void setTesterId(UUID testerId) {
        this.testerId = testerId;
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

    public String getScreenshotFilePath() {
        return screenshotFilePath;
    }

    public void setScreenshotFilePath(String screenshotFilePath) {
        this.screenshotFilePath = screenshotFilePath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
