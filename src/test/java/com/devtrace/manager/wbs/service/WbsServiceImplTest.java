package com.devtrace.manager.wbs.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devtrace.manager.issue.dao.IssueDao;
import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.issue.dto.IssueStatus;
import com.devtrace.manager.issue.dto.IssueType;
import com.devtrace.manager.project.dao.ProjectDao;
import com.devtrace.manager.project.dto.ProjectEntity;
import com.devtrace.manager.project.dto.ProjectStatus;
import com.devtrace.manager.wbs.dao.WbsDao;
import com.devtrace.manager.wbs.dto.WbsDependencyType;
import com.devtrace.manager.wbs.dto.WbsGanttResponse;
import com.devtrace.manager.wbs.dto.WbsTaskDependencyEntity;
import com.devtrace.manager.wbs.dto.WbsTaskDependencyRequest;
import com.devtrace.manager.wbs.dto.WbsTaskDependencySearchCondition;
import com.devtrace.manager.wbs.dto.WbsTaskEntity;
import com.devtrace.manager.wbs.dto.WbsTaskRequest;
import com.devtrace.manager.wbs.dto.WbsTaskResponse;
import com.devtrace.manager.wbs.dto.WbsTaskSearchCondition;
import com.devtrace.manager.wbs.dto.WbsTaskStatus;
import com.devtrace.manager.wbs.dto.WbsTaskType;
import com.devtrace.manager.wbs.service.impl.WbsServiceImpl;
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
class WbsServiceImplTest {

    @Mock
    private WbsDao wbsDao;

    @Mock
    private ProjectDao projectDao;

    @Mock
    private IssueDao issueDao;

    private WbsService wbsService;

    @BeforeEach
    void setUp() {
        wbsService = new WbsServiceImpl(wbsDao, projectDao, issueDao);
    }

    @Test
    void insertRootWbsTaskCreatesWbsCode() {
        UUID projectId = UUID.randomUUID();
        WbsTaskRequest request = createTaskRequest(projectId, null, null);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(wbsDao.selectWbsTaskChildCount(projectId, null)).thenReturn(0);

        WbsTaskResponse response = wbsService.insertWbsTask(request);

        ArgumentCaptor<WbsTaskEntity> captor = ArgumentCaptor.forClass(WbsTaskEntity.class);
        verify(wbsDao).insertWbsTask(captor.capture());
        WbsTaskEntity saved = captor.getValue();
        assertThat(saved.getWbsTaskId()).isNotNull();
        assertThat(saved.getWbsCode()).isEqualTo("1");
        assertThat(saved.getDisplayOrder()).isEqualTo(1);
        assertThat(response.getTaskName()).isEqualTo("요구사항 분석");
    }

    @Test
    void insertChildWbsTaskUsesParentCodeAndIssueSpentMinutes() {
        UUID projectId = UUID.randomUUID();
        UUID parentTaskId = UUID.randomUUID();
        UUID issueId = UUID.randomUUID();
        WbsTaskEntity parent = createTask(parentTaskId, projectId, null, "1", WbsTaskStatus.IN_PROGRESS);
        WbsTaskRequest request = createTaskRequest(projectId, parentTaskId, issueId);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(wbsDao.selectWbsTaskDetails(parentTaskId)).thenReturn(Optional.of(parent));
        when(issueDao.selectIssueByIdDetails(issueId)).thenReturn(Optional.of(createIssue(projectId, issueId, 90)));
        when(wbsDao.selectWbsTaskChildCount(projectId, parentTaskId)).thenReturn(2);

        wbsService.insertWbsTask(request);

        ArgumentCaptor<WbsTaskEntity> captor = ArgumentCaptor.forClass(WbsTaskEntity.class);
        verify(wbsDao).insertWbsTask(captor.capture());
        WbsTaskEntity saved = captor.getValue();
        assertThat(saved.getWbsCode()).isEqualTo("1.3");
        assertThat(saved.getSpentMinutes()).isEqualTo(90);
    }

    @Test
    void insertFinishToStartDependency() {
        UUID projectId = UUID.randomUUID();
        UUID predecessorId = UUID.randomUUID();
        UUID successorId = UUID.randomUUID();
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(wbsDao.selectWbsTaskDetails(predecessorId)).thenReturn(Optional.of(createTask(predecessorId, projectId, null, "1", WbsTaskStatus.DONE)));
        when(wbsDao.selectWbsTaskDetails(successorId)).thenReturn(Optional.of(createTask(successorId, projectId, null, "2", WbsTaskStatus.READY)));

        WbsTaskDependencyRequest request = new WbsTaskDependencyRequest();
        request.setProjectId(projectId);
        request.setPredecessorTaskId(predecessorId);
        request.setSuccessorTaskId(successorId);
        request.setDependencyType(WbsDependencyType.FINISH_TO_START);
        request.setLagDays(1);

        wbsService.insertWbsTaskDependency(request);

        ArgumentCaptor<WbsTaskDependencyEntity> captor = ArgumentCaptor.forClass(WbsTaskDependencyEntity.class);
        verify(wbsDao).insertWbsTaskDependency(captor.capture());
        assertThat(captor.getValue().getDependencyId()).isNotNull();
        assertThat(captor.getValue().getLagDays()).isEqualTo(1);
    }

