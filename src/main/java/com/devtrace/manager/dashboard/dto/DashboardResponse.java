package com.devtrace.manager.dashboard.dto;

import java.util.List;

public class DashboardResponse {

    private DashboardSummaryResponse summary = new DashboardSummaryResponse();
    private List<DashboardRiskIssueResponse> todayDueIssues = List.of();
    private List<DashboardRiskIssueResponse> delayedIssues = List.of();
    private List<DashboardWbsSummaryResponse> delayedWbsTasks = List.of();
    private List<DashboardTestEvidenceSummaryResponse> failedTestEvidences = List.of();
    private List<DashboardRecentChangeLogResponse> recentChangeLogs = List.of();
    private List<DashboardProjectHealthResponse> projectHealthList = List.of();
    private List<DashboardBoardSummaryResponse> boardSummaryList = List.of();
    private List<DashboardRiskIssueResponse> recentDoneIssues = List.of();
    private List<DashboardTestEvidenceSummaryResponse> recentTestEvidences = List.of();
    private List<DashboardArtifactSummaryResponse> recentArtifacts = List.of();

    public DashboardSummaryResponse getSummary() {
        return summary;
    }

    public void setSummary(DashboardSummaryResponse summary) {
        this.summary = summary;
    }

    public List<DashboardRiskIssueResponse> getTodayDueIssues() {
        return todayDueIssues;
    }

    public void setTodayDueIssues(List<DashboardRiskIssueResponse> todayDueIssues) {
        this.todayDueIssues = todayDueIssues;
    }

    public List<DashboardRiskIssueResponse> getDelayedIssues() {
        return delayedIssues;
    }

    public void setDelayedIssues(List<DashboardRiskIssueResponse> delayedIssues) {
        this.delayedIssues = delayedIssues;
    }

    public List<DashboardWbsSummaryResponse> getDelayedWbsTasks() {
        return delayedWbsTasks;
    }

    public void setDelayedWbsTasks(List<DashboardWbsSummaryResponse> delayedWbsTasks) {
        this.delayedWbsTasks = delayedWbsTasks;
    }

    public List<DashboardTestEvidenceSummaryResponse> getFailedTestEvidences() {
        return failedTestEvidences;
    }

    public void setFailedTestEvidences(List<DashboardTestEvidenceSummaryResponse> failedTestEvidences) {
        this.failedTestEvidences = failedTestEvidences;
    }

    public List<DashboardRecentChangeLogResponse> getRecentChangeLogs() {
        return recentChangeLogs;
    }

    public void setRecentChangeLogs(List<DashboardRecentChangeLogResponse> recentChangeLogs) {
        this.recentChangeLogs = recentChangeLogs;
    }

    public List<DashboardProjectHealthResponse> getProjectHealthList() {
        return projectHealthList;
    }

    public void setProjectHealthList(List<DashboardProjectHealthResponse> projectHealthList) {
        this.projectHealthList = projectHealthList;
    }

    public List<DashboardBoardSummaryResponse> getBoardSummaryList() {
        return boardSummaryList;
    }

    public void setBoardSummaryList(List<DashboardBoardSummaryResponse> boardSummaryList) {
        this.boardSummaryList = boardSummaryList;
    }

    public List<DashboardRiskIssueResponse> getRecentDoneIssues() {
        return recentDoneIssues;
    }

    public void setRecentDoneIssues(List<DashboardRiskIssueResponse> recentDoneIssues) {
        this.recentDoneIssues = recentDoneIssues;
    }

    public List<DashboardTestEvidenceSummaryResponse> getRecentTestEvidences() {
        return recentTestEvidences;
    }

    public void setRecentTestEvidences(List<DashboardTestEvidenceSummaryResponse> recentTestEvidences) {
        this.recentTestEvidences = recentTestEvidences;
    }

    public List<DashboardArtifactSummaryResponse> getRecentArtifacts() {
        return recentArtifacts;
    }

    public void setRecentArtifacts(List<DashboardArtifactSummaryResponse> recentArtifacts) {
        this.recentArtifacts = recentArtifacts;
    }
}
