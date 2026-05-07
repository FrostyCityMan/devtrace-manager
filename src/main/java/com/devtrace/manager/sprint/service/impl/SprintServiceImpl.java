package com.devtrace.manager.sprint.service.impl;

import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.common.util.DateTimeUtil;
import com.devtrace.manager.issue.dao.IssueDao;
import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.issue.dto.IssueStatus;
import com.devtrace.manager.project.dao.ProjectDao;
import com.devtrace.manager.sprint.dao.SprintDao;
import com.devtrace.manager.sprint.dto.SprintAssigneeWorkloadResponse;
import com.devtrace.manager.sprint.dto.SprintBacklogSearchCondition;
import com.devtrace.manager.sprint.dto.SprintBurndownPointResponse;
import com.devtrace.manager.sprint.dto.SprintEntity;
import com.devtrace.manager.sprint.dto.SprintIssueEntity;
import com.devtrace.manager.sprint.dto.SprintIssueOrderRequest;
import com.devtrace.manager.sprint.dto.SprintIssueRequest;
import com.devtrace.manager.sprint.dto.SprintIssueResponse;
import com.devtrace.manager.sprint.dto.SprintRequest;
import com.devtrace.manager.sprint.dto.SprintResponse;
import com.devtrace.manager.sprint.dto.SprintReportResponse;
import com.devtrace.manager.sprint.dto.SprintRiskIssueResponse;
import com.devtrace.manager.sprint.dto.SprintSearchCondition;
import com.devtrace.manager.sprint.dto.SprintStatus;
import com.devtrace.manager.sprint.dto.SprintStatusDistributionResponse;
import com.devtrace.manager.sprint.dto.SprintSummaryResponse;
import com.devtrace.manager.sprint.dto.SprintTestEvidenceRiskResponse;
import com.devtrace.manager.sprint.service.SprintService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 스프린트 계획과 분석 업무 규칙을 구현합니다.
 *
 * <p>스프린트 상태 전이, 백로그 배정, 요약 지표, Burndown 계산형 리포트를 조립합니다.</p>
 */
@Service
@Transactional(readOnly = true)
public class SprintServiceImpl implements SprintService {

    private final SprintDao sprintDao;
    private final ProjectDao projectDao;
    private final IssueDao issueDao;

    /**
     * 스프린트 서비스 구현체를 생성한다.
     *
     * <p>스프린트 자체의 저장소와 함께 프로젝트/이슈 존재 여부 검증을 위한 DAO를 주입받는다.</p>
     *
     * @param sprintDao 스프린트 SQL 호출 DAO
     * @param projectDao 프로젝트 검증 DAO
     * @param issueDao 이슈 검증 DAO
     */
    public SprintServiceImpl(SprintDao sprintDao, ProjectDao projectDao, IssueDao issueDao) {
        this.sprintDao = sprintDao;
        this.projectDao = projectDao;
        this.issueDao = issueDao;
    }

    /**
     * 신규 스프린트를 등록한다.
     *
     * <p>프로젝트 존재 여부와 기간 역전 여부를 먼저 검증한다. 요청 상태가 없으면
     * 계획 상태로 보정하여 백로그 계획 단계의 기본 흐름을 유지한다.</p>
     *
     * @param request 스프린트 등록 요청
     * @return 등록 후 다시 조회한 스프린트 응답
     */
    @Override
    @Transactional
    public SprintResponse insertSprint(SprintRequest request) {
        validateProject(request.getProjectId());
        validatePeriod(request);

        LocalDateTime now = DateTimeUtil.now();
        SprintEntity sprint = new SprintEntity();
        sprint.setSprintId(UUID.randomUUID());
        applyRequest(sprint, request);
        sprint.setStatus(request.getStatus() == null ? SprintStatus.PLANNED : request.getStatus());
        sprint.setCreatedAt(now);

        sprintDao.insertSprint(sprint);
        return selectSprintDetails(sprint.getSprintId());
    }

