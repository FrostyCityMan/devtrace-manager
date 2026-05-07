package com.devtrace.manager.sprint.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.issue.dao.IssueDao;
import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.issue.dto.IssueStatus;
import com.devtrace.manager.issue.dto.IssueType;
import com.devtrace.manager.project.dao.ProjectDao;
import com.devtrace.manager.project.dto.ProjectEntity;
import com.devtrace.manager.project.dto.ProjectStatus;
import com.devtrace.manager.sprint.dao.SprintDao;
import com.devtrace.manager.sprint.dto.SprintAssigneeWorkloadResponse;
import com.devtrace.manager.sprint.dto.SprintBurndownPointResponse;
import com.devtrace.manager.sprint.dto.SprintEntity;
import com.devtrace.manager.sprint.dto.SprintIssueEntity;
import com.devtrace.manager.sprint.dto.SprintIssueRequest;
import com.devtrace.manager.sprint.dto.SprintIssueResponse;
import com.devtrace.manager.sprint.dto.SprintRequest;
import com.devtrace.manager.sprint.dto.SprintResponse;
import com.devtrace.manager.sprint.dto.SprintReportResponse;
import com.devtrace.manager.sprint.dto.SprintRiskIssueResponse;
import com.devtrace.manager.sprint.dto.SprintStatus;
import com.devtrace.manager.sprint.dto.SprintStatusDistributionResponse;
import com.devtrace.manager.sprint.dto.SprintSummaryResponse;
import com.devtrace.manager.sprint.dto.SprintTestEvidenceRiskResponse;
import com.devtrace.manager.sprint.service.impl.SprintServiceImpl;
import com.devtrace.manager.testevidence.dto.TestEvidenceResult;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SprintServiceImplTest {

    @Mock
    private SprintDao sprintDao;

    @Mock
    private ProjectDao projectDao;

    @Mock
    private IssueDao issueDao;

    private SprintService sprintService;

    @BeforeEach
    void setUp() {
        sprintService = new SprintServiceImpl(sprintDao, projectDao, issueDao);
    }

    @Test
    void insertSprintUsesPlannedStatusByDefault() {
        UUID projectId = UUID.randomUUID();
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(sprintDao.selectSprintDetails(any(UUID.class)))
                .thenAnswer(invocation -> Optional.of(createSprint(invocation.getArgument(0), projectId, SprintStatus.PLANNED)));

        SprintResponse response = sprintService.insertSprint(createSprintRequest(projectId));

        ArgumentCaptor<SprintEntity> captor = ArgumentCaptor.forClass(SprintEntity.class);
        verify(sprintDao).insertSprint(captor.capture());
        assertThat(captor.getValue().getSprintId()).isNotNull();
        assertThat(captor.getValue().getStatus()).isEqualTo(SprintStatus.PLANNED);
        assertThat(response.getStatus()).isEqualTo(SprintStatus.PLANNED);
    }

    @Test
    void updateSprintStartRejectsWhenProjectAlreadyHasActiveSprint() {
        UUID projectId = UUID.randomUUID();
        UUID sprintId = UUID.randomUUID();
        UUID activeSprintId = UUID.randomUUID();
        when(sprintDao.selectSprintDetails(sprintId)).thenReturn(Optional.of(createSprint(sprintId, projectId, SprintStatus.PLANNED)));
        when(sprintDao.selectActiveSprintByProjectIdDetails(projectId))
                .thenReturn(Optional.of(createSprint(activeSprintId, projectId, SprintStatus.ACTIVE)));

        assertThatThrownBy(() -> sprintService.updateSprintStart(sprintId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 진행 중인 스프린트가 있습니다.");
    }

    @Test
    void insertSprintIssueUsesNextDisplayOrder() {
        UUID projectId = UUID.randomUUID();
        UUID sprintId = UUID.randomUUID();
        UUID issueId = UUID.randomUUID();
        SprintIssueRequest request = new SprintIssueRequest();
        request.setIssueId(issueId);
        when(sprintDao.selectSprintDetails(sprintId)).thenReturn(Optional.of(createSprint(sprintId, projectId, SprintStatus.ACTIVE)));
        when(issueDao.selectIssueByIdDetails(issueId)).thenReturn(Optional.of(createIssue(projectId, issueId)));
        when(sprintDao.selectSprintIssueDetails(sprintId, issueId)).thenReturn(Optional.empty());
        when(sprintDao.selectSprintIssueMaxDisplayOrder(sprintId)).thenReturn(2);
        when(sprintDao.selectSprintIssueList(sprintId)).thenReturn(List.of(createSprintIssueResponse(sprintId, projectId, issueId, 3)));

        SprintIssueResponse response = sprintService.insertSprintIssue(sprintId, request);

        ArgumentCaptor<SprintIssueEntity> captor = ArgumentCaptor.forClass(SprintIssueEntity.class);
        verify(sprintDao).insertSprintIssue(captor.capture());
        assertThat(captor.getValue().getDisplayOrder()).isEqualTo(3);
        assertThat(response.getIssueId()).isEqualTo(issueId);
        assertThat(response.getDisplayOrder()).isEqualTo(3);
    }

    @Test
    void selectSprintReportDetailsBuildsBurndownAndRiskSummary() {
        UUID projectId = UUID.randomUUID();
        UUID sprintId = UUID.randomUUID();
        SprintEntity sprint = createSprint(sprintId, projectId, SprintStatus.ACTIVE);
        sprint.setStartDate(LocalDate.of(2026, 5, 4));
        sprint.setEndDate(LocalDate.of(2026, 5, 6));
        when(sprintDao.selectSprintDetails(sprintId)).thenReturn(Optional.of(sprint));
        when(sprintDao.selectSprintSummaryDetails(eq(sprintId), any(LocalDate.class))).thenReturn(createSummary());
        when(sprintDao.selectSprintStatusDistributionList(sprintId)).thenReturn(List.of(createDistribution()));
        when(sprintDao.selectSprintAssigneeWorkloadList(sprintId)).thenReturn(List.of(createWorkload()));
        when(sprintDao.selectSprintRiskIssueList(eq(sprintId), any(LocalDate.class))).thenReturn(List.of(createRiskIssue()));
        when(sprintDao.selectSprintTestEvidenceRiskList(sprintId)).thenReturn(List.of(createTestEvidenceRisk()));
        when(sprintDao.selectSprintDailySpentList(sprintId)).thenReturn(List.of(
                createDailySpent(LocalDate.of(2026, 5, 4), 120),
                createDailySpent(LocalDate.of(2026, 5, 5), 180)
        ));

        SprintReportResponse report = sprintService.selectSprintReportDetails(sprintId);

        assertThat(report.getSprint().getSprintId()).isEqualTo(sprintId);
        assertThat(report.getRemainingIssueCount()).isEqualTo(2);
        assertThat(report.getBurndownPoints()).hasSize(3);
        assertThat(report.getBurndownPoints().get(0).getIdealRemainingMinutes()).isEqualTo(600);
        assertThat(report.getBurndownPoints().get(2).getIdealRemainingMinutes()).isZero();
        assertThat(report.getBurndownPoints().get(2).getActualRemainingMinutes()).isEqualTo(300);
        assertThat(report.getStatusDistributions().get(0).getIssueRate()).isEqualTo(50);
        assertThat(report.getAssigneeWorkloads()).hasSize(1);
        assertThat(report.getRiskIssues()).hasSize(1);
        assertThat(report.getFailedTestEvidences()).hasSize(1);
        assertThat(report.getIdealPolylinePoints()).isNotBlank();
        assertThat(report.getActualPolylinePoints()).isNotBlank();
    }

    private SprintRequest createSprintRequest(UUID projectId) {
        SprintRequest request = new SprintRequest();
        request.setProjectId(projectId);
        request.setSprintName("2026년 5월 1차 스프린트");
        request.setGoal("핵심 이슈 실행");
        request.setStartDate(LocalDate.of(2026, 5, 6));
        request.setEndDate(LocalDate.of(2026, 5, 19));
        return request;
    }

    private SprintEntity createSprint(UUID sprintId, UUID projectId, SprintStatus status) {
        SprintEntity sprint = new SprintEntity();
        sprint.setSprintId(sprintId);
        sprint.setProjectId(projectId);
        sprint.setProjectCode("DTR");
        sprint.setProjectName("DevTrace Manager");
        sprint.setSprintName("2026년 5월 1차 스프린트");
        sprint.setGoal("핵심 이슈 실행");
        sprint.setStatus(status);
        sprint.setStartDate(LocalDate.of(2026, 5, 6));
        sprint.setEndDate(LocalDate.of(2026, 5, 19));
        sprint.setCreatedAt(LocalDateTime.of(2026, 5, 6, 9, 0));
        return sprint;
    }

    private ProjectEntity createProject(UUID projectId) {
        ProjectEntity project = new ProjectEntity();
        project.setProjectId(projectId);
        project.setProjectCode("DTR");
        project.setProjectName("DevTrace Manager");
        project.setStatus(ProjectStatus.DEVELOPMENT);
        project.setCreatedAt(LocalDateTime.of(2026, 5, 6, 9, 0));
        return project;
    }

    private IssueEntity createIssue(UUID projectId, UUID issueId) {
        IssueEntity issue = new IssueEntity();
        issue.setIssueId(issueId);
        issue.setProjectId(projectId);
        issue.setIssueKey("DTR-101");
        issue.setIssueType(IssueType.FEATURE);
        issue.setTitle("스프린트 계획");
        issue.setStatus(IssueStatus.IN_PROGRESS);
        issue.setPriority(IssuePriority.NORMAL);
        issue.setCreatedAt(LocalDateTime.of(2026, 5, 6, 9, 0));
        return issue;
    }

    private SprintIssueResponse createSprintIssueResponse(UUID sprintId, UUID projectId, UUID issueId, int displayOrder) {
        SprintIssueResponse response = new SprintIssueResponse();
        response.setSprintId(sprintId);
        response.setProjectId(projectId);
        response.setIssueId(issueId);
        response.setIssueKey("DTR-101");
        response.setTitle("스프린트 계획");
        response.setStatus(IssueStatus.IN_PROGRESS);
        response.setPriority(IssuePriority.NORMAL);
        response.setDisplayOrder(displayOrder);
        return response;
    }

    private SprintSummaryResponse createSummary() {
        SprintSummaryResponse summary = new SprintSummaryResponse();
        summary.setTotalIssueCount(4);
        summary.setDoneIssueCount(2);
        summary.setActiveIssueCount(2);
        summary.setDelayedIssueCount(1);
        summary.setEstimatedMinutes(600);
        summary.setSpentMinutes(300);
        return summary;
    }

    private SprintStatusDistributionResponse createDistribution() {
        SprintStatusDistributionResponse distribution = new SprintStatusDistributionResponse();
        distribution.setStatus(IssueStatus.DONE);
        distribution.setIssueCount(2);
        return distribution;
    }

    private SprintAssigneeWorkloadResponse createWorkload() {
        SprintAssigneeWorkloadResponse workload = new SprintAssigneeWorkloadResponse();
        workload.setAssigneeId(UUID.randomUUID());
        workload.setAssigneeName("관리자");
        workload.setIssueCount(2);
        workload.setEstimatedMinutes(300);
        workload.setSpentMinutes(360);
        return workload;
    }

    private SprintRiskIssueResponse createRiskIssue() {
        SprintRiskIssueResponse riskIssue = new SprintRiskIssueResponse();
        riskIssue.setIssueId(UUID.randomUUID());
        riskIssue.setIssueKey("DTR-201");
        riskIssue.setTitle("지연 이슈");
        riskIssue.setStatus(IssueStatus.IN_PROGRESS);
        riskIssue.setPriority(IssuePriority.HIGH);
        riskIssue.setDueDate(LocalDate.of(2026, 5, 5));
        riskIssue.setEstimatedMinutes(120);
        riskIssue.setSpentMinutes(180);
        riskIssue.setDelayed(true);
        riskIssue.setHighPriority(true);
        riskIssue.setOverEffort(true);
        return riskIssue;
    }

    private SprintTestEvidenceRiskResponse createTestEvidenceRisk() {
        SprintTestEvidenceRiskResponse risk = new SprintTestEvidenceRiskResponse();
        risk.setTestEvidenceId(UUID.randomUUID());
        risk.setIssueId(UUID.randomUUID());
        risk.setIssueKey("DTR-201");
        risk.setTestName("로그인 실패 케이스");
        risk.setTestTarget("/login");
        risk.setResultStatus(TestEvidenceResult.FAIL);
        risk.setTesterName("관리자");
        risk.setTestedAt(LocalDateTime.of(2026, 5, 6, 10, 0));
        return risk;
    }

    private SprintBurndownPointResponse createDailySpent(LocalDate snapshotDate, int spentMinutes) {
        SprintBurndownPointResponse point = new SprintBurndownPointResponse();
        point.setSnapshotDate(snapshotDate);
        point.setSpentMinutes(spentMinutes);
        return point;
    }
}
