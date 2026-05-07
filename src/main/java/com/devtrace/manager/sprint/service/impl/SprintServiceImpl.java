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

@Service
@Transactional(readOnly = true)
public class SprintServiceImpl implements SprintService {

    private final SprintDao sprintDao;
    private final ProjectDao projectDao;
    private final IssueDao issueDao;

    public SprintServiceImpl(SprintDao sprintDao, ProjectDao projectDao, IssueDao issueDao) {
        this.sprintDao = sprintDao;
        this.projectDao = projectDao;
        this.issueDao = issueDao;
    }

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

    @Override
    @Transactional
    public void deleteSprint(UUID sprintId) {
        SprintEntity sprint = selectSprintEntityDetails(sprintId);
        sprintDao.deleteSprint(sprint.getSprintId());
    }

    @Override
    public SprintResponse selectSprintDetails(UUID sprintId) {
        return selectSprintEntityDetails(sprintId).toResponse();
    }

    @Override
    public List<SprintResponse> selectSprintList(SprintSearchCondition condition) {
        SprintSearchCondition searchCondition = condition == null ? new SprintSearchCondition() : condition;
        return sprintDao.selectSprintList(searchCondition).stream()
                .map(SprintEntity::toResponse)
                .toList();
    }

    @Override
    public List<SprintIssueResponse> selectBacklogIssueList(SprintBacklogSearchCondition condition) {
        SprintBacklogSearchCondition searchCondition = condition == null ? new SprintBacklogSearchCondition() : condition;
        return sprintDao.selectBacklogIssueList(searchCondition).stream()
                .peek(this::markDelayed)
                .toList();
    }

    @Override
    public List<SprintIssueResponse> selectSprintIssueList(UUID sprintId) {
        selectSprintEntityDetails(sprintId);
        return sprintDao.selectSprintIssueList(sprintId).stream()
                .peek(this::markDelayed)
                .toList();
    }

    @Override
    public SprintSummaryResponse selectSprintSummaryDetails(UUID sprintId) {
        selectSprintEntityDetails(sprintId);
        SprintSummaryResponse summary = sprintDao.selectSprintSummaryDetails(sprintId, LocalDate.now());
        return summary == null ? new SprintSummaryResponse() : summary;
    }

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

    @Override
    public List<SprintBurndownPointResponse> selectSprintBurndownList(UUID sprintId) {
        SprintEntity sprint = selectSprintEntityDetails(sprintId);
        LocalDate today = LocalDate.now();
        return buildBurndownPoints(sprint, selectSummary(sprintId, today), sprintDao.selectSprintDailySpentList(sprintId), today);
    }

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

    @Override
    @Transactional
    public void updateSprintIssueDisplayOrder(UUID sprintId, UUID issueId, SprintIssueOrderRequest request) {
        selectSprintEntityDetails(sprintId);
        selectSprintIssueEntityDetails(sprintId, issueId);
        sprintDao.updateSprintIssueDisplayOrder(sprintId, issueId, request.getDisplayOrder());
    }

    @Override
    @Transactional
    public void deleteSprintIssue(UUID sprintId, UUID issueId) {
        selectSprintEntityDetails(sprintId);
        selectSprintIssueEntityDetails(sprintId, issueId);
        sprintDao.deleteSprintIssue(sprintId, issueId);
    }

    private SprintEntity selectSprintEntityDetails(UUID sprintId) {
        return sprintDao.selectSprintDetails(sprintId)
                .orElseThrow(() -> new BusinessException("스프린트를 찾을 수 없습니다.", "SPRINT_NOT_FOUND"));
    }

    private SprintIssueEntity selectSprintIssueEntityDetails(UUID sprintId, UUID issueId) {
        return sprintDao.selectSprintIssueDetails(sprintId, issueId)
                .orElseThrow(() -> new BusinessException("스프린트 이슈를 찾을 수 없습니다.", "SPRINT_ISSUE_NOT_FOUND"));
    }

    private IssueEntity selectIssueEntityDetails(UUID issueId) {
        return issueDao.selectIssueByIdDetails(issueId)
                .orElseThrow(() -> new BusinessException("이슈를 찾을 수 없습니다.", "ISSUE_NOT_FOUND"));
    }

    private void validateProject(UUID projectId) {
        projectDao.selectProjectById(projectId)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다.", "PROJECT_NOT_FOUND"));
    }

    private void validatePeriod(SprintRequest request) {
        if (request.getStartDate() != null
                && request.getEndDate() != null
                && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("스프린트 종료일은 시작일 이후여야 합니다.", "SPRINT_DATE_INVALID");
        }
    }

    private void applyRequest(SprintEntity sprint, SprintRequest request) {
        sprint.setProjectId(request.getProjectId());
        sprint.setSprintName(request.getSprintName());
        sprint.setGoal(request.getGoal());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());
    }

    private int selectDisplayOrder(UUID sprintId, Integer requestedDisplayOrder) {
        if (requestedDisplayOrder != null && requestedDisplayOrder >= 0) {
            return requestedDisplayOrder;
        }
        return sprintDao.selectSprintIssueMaxDisplayOrder(sprintId) + 1;
    }

    private SprintSummaryResponse selectSummary(UUID sprintId, LocalDate today) {
        SprintSummaryResponse summary = sprintDao.selectSprintSummaryDetails(sprintId, today);
        return summary == null ? new SprintSummaryResponse() : summary;
    }

    private List<SprintStatusDistributionResponse> selectStatusDistributions(UUID sprintId, SprintSummaryResponse summary) {
        List<SprintStatusDistributionResponse> distributions = emptyIfNull(sprintDao.selectSprintStatusDistributionList(sprintId));
        int totalCount = summary.getTotalIssueCount();
        distributions.forEach(distribution -> distribution.setIssueRate(totalCount == 0
                ? 0
                : Math.round(distribution.getIssueCount() * 100.0f / totalCount)));
        return distributions;
    }

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

    private int calculateXPercent(int dayIndex, long totalDays) {
        if (totalDays <= 1) {
            return 0;
        }
        return (int) Math.round(dayIndex * 100.0 / (totalDays - 1));
    }

    private int calculateYPercent(int remainingMinutes, int maxRemainingMinutes) {
        double ratio = Math.max(0.0, Math.min(1.0, remainingMinutes / (double) maxRemainingMinutes));
        return 10 + (int) Math.round(80.0 * (1.0 - ratio));
    }

    private <T> List<T> emptyIfNull(List<T> items) {
        return items == null ? List.of() : items;
    }

    private void markDelayed(SprintIssueResponse issue) {
        issue.setDelayed(issue.getDueDate() != null
                && issue.getDueDate().isBefore(LocalDate.now())
                && issue.getStatus() != null
                && issue.getStatus() != IssueStatus.DONE
                && issue.getStatus() != IssueStatus.CLOSED);
    }
}
