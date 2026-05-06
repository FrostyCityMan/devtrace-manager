package com.devtrace.manager.artifact.dto;

import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.testevidence.dto.TestEvidenceResult;
import java.time.LocalDate;
import java.util.List;

public class TestResultReportData {

    private ProjectResponse project;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TestResultEvidenceRow> evidences = List.of();

    public int getTotalCount() {
        return evidences.size();
    }

    public long getSuccessCount() {
        return countByResult(TestEvidenceResult.SUCCESS);
    }

    public long getFailCount() {
        return countByResult(TestEvidenceResult.FAIL);
    }

    public long getBlockedCount() {
        return countByResult(TestEvidenceResult.BLOCKED);
    }

    public long getScreenshotCount() {
        return evidences.stream().filter(TestResultEvidenceRow::hasScreenshot).count();
    }

    public double getSuccessRate() {
        return evidences.isEmpty() ? 0.0 : getSuccessCount() * 100.0 / evidences.size();
    }

    private long countByResult(TestEvidenceResult result) {
        return evidences.stream()
                .filter(evidence -> evidence.getResultStatus() == result)
                .count();
    }

    public ProjectResponse getProject() {
        return project;
    }

    public void setProject(ProjectResponse project) {
        this.project = project;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<TestResultEvidenceRow> getEvidences() {
        return evidences;
    }

    public void setEvidences(List<TestResultEvidenceRow> evidences) {
        this.evidences = evidences == null ? List.of() : evidences;
    }
}