    /**
     * 기존 스프린트 기본 정보를 수정한다.
     *
     * <p>대상 스프린트가 존재해야 하며, 수정 요청의 프로젝트와 기간도 유효해야 한다.
     * 상태가 요청에 포함되지 않으면 기존 상태를 유지한다.</p>
     *
     * @param sprintId 수정 대상 스프린트 ID
     * @param request 스프린트 수정 요청
     * @return 수정 후 다시 조회한 스프린트 응답
     */
    @Override
    @Transactional
    public SprintResponse updateSprint(UUID sprintId, SprintRequest request) {
        SprintEntity sprint = selectSprintEntityDetails(sprintId);
        validateProject(request.getProjectId());
        validatePeriod(request);

        applyRequest(sprint, request);
        sprint.setStatus(request.getStatus() == null ? sprint.getStatus() : request.getStatus());
        sprint.setUpdatedAt(DateTimeUtil.now());
        sprintDao.updateSprint(sprint);
        return selectSprintDetails(sprintId);
    }

    /**
     * 스프린트를 진행 상태로 시작한다.
     *
     * <p>종료된 스프린트는 다시 시작할 수 없고, 동일 프로젝트에 이미 진행 중인 스프린트가
     * 있으면 업무 운영 기준상 시작을 거부한다.</p>
     *
     * @param sprintId 시작 대상 스프린트 ID
     * @return 시작 처리된 스프린트 응답
     */
    @Override
    @Transactional
    public SprintResponse updateSprintStart(UUID sprintId) {
        SprintEntity sprint = selectSprintEntityDetails(sprintId);
        if (sprint.getStatus() == SprintStatus.CLOSED) {
            throw new BusinessException("종료된 스프린트는 시작할 수 없습니다.", "SPRINT_ALREADY_CLOSED");
        }
        sprintDao.selectActiveSprintByProjectIdDetails(sprint.getProjectId())
                .filter(active -> !active.getSprintId().equals(sprintId))
                .ifPresent(active -> {
                    throw new BusinessException("이미 진행 중인 스프린트가 있습니다.", "SPRINT_ACTIVE_EXISTS");
                });
        sprintDao.updateSprintStatus(sprintId, SprintStatus.ACTIVE, DateTimeUtil.now());
        return selectSprintDetails(sprintId);
    }

    /**
     * 스프린트를 종료 상태로 전환한다.
     *
     * <p>종료 상태 전환은 멱등적으로 처리한다. 이미 종료된 스프린트라면 DB 갱신 없이
     * 현재 엔티티를 응답으로 변환한다.</p>
     *
     * @param sprintId 종료 대상 스프린트 ID
     * @return 종료 처리된 스프린트 응답
     */
    @Override
    @Transactional
    public SprintResponse updateSprintClose(UUID sprintId) {
        SprintEntity sprint = selectSprintEntityDetails(sprintId);
        if (sprint.getStatus() == SprintStatus.CLOSED) {
            return sprint.toResponse();
        }
        sprintDao.updateSprintStatus(sprintId, SprintStatus.CLOSED, DateTimeUtil.now());
        return selectSprintDetails(sprintId);
    }

    /**
     * 스프린트를 삭제한다.
     *
     * <p>존재하지 않는 ID에 대한 조용한 삭제를 허용하지 않고, 먼저 상세 조회로
     * 업무 데이터 존재 여부를 검증한다.</p>
     *
     * @param sprintId 삭제 대상 스프린트 ID
     */
    @Override
    @Transactional
    public void deleteSprint(UUID sprintId) {
        SprintEntity sprint = selectSprintEntityDetails(sprintId);
        sprintDao.deleteSprint(sprint.getSprintId());
    }

    /**
     * 스프린트 단건 상세를 조회한다.
     *
     * @param sprintId 조회 대상 스프린트 ID
     * @return 스프린트 응답
     */
    @Override
    public SprintResponse selectSprintDetails(UUID sprintId) {
        return selectSprintEntityDetails(sprintId).toResponse();
    }