    @Test
    void selectWbsGanttDetailsMarksBlockedSuccessor() {
        UUID projectId = UUID.randomUUID();
        UUID predecessorId = UUID.randomUUID();
        UUID successorId = UUID.randomUUID();
        WbsTaskEntity predecessor = createTask(predecessorId, projectId, null, "1", WbsTaskStatus.IN_PROGRESS);
        WbsTaskEntity successor = createTask(successorId, projectId, null, "2", WbsTaskStatus.READY);
        WbsTaskDependencyEntity dependency = new WbsTaskDependencyEntity();
        dependency.setProjectId(projectId);
        dependency.setPredecessorTaskId(predecessorId);
        dependency.setPredecessorStatus(WbsTaskStatus.IN_PROGRESS);
        dependency.setSuccessorTaskId(successorId);
        dependency.setDependencyType(WbsDependencyType.FINISH_TO_START);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(wbsDao.selectWbsTaskList(any(WbsTaskSearchCondition.class))).thenReturn(List.of(predecessor, successor));
        when(wbsDao.selectWbsTaskDependencyList(any(WbsTaskDependencySearchCondition.class))).thenReturn(List.of(dependency));

        WbsGanttResponse gantt = wbsService.selectWbsGanttDetails(projectId);

        assertThat(gantt.getTasks()).hasSize(2);
        assertThat(gantt.getTaskCount()).isEqualTo(2);
        assertThat(gantt.getDoneCount()).isZero();
        assertThat(gantt.getRiskCount()).isZero();
        assertThat(gantt.getBlockedCount()).isEqualTo(1);
        assertThat(gantt.getTimelineDays()).isGreaterThanOrEqualTo(3);
        assertThat(gantt.getTasks().get(1).isPredecessorBlocked()).isTrue();
        assertThat(gantt.getTasks().get(1).getRiskClass()).isEqualTo("blocked");
    }

    private WbsTaskRequest createTaskRequest(UUID projectId, UUID parentTaskId, UUID issueId) {
        WbsTaskRequest request = new WbsTaskRequest();
        request.setProjectId(projectId);
        request.setParentTaskId(parentTaskId);
        request.setIssueId(issueId);
        request.setTaskName("요구사항 분석");
        request.setTaskType(WbsTaskType.TASK);
        request.setStatus(WbsTaskStatus.IN_PROGRESS);
        request.setPlanStartDate(LocalDate.of(2026, 5, 5));
        request.setPlanEndDate(LocalDate.of(2026, 5, 8));
        request.setEstimatedMinutes(480);
        request.setSpentMinutes(60);
        request.setProgressRate(20);
        return request;
    }

    private WbsTaskEntity createTask(UUID taskId, UUID projectId, UUID parentTaskId, String wbsCode, WbsTaskStatus status) {
        WbsTaskEntity task = new WbsTaskEntity();
        task.setWbsTaskId(taskId);
        task.setProjectId(projectId);
        task.setParentTaskId(parentTaskId);
        task.setWbsCode(wbsCode);
        task.setTaskName("WBS " + wbsCode);
        task.setTaskType(WbsTaskType.TASK);
        task.setStatus(status);
        task.setPlanStartDate(LocalDate.now().plusDays(1));
        task.setPlanEndDate(LocalDate.now().plusDays(3));
        task.setEstimatedMinutes(480);
        task.setSpentMinutes(60);
        task.setProgressRate(status == WbsTaskStatus.DONE ? 100 : 20);
        task.setCreatedAt(LocalDateTime.of(2026, 5, 5, 9, 0));
        return task;
    }

    private ProjectEntity createProject(UUID projectId) {
        ProjectEntity project = new ProjectEntity();
        project.setProjectId(projectId);
        project.setProjectCode("DTR");
        project.setProjectName("DevTrace Manager");
        project.setStatus(ProjectStatus.DEVELOPMENT);
        project.setCreatedAt(LocalDateTime.of(2026, 5, 5, 9, 0));
        return project;
    }

    private IssueEntity createIssue(UUID projectId, UUID issueId, int spentMinutes) {
        IssueEntity issue = new IssueEntity();
        issue.setIssueId(issueId);
        issue.setProjectId(projectId);
        issue.setIssueKey("DTR-101");
        issue.setIssueType(IssueType.FEATURE);
        issue.setTitle("요구사항 분석");
        issue.setStatus(IssueStatus.IN_PROGRESS);
        issue.setPriority(IssuePriority.NORMAL);
        issue.setSpentMinutes(spentMinutes);
        issue.setCreatedAt(LocalDateTime.of(2026, 5, 5, 9, 0));
        return issue;
    }
}
