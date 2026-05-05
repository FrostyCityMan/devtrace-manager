package com.devtrace.manager.testevidence.dto;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

public class TestEvidenceSearchCondition {

    private UUID projectId;
    private UUID issueId;
    private TestEvidenceResult resultStatus;
    private String keyword;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate testedAtFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate testedAtTo;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    public TestEvidenceResult getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(TestEvidenceResult resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public LocalDate getTestedAtFrom() {
        return testedAtFrom;
    }

    public void setTestedAtFrom(LocalDate testedAtFrom) {
        this.testedAtFrom = testedAtFrom;
    }

    public LocalDate getTestedAtTo() {
        return testedAtTo;
    }

    public void setTestedAtTo(LocalDate testedAtTo) {
        this.testedAtTo = testedAtTo;
    }
}
