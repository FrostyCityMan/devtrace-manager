package com.devtrace.manager.sprint.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class SprintReportResponse {

    private SprintResponse sprint;
    private SprintSummaryResponse summary = new SprintSummaryResponse();
    private List<SprintBurndownPointResponse> burndownPoints = List.of();
    private List<SprintStatusDistributionResponse> statusDistributions = List.of();
    private List<SprintAssigneeWorkloadResponse> assigneeWorkloads = List.of();
    private List<SprintRiskIssueResponse> riskIssues = List.of();
    private List<SprintTestEvidenceRiskResponse> failedTestEvidences = List.of();

    public int getRemainingIssueCount() {
        return Math.max(0, summary.getTotalIssueCount() - summary.getDoneIssueCount());
    }

    public int getElapsedRate() {
        if (sprint == null || sprint.getStartDate() == null || sprint.getEndDate() == null) {
            return 0;
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(sprint.getStartDate())) {
            return 0;
        }
        long totalDays = Math.max(1, ChronoUnit.DAYS.between(sprint.getStartDate(), sprint.getEndDate()) + 1);
        long elapsedDays = ChronoUnit.DAYS.between(sprint.getStartDate(), today) + 1;
        elapsedDays = Math.max(0, Math.min(totalDays, elapsedDays));
        return Math.round((elapsedDays * 100.0f) / totalDays);
    }

    public String getForecastLabel() {
        if (summary.getTotalIssueCount() == 0) {
            return "데이터 없음";
        }
        return summary.getProgressRate() >= getElapsedRate() ? "정상" : "주의";
    }

    public String getForecastCssClass() {
        if (summary.getTotalIssueCount() == 0) {
            return "planned";
        }
        return "정상".equals(getForecastLabel()) ? "done" : "danger";
    }

    public String getIdealPolylinePoints() {
        return burndownPoints.stream()
                .map(point -> point.getXPercent() + "," + point.getIdealYPercent())
                .collect(Collectors.joining(" "));
    }

    public String getActualPolylinePoints() {
        return burndownPoints.stream()
                .filter(point -> point.getActualRemainingMinutes() != null && point.getActualYPercent() != null)
                .map(point -> point.getXPercent() + "," + point.getActualYPercent())
                .collect(Collectors.joining(" "));
    }

    public SprintResponse getSprint() {
        return sprint;
    }

    public void setSprint(SprintResponse sprint) {
        this.sprint = sprint;
    }

    public SprintSummaryResponse getSummary() {
        return summary;
    }

    public void setSummary(SprintSummaryResponse summary) {
        this.summary = summary == null ? new SprintSummaryResponse() : summary;
    }

    public List<SprintBurndownPointResponse> getBurndownPoints() {
        return burndownPoints;
    }

    public void setBurndownPoints(List<SprintBurndownPointResponse> burndownPoints) {
        this.burndownPoints = burndownPoints == null ? List.of() : burndownPoints;
    }

    public List<SprintStatusDistributionResponse> getStatusDistributions() {
        return statusDistributions;
    }

    public void setStatusDistributions(List<SprintStatusDistributionResponse> statusDistributions) {
        this.statusDistributions = statusDistributions == null ? List.of() : statusDistributions;
    }

    public List<SprintAssigneeWorkloadResponse> getAssigneeWorkloads() {
        return assigneeWorkloads;
    }

    public void setAssigneeWorkloads(List<SprintAssigneeWorkloadResponse> assigneeWorkloads) {
        this.assigneeWorkloads = assigneeWorkloads == null ? List.of() : assigneeWorkloads;
    }

    public List<SprintRiskIssueResponse> getRiskIssues() {
        return riskIssues;
    }

    public void setRiskIssues(List<SprintRiskIssueResponse> riskIssues) {
        this.riskIssues = riskIssues == null ? List.of() : riskIssues;
    }

    public List<SprintTestEvidenceRiskResponse> getFailedTestEvidences() {
        return failedTestEvidences;
    }

    public void setFailedTestEvidences(List<SprintTestEvidenceRiskResponse> failedTestEvidences) {
        this.failedTestEvidences = failedTestEvidences == null ? List.of() : failedTestEvidences;
    }
}