    /**
     * 스프린트 목록을 조회한다.
     *
     * <p>null 조건은 빈 검색 조건으로 보정하여 Controller/API 계층에서 조건 객체 생성을
     * 강제하지 않는다.</p>
     *
     * @param condition 검색 조건
     * @return 스프린트 목록
     */
    @Override
    public List<SprintResponse> selectSprintList(SprintSearchCondition condition) {
        SprintSearchCondition searchCondition = condition == null ? new SprintSearchCondition() : condition;
        return sprintDao.selectSprintList(searchCondition).stream()
                .map(SprintEntity::toResponse)
                .toList();
    }

    /**
     * 스프린트에 배정 가능한 백로그 이슈 목록을 조회한다.
     *
     * <p>조회된 각 이슈에는 현재일 기준 지연 여부를 계산해 화면 카드 강조에 사용한다.</p>
     *
     * @param condition 백로그 검색 조건
     * @return 백로그 이슈 목록
     */
    @Override
    public List<SprintIssueResponse> selectBacklogIssueList(SprintBacklogSearchCondition condition) {
        SprintBacklogSearchCondition searchCondition = condition == null ? new SprintBacklogSearchCondition() : condition;
        return sprintDao.selectBacklogIssueList(searchCondition).stream()
                .peek(this::markDelayed)
                .toList();
    }

    /**
     * 스프린트에 배정된 이슈 목록을 조회한다.
     *
     * <p>스프린트 존재 여부를 먼저 확인하고, 각 이슈의 지연 여부를 계산한다.</p>
     *
     * @param sprintId 스프린트 ID
     * @return 스프린트 이슈 목록
     */
    @Override
    public List<SprintIssueResponse> selectSprintIssueList(UUID sprintId) {
        selectSprintEntityDetails(sprintId);
        return sprintDao.selectSprintIssueList(sprintId).stream()
                .peek(this::markDelayed)
                .toList();
    }

    /**
     * 스프린트 요약 지표를 조회한다.
     *
     * <p>DAO 결과가 null이면 빈 요약 객체로 보정하여 화면에서 null 방어 코드를 줄인다.</p>
     *
     * @param sprintId 스프린트 ID
     * @return 스프린트 요약 응답
     */
    @Override
    public SprintSummaryResponse selectSprintSummaryDetails(UUID sprintId) {
        selectSprintEntityDetails(sprintId);
        SprintSummaryResponse summary = sprintDao.selectSprintSummaryDetails(sprintId, LocalDate.now());
        return summary == null ? new SprintSummaryResponse() : summary;
    }

    /**
     * 스프린트 분석 리포트 데이터를 조회한다.
     *
     * <p>여러 SQL 조회 결과를 하나의 화면/API 응답으로 조합한다. 현재 MVP에서는
     * 스냅샷 테이블 없이 실시간 계산 방식으로 Burndown 데이터를 만든다.</p>
     *
     * @param sprintId 분석 대상 스프린트 ID
     * @return 스프린트 분석 리포트 응답
     */
    @Override
    public SprintReportResponse selectSprintReportDetails(UUID sprintId) {
        SprintEntity sprint = selectSprintEntityDetails(sprintId);
        LocalDate today = LocalDate.now();
        SprintSummaryResponse summary = selectSummary(sprintId, today);
        List<SprintStatusDistributionResponse> statusDistributions = selectStatusDistributions(sprintId, summary);
        List<SprintAssigneeWorkloadResponse> assigneeWorkloads = emptyIfNull(sprintDao.selectSprintAssigneeWorkloadList(sprintId));
        List<SprintRiskIssueResponse> riskIssues = emptyIfNull(sprintDao.selectSprintRiskIssueList(sprintId, today));
        List<SprintTestEvidenceRiskResponse> testEvidenceRisks = emptyIfNull(sprintDao.selectSprintTestEvidenceRiskList(sprintId));

        SprintReportResponse report = new SprintReportResponse();
        report.setSprint(sprint.toResponse());
        report.setSummary(summary);
        report.setBurndownPoints(buildBurndownPoints(sprint, summary, sprintDao.selectSprintDailySpentList(sprintId), today));
        report.setStatusDistributions(statusDistributions);
        report.setAssigneeWorkloads(assigneeWorkloads);
        report.setRiskIssues(riskIssues);
        report.setFailedTestEvidences(testEvidenceRisks);
        return report;
    }

