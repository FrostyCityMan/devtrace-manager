package com.devtrace.manager.sprint.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import com.devtrace.manager.sprint.dto.SprintEntity;
import com.devtrace.manager.sprint.dto.SprintIssueEntity;
import com.devtrace.manager.sprint.dto.SprintIssueRequest;
import com.devtrace.manager.sprint.dto.SprintIssueResponse;
import com.devtrace.manager.sprint.dto.SprintRequest;
import com.devtrace.manager.sprint.dto.SprintResponse;
import com.devtrace.manager.sprint.dto.SprintStatus;
import com.devtrace.manager.sprint.service.impl.SprintServiceImpl;
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
}
