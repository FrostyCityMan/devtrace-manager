package com.devtrace.manager.sprint.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 스프린트 분석 화면과 API에서 사용하는 통합 응답 DTO다.
 *
 * <p>스프린트 기본 정보, 요약 지표, Burndown 포인트, 상태 분포,
 * 담당자별 작업량, 위험 이슈, 실패/차단 테스트 증적을 하나의 응답으로 묶는다.</p>
 */
public class SprintReportResponse {

    private SprintResponse sprint;
    private SprintSummaryResponse summary = new SprintSummaryResponse();
    private List<SprintBurndownPointResponse> burndownPoints = List.of();
    private List<SprintStatusDistributionResponse> statusDistributions = List.of();
    private List<SprintAssigneeWorkloadResponse> assigneeWorkloads = List.of();
    private List<SprintRiskIssueResponse> riskIssues = List.of();
    private List<SprintTestEvidenceRiskResponse> failedTestEvidences = List.of();

    /**
     * 아직 완료되지 않은 이슈 수를 계산한다.
     *
     * @return 전체 이슈 수에서 완료 이슈 수를 뺀 값
     */
    public int getRemainingIssueCount() {
        return Math.max(0, summary.getTotalIssueCount() - summary.getDoneIssueCount());
    }

    /**
     * 스프린트 기간 경과율을 계산한다.
     *
     * <p>시작 전이면 0%, 종료일 이후면 100%를 반환한다.
     * 진행률과 비교해 완료 가능성 판단에 사용한다.</p>
     *
     * @return 기간 경과율
     */
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

    /**
     * 스프린트 완료 전망 라벨을 반환한다.
     *
     * <p>현재 구현은 이슈 완료율이 기간 경과율 이상이면 정상, 그보다 낮으면 주의로 본다.</p>
     *
     * @return 완료 전망 라벨
     */
    public String getForecastLabel() {
        if (summary.getTotalIssueCount() == 0) {
            return "데이터 없음";
        }
        return summary.getProgressRate() >= getElapsedRate() ? "정상" : "주의";
    }

    /**
     * 완료 전망 라벨에 대응하는 화면 CSS 클래스를 반환한다.
     *
     * @return CSS 클래스명
     */
    public String getForecastCssClass() {
        if (summary.getTotalIssueCount() == 0) {
            return "planned";
        }
        return "정상".equals(getForecastLabel()) ? "done" : "danger";
    }

    /**
     * SVG polyline에 사용할 이상 잔여 공수 좌표 문자열을 만든다.
     *
     * @return {@code "x,y x,y"} 형태의 좌표 문자열
     */
    public String getIdealPolylinePoints() {
        return burndownPoints.stream()
                .map(point -> point.getXPercent() + "," + point.getIdealYPercent())
                .collect(Collectors.joining(" "));
    }

    /**
     * SVG polyline에 사용할 실제 잔여 공수 좌표 문자열을 만든다.
     *
     * <p>미래 일자처럼 실제 잔여 공수가 아직 없는 포인트는 제외한다.</p>
     *
     * @return {@code "x,y x,y"} 형태의 좌표 문자열
     */
    public String getActualPolylinePoints() {
        return burndownPoints.stream()
                .filter(point -> point.getActualRemainingMinutes() != null && point.getActualYPercent() != null)
                .map(point -> point.getXPercent() + "," + point.getActualYPercent())
                .collect(Collectors.joining(" "));
    }

    /**
     * 스프린트 기본 정보를 반환한다.
     *
     * @return 스프린트 응답
     */
    public SprintResponse getSprint() {
        return sprint;
    }

    /**
     * 스프린트 기본 정보를 설정한다.
     *
     * @param sprint 스프린트 응답
     */
    public void setSprint(SprintResponse sprint) {
        this.sprint = sprint;
    }

    /**
     * 스프린트 요약 지표를 반환한다.
     *
     * @return 요약 지표
     */
    public SprintSummaryResponse getSummary() {
        return summary;
    }

    /**
     * 스프린트 요약 지표를 설정한다.
     *
     * <p>null이 들어오면 빈 요약 객체로 보정한다.</p>
     *
     * @param summary 요약 지표
     */
    public void setSummary(SprintSummaryResponse summary) {
        this.summary = summary == null ? new SprintSummaryResponse() : summary;
    }

    /**
     * Burndown Chart 포인트 목록을 반환한다.
     *
     * @return Burndown 포인트 목록
     */
    public List<SprintBurndownPointResponse> getBurndownPoints() {
        return burndownPoints;
    }

    /**
     * Burndown Chart 포인트 목록을 설정한다.
     *
     * <p>null이 들어오면 빈 목록으로 보정한다.</p>
     *
     * @param burndownPoints Burndown 포인트 목록
     */
    public void setBurndownPoints(List<SprintBurndownPointResponse> burndownPoints) {
        this.burndownPoints = burndownPoints == null ? List.of() : burndownPoints;
    }

    /**
     * 상태별 이슈 분포 목록을 반환한다.
     *
     * @return 상태별 이슈 분포 목록
     */
    public List<SprintStatusDistributionResponse> getStatusDistributions() {
        return statusDistributions;
    }

    /**
     * 상태별 이슈 분포 목록을 설정한다.
     *
     * @param statusDistributions 상태별 이슈 분포 목록
     */
    public void setStatusDistributions(List<SprintStatusDistributionResponse> statusDistributions) {
        this.statusDistributions = statusDistributions == null ? List.of() : statusDistributions;
    }

    /**
     * 담당자별 작업량 목록을 반환한다.
     *
     * @return 담당자별 작업량 목록
     */
    public List<SprintAssigneeWorkloadResponse> getAssigneeWorkloads() {
        return assigneeWorkloads;
    }

    /**
     * 담당자별 작업량 목록을 설정한다.
     *
     * @param assigneeWorkloads 담당자별 작업량 목록
     */
    public void setAssigneeWorkloads(List<SprintAssigneeWorkloadResponse> assigneeWorkloads) {
        this.assigneeWorkloads = assigneeWorkloads == null ? List.of() : assigneeWorkloads;
    }

    /**
     * 위험 이슈 목록을 반환한다.
     *
     * @return 위험 이슈 목록
     */
    public List<SprintRiskIssueResponse> getRiskIssues() {
        return riskIssues;
    }

    /**
     * 위험 이슈 목록을 설정한다.
     *
     * @param riskIssues 위험 이슈 목록
     */
    public void setRiskIssues(List<SprintRiskIssueResponse> riskIssues) {
        this.riskIssues = riskIssues == null ? List.of() : riskIssues;
    }

    /**
     * 실패/차단 테스트 증적 목록을 반환한다.
     *
     * @return 실패/차단 테스트 증적 목록
     */
    public List<SprintTestEvidenceRiskResponse> getFailedTestEvidences() {
        return failedTestEvidences;
    }

    /**
     * 실패/차단 테스트 증적 목록을 설정한다.
     *
     * @param failedTestEvidences 실패/차단 테스트 증적 목록
     */
    public void setFailedTestEvidences(List<SprintTestEvidenceRiskResponse> failedTestEvidences) {
        this.failedTestEvidences = failedTestEvidences == null ? List.of() : failedTestEvidences;
    }
}