    /**
     * Burndown Chart 포인트 목록을 조회한다.
     *
     * <p>리포트 전체가 필요 없는 API 호출을 위해 차트 데이터만 계산해 반환한다.</p>
     *
     * @param sprintId 분석 대상 스프린트 ID
     * @return Burndown Chart 포인트 목록
     */
    @Override
    public List<SprintBurndownPointResponse> selectSprintBurndownList(UUID sprintId) {
        SprintEntity sprint = selectSprintEntityDetails(sprintId);
        LocalDate today = LocalDate.now();
        return buildBurndownPoints(sprint, selectSummary(sprintId, today), sprintDao.selectSprintDailySpentList(sprintId), today);
    }

    /**
     * 이슈를 스프린트에 배정한다.
     *
     * <p>종료된 스프린트에는 배정할 수 없으며, 이슈와 스프린트의 프로젝트가 다르면
     * 계획 데이터의 정합성을 위해 거부한다.</p>
     *
     * @param sprintId 배정 대상 스프린트 ID
     * @param request 이슈 배정 요청
     * @return 배정된 스프린트 이슈 응답
     */
    @Override
    @Transactional
    public SprintIssueResponse insertSprintIssue(UUID sprintId, SprintIssueRequest request) {
        SprintEntity sprint = selectSprintEntityDetails(sprintId);
        if (sprint.getStatus() == SprintStatus.CLOSED) {
            throw new BusinessException("종료된 스프린트에는 이슈를 배정할 수 없습니다.", "SPRINT_CLOSED");
        }
        IssueEntity issue = selectIssueEntityDetails(request.getIssueId());
        if (!sprint.getProjectId().equals(issue.getProjectId())) {
            throw new BusinessException("스프린트와 이슈의 프로젝트가 다릅니다.", "SPRINT_ISSUE_PROJECT_MISMATCH");
        }
        if (sprintDao.selectSprintIssueDetails(sprintId, issue.getIssueId()).isPresent()) {
            throw new BusinessException("이미 배정된 이슈입니다.", "SPRINT_ISSUE_DUPLICATED");
        }

        SprintIssueEntity sprintIssue = new SprintIssueEntity();
        sprintIssue.setSprintId(sprintId);
        sprintIssue.setIssueId(issue.getIssueId());
        sprintIssue.setDisplayOrder(selectDisplayOrder(sprintId, request.getDisplayOrder()));
        sprintIssue.setCreatedAt(DateTimeUtil.now());
        sprintDao.insertSprintIssue(sprintIssue);

        return sprintDao.selectSprintIssueList(sprintId).stream()
                .filter(item -> item.getIssueId().equals(issue.getIssueId()))
                .peek(this::markDelayed)
                .findFirst()
                .orElseThrow(() -> new BusinessException("스프린트 이슈를 찾을 수 없습니다.", "SPRINT_ISSUE_NOT_FOUND"));
    }

    /**
     * 스프린트 이슈의 표시 순서를 수정한다.
     *
     * <p>스프린트와 배정 관계가 모두 존재하는지 확인한 뒤 순서만 갱신한다.</p>
     *
     * @param sprintId 스프린트 ID
     * @param issueId 이슈 ID
     * @param request 표시 순서 요청
     */
    @Override
    @Transactional
    public void updateSprintIssueDisplayOrder(UUID sprintId, UUID issueId, SprintIssueOrderRequest request) {
        selectSprintEntityDetails(sprintId);
        selectSprintIssueEntityDetails(sprintId, issueId);
        sprintDao.updateSprintIssueDisplayOrder(sprintId, issueId, request.getDisplayOrder());
    }

    /**
     * 스프린트에서 이슈 배정을 해제한다.
     *
     * <p>이슈 원본은 유지하고, 스프린트 계획과 이슈의 관계만 제거한다.</p>
     *
     * @param sprintId 스프린트 ID
     * @param issueId 제외 대상 이슈 ID
     */
    @Override
    @Transactional
    public void deleteSprintIssue(UUID sprintId, UUID issueId) {
        selectSprintEntityDetails(sprintId);
        selectSprintIssueEntityDetails(sprintId, issueId);
        sprintDao.deleteSprintIssue(sprintId, issueId);
    }

