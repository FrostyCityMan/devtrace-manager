package com.devtrace.manager.sprint.dto;

import com.devtrace.manager.testevidence.dto.TestEvidenceResult;
import java.time.LocalDateTime;
import java.util.UUID;

public class SprintTestEvidenceRiskResponse {

    private UUID testEvidenceId;
    private UUID issueId;
    private String issueKey;
    private String testName;
    private String testTarget;
    private TestEvidenceResult resultStatus;
    private String testerName;
    private LocalDateTime testedAt;

    public String getResultLabel() {
        return resultStatus == null ? "-" : resultStatus.getLabel();
    }

    public String getResultCssClass() {
        return resultStatus == null ? "planned" : resultStatus.getCssClass();
    }

    public UUID getTestEvidenceId() {
        return testEvidenceId;
    }

    public void setTestEvidenceId(UUID testEvidenceId) {
        this.testEvidenceId = testEvidenceId;
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
}