    /**
     * 스프린트 엔티티를 조회하고 없으면 업무 예외를 발생시킨다.
     *
     * @param sprintId 조회 대상 스프린트 ID
     * @return 스프린트 엔티티
     */
    private SprintEntity selectSprintEntityDetails(UUID sprintId) {
        return sprintDao.selectSprintDetails(sprintId)
                .orElseThrow(() -> new BusinessException("스프린트를 찾을 수 없습니다.", "SPRINT_NOT_FOUND"));
    }

    /**
     * 스프린트-이슈 배정 엔티티를 조회한다.
     *
     * <p>순서 변경이나 배정 해제처럼 기존 관계가 전제되는 작업에서 사용한다.</p>
     *
     * @param sprintId 스프린트 ID
     * @param issueId 이슈 ID
     * @return 스프린트 이슈 엔티티
     */
    private SprintIssueEntity selectSprintIssueEntityDetails(UUID sprintId, UUID issueId) {
        return sprintDao.selectSprintIssueDetails(sprintId, issueId)
                .orElseThrow(() -> new BusinessException("스프린트 이슈를 찾을 수 없습니다.", "SPRINT_ISSUE_NOT_FOUND"));
    }

    /**
     * 이슈 엔티티를 조회하고 없으면 업무 예외를 발생시킨다.
     *
     * @param issueId 조회 대상 이슈 ID
     * @return 이슈 엔티티
     */
    private IssueEntity selectIssueEntityDetails(UUID issueId) {
        return issueDao.selectIssueByIdDetails(issueId)
                .orElseThrow(() -> new BusinessException("이슈를 찾을 수 없습니다.", "ISSUE_NOT_FOUND"));
    }

    /**
     * 프로젝트 존재 여부를 검증한다.
     *
     * @param projectId 검증 대상 프로젝트 ID
     */
    private void validateProject(UUID projectId) {
        projectDao.selectProjectById(projectId)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다.", "PROJECT_NOT_FOUND"));
    }

    /**
     * 스프린트 기간이 역전되지 않았는지 검증한다.
     *
     * <p>시작일과 종료일이 모두 있는 경우에만 검증하며, 종료일이 시작일보다 빠르면
     * 스프린트 계획 기준을 위반한 것으로 본다.</p>
     *
     * @param request 검증 대상 스프린트 요청
     */
    private void validatePeriod(SprintRequest request) {
        if (request.getStartDate() != null
                && request.getEndDate() != null
                && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("스프린트 종료일은 시작일 이후여야 합니다.", "SPRINT_DATE_INVALID");
        }
    }

    /**
     * 요청 DTO의 기본 값을 스프린트 엔티티에 반영한다.
     *
     * <p>상태와 생성/수정 일시는 호출 흐름별 규칙이 다르므로 이 메소드에서 다루지 않는다.</p>
     *
     * @param sprint 값이 반영될 스프린트 엔티티
     * @param request 사용자 요청 DTO
     */
    private void applyRequest(SprintEntity sprint, SprintRequest request) {
        sprint.setProjectId(request.getProjectId());
        sprint.setSprintName(request.getSprintName());
        sprint.setGoal(request.getGoal());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());
    }

    /**
     * 스프린트 이슈 표시 순서를 결정한다.
     *
     * <p>요청 순서가 0 이상이면 그대로 사용하고, 없으면 현재 최대 표시 순서 다음 값을 사용한다.</p>
     *
     * @param sprintId 스프린트 ID
     * @param requestedDisplayOrder 요청 표시 순서
     * @return 확정된 표시 순서
     */
    private int selectDisplayOrder(UUID sprintId, Integer requestedDisplayOrder) {
        if (requestedDisplayOrder != null && requestedDisplayOrder >= 0) {
            return requestedDisplayOrder;
        }
        return sprintDao.selectSprintIssueMaxDisplayOrder(sprintId) + 1;
    }

    /**
     * 스프린트 요약을 조회하고 null을 빈 요약 객체로 보정한다.
     *
     * @param sprintId 스프린트 ID
     * @param today 지연 판단 기준일
     * @return 스프린트 요약 응답
     */
    private SprintSummaryResponse selectSummary(UUID sprintId, LocalDate today) {
        SprintSummaryResponse summary = sprintDao.selectSprintSummaryDetails(sprintId, today);
        return summary == null ? new SprintSummaryResponse() : summary;
    }

    /**
     * 상태별 이슈 분포를 조회하고 전체 이슈 대비 비율을 계산한다.
     *
     * <p>DAO는 건수만 조회하고, 비율 계산은 총 이슈 수를 알고 있는 서비스 계층에서 수행한다.</p>
     *
     * @param sprintId 스프린트 ID
     * @param summary 스프린트 요약 지표
     * @return 상태별 이슈 분포 목록
     */
    private List<SprintStatusDistributionResponse> selectStatusDistributions(UUID sprintId, SprintSummaryResponse summary) {
        List<SprintStatusDistributionResponse> distributions = emptyIfNull(sprintDao.selectSprintStatusDistributionList(sprintId));
        int totalCount = summary.getTotalIssueCount();
        distributions.forEach(distribution -> distribution.setIssueRate(totalCount == 0
                ? 0
                : Math.round(distribution.getIssueCount() * 100.0f / totalCount)));
        return distributions;
    }

    /**
     * Burndown Chart용 일자별 포인트를 계산한다.
     *
     * <p>각 포인트에는 다음 값이 포함된다.</p>
     * <ul>
     *     <li>해당 일자의 실제 투입 공수</li>
     *     <li>누적 투입 공수</li>
     *     <li>이상 잔여 공수</li>
     *     <li>현재일 이전 구간의 실제 잔여 공수</li>
     *     <li>SVG Polyline 표시를 위한 x/y 좌표 비율</li>
     * </ul>
     *
     * @param sprint 분석 대상 스프린트 엔티티
     * @param summary 스프린트 요약 지표
     * @param dailySpentList 일자별 실제 투입 공수 목록
     * @param today 실제 잔여 공수 표시 기준일
     * @return Burndown Chart 포인트 목록
     */
    private List<SprintBurndownPointResponse> buildBurndownPoints(
            SprintEntity sprint,
            SprintSummaryResponse summary,
            List<SprintBurndownPointResponse> dailySpentList,
            LocalDate today
    ) {
        if (sprint.getStartDate() == null || sprint.getEndDate() == null || sprint.getEndDate().isBefore(sprint.getStartDate())) {
            return List.of();
        }
        Map<LocalDate, Integer> spentByDate = emptyIfNull(dailySpentList).stream()
                .filter(point -> point.getSnapshotDate() != null)
                .collect(Collectors.toMap(
                        SprintBurndownPointResponse::getSnapshotDate,
                        SprintBurndownPointResponse::getSpentMinutes,
                        Integer::sum
                ));

        int estimatedMinutes = summary.getEstimatedMinutes();
        int maxRemainingMinutes = Math.max(1, estimatedMinutes);
        long totalDays = ChronoUnit.DAYS.between(sprint.getStartDate(), sprint.getEndDate()) + 1;
        List<SprintBurndownPointResponse> points = new ArrayList<>();
        int cumulativeSpentMinutes = 0;

        for (int dayIndex = 0; dayIndex < totalDays; dayIndex++) {
            LocalDate snapshotDate = sprint.getStartDate().plusDays(dayIndex);
            int spentMinutes = spentByDate.getOrDefault(snapshotDate, 0);
            cumulativeSpentMinutes += spentMinutes;
            int idealRemainingMinutes = calculateIdealRemainingMinutes(estimatedMinutes, dayIndex, totalDays);
            Integer actualRemainingMinutes = snapshotDate.isAfter(today)
                    ? null
                    : Math.max(0, estimatedMinutes - cumulativeSpentMinutes);

            SprintBurndownPointResponse point = new SprintBurndownPointResponse();
            point.setSnapshotDate(snapshotDate);
            point.setSpentMinutes(spentMinutes);
            point.setCumulativeSpentMinutes(cumulativeSpentMinutes);
            point.setIdealRemainingMinutes(idealRemainingMinutes);
            point.setActualRemainingMinutes(actualRemainingMinutes);
            point.setXPercent(calculateXPercent(dayIndex, totalDays));
            point.setIdealYPercent(calculateYPercent(idealRemainingMinutes, maxRemainingMinutes));
            point.setActualYPercent(actualRemainingMinutes == null
                    ? null
                    : calculateYPercent(actualRemainingMinutes, maxRemainingMinutes));
            points.add(point);
        }
        return points;
    }

    /**
     * 이상 잔여 공수를 선형 소진 기준으로 계산한다.
     *
     * <p>스프린트 시작일에는 전체 예상 공수, 종료일에는 0에 도달하는 선형 기준선이다.</p>
     *
     * @param estimatedMinutes 전체 예상 공수
     * @param dayIndex 시작일 기준 0부터 시작하는 일자 인덱스
     * @param totalDays 전체 스프린트 일수
     * @return 이상 잔여 공수
     */
    private int calculateIdealRemainingMinutes(int estimatedMinutes, int dayIndex, long totalDays) {
        if (estimatedMinutes <= 0) {
            return 0;
        }
        if (totalDays <= 1) {
            return estimatedMinutes;
        }
        double burnRatio = dayIndex / (double) (totalDays - 1);
        return Math.max(0, (int) Math.round(estimatedMinutes - (estimatedMinutes * burnRatio)));
    }

    /**
     * 차트의 X축 표시 위치를 0~100 비율로 계산한다.
     *
     * @param dayIndex 시작일 기준 일자 인덱스
     * @param totalDays 전체 스프린트 일수
     * @return SVG 좌표계의 X축 백분율
     */
    private int calculateXPercent(int dayIndex, long totalDays) {
        if (totalDays <= 1) {
            return 0;
        }
        return (int) Math.round(dayIndex * 100.0 / (totalDays - 1));
    }

    /**
     * 잔여 공수를 차트의 Y축 표시 위치로 변환한다.
     *
     * <p>SVG 좌표계는 위쪽이 0이므로, 잔여 공수가 많을수록 위쪽에 표시되도록 역비율을 적용한다.
     * 상하 여백을 확보하기 위해 10~90 범위로 제한한다.</p>
     *
     * @param remainingMinutes 잔여 공수
     * @param maxRemainingMinutes 차트 기준 최대 잔여 공수
     * @return SVG 좌표계의 Y축 백분율
     */
    private int calculateYPercent(int remainingMinutes, int maxRemainingMinutes) {
        double ratio = Math.max(0.0, Math.min(1.0, remainingMinutes / (double) maxRemainingMinutes));
        return 10 + (int) Math.round(80.0 * (1.0 - ratio));
    }

    /**
     * null 목록을 빈 목록으로 보정한다.
     *
     * <p>MyBatis 조회 결과가 null로 들어와도 서비스 조합 로직과 Thymeleaf 화면이 안정적으로
     * 동작하도록 하기 위한 방어 메소드다.</p>
     *
     * @param items 원본 목록
     * @param <T> 목록 요소 타입
     * @return 원본 목록 또는 빈 목록
     */
    private <T> List<T> emptyIfNull(List<T> items) {
        return items == null ? List.of() : items;
    }

    /**
     * 이슈의 지연 여부를 현재일 기준으로 표시한다.
     *
     * <p>완료/종료 상태가 아니고 예정일이 현재일보다 이전이면 지연으로 본다.</p>
     *
     * @param issue 지연 여부를 표시할 이슈 응답
     */
    private void markDelayed(SprintIssueResponse issue) {
        issue.setDelayed(issue.getDueDate() != null
                && issue.getDueDate().isBefore(LocalDate.now())
                && issue.getStatus() != null
                && issue.getStatus() != IssueStatus.DONE
                && issue.getStatus() != IssueStatus.CLOSED);
    }
}
