package com.devtrace.manager.issue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devtrace.manager.issue.dao.IssueDao;
import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.issue.dto.IssueRequest;
import com.devtrace.manager.issue.dto.IssueResponse;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import com.devtrace.manager.issue.dto.IssueStatus;
import com.devtrace.manager.issue.dto.IssueType;
import com.devtrace.manager.issue.service.impl.IssueServiceImpl;
import com.devtrace.manager.project.dao.ProjectDao;
import com.devtrace.manager.project.dto.ProjectEntity;
import com.devtrace.manager.project.dto.ProjectStatus;
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
class IssueServiceImplTest {

    @Mock
    private IssueDao issueDao;

    @Mock
    private ProjectDao projectDao;

    private IssueService issueService;

    @BeforeEach
    void setUp() {
        issueService = new IssueServiceImpl(issueDao, projectDao);
    }

    @Test
    void insertIssue() {
        UUID projectId = UUID.randomUUID();
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        IssueRequest request = createRequest(projectId, "DTR-101", "이슈 등록");

        IssueResponse response = issueService.insertIssue(request);

        ArgumentCaptor<IssueEntity> captor = ArgumentCaptor.forClass(IssueEntity.class);
        verify(issueDao).insertIssue(captor.capture());
        IssueEntity saved = captor.getValue();

        assertThat(saved.getIssueId()).isNotNull();
        assertThat(saved.getProjectId()).isEqualTo(projectId);
        assertThat(saved.getIssueKey()).isEqualTo("DTR-101");
        assertThat(saved.getSpentMinutes()).isZero();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(response.getIssueKey()).isEqualTo("DTR-101");
    }

    @Test
    void updateIssue() {
        UUID projectId = UUID.randomUUID();
        UUID issueId = UUID.randomUUID();
        IssueEntity issue = createIssueEntity(issueId, projectId);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(issueDao.selectIssueByIdDetails(issueId)).thenReturn(Optional.of(issue));

        IssueRequest request = createRequest(projectId, "DTR-102", "수정된 이슈");
        IssueResponse response = issueService.updateIssue(issueId, request);

        verify(issueDao).updateIssue(issue);
        assertThat(response.getIssueKey()).isEqualTo("DTR-102");
        assertThat(response.getTitle()).isEqualTo("수정된 이슈");
        assertThat(response.getUpdatedAt()).isNotNull();
    }

    @Test
    void updateIssueStatus() {
        UUID projectId = UUID.randomUUID();
        UUID issueId = UUID.randomUUID();
        IssueEntity issue = createIssueEntity(issueId, projectId);
        when(issueDao.selectIssueByIdDetails(issueId)).thenReturn(Optional.of(issue));

        IssueResponse response = issueService.updateIssueStatus(issueId, IssueStatus.DONE);

        verify(issueDao).updateIssueStatus(eq(issueId), eq(IssueStatus.DONE), eq(response.getResolvedDate()), any(LocalDateTime.class));
        assertThat(response.getStatus()).isEqualTo(IssueStatus.DONE);
        assertThat(response.getResolvedDate()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
    }

    @Test
    void deleteIssue() {
        UUID projectId = UUID.randomUUID();
        UUID issueId = UUID.randomUUID();
        IssueEntity issue = createIssueEntity(issueId, projectId);
        when(issueDao.selectIssueByIdDetails(issueId)).thenReturn(Optional.of(issue));

        issueService.deleteIssue(issueId);

        verify(issueDao).deleteIssue(issueId);
    }

    @Test
    void selectIssueDetails() {
        UUID projectId = UUID.randomUUID();
        UUID issueId = UUID.randomUUID();
        IssueEntity issue = createIssueEntity(issueId, projectId);
        when(issueDao.selectIssueByIdDetails(issueId)).thenReturn(Optional.of(issue));

        IssueResponse response = issueService.selectIssueDetails(issueId);

        assertThat(response.getIssueId()).isEqualTo(issueId);
        assertThat(response.getIssueKey()).isEqualTo("DTR-101");
    }

    @Test
    void selectIssueList() {
        UUID projectId = UUID.randomUUID();
        IssueEntity issue = createIssueEntity(UUID.randomUUID(), projectId);
        when(issueDao.selectIssueList(any(IssueSearchCondition.class))).thenReturn(List.of(issue));

        List<IssueResponse> issues = issueService.selectIssueList(new IssueSearchCondition());

        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).getProjectId()).isEqualTo(projectId);
        assertThat(issues.get(0).getStatus()).isEqualTo(IssueStatus.REGISTERED);
    }

    private IssueRequest createRequest(UUID projectId, String issueKey, String title) {
        IssueRequest request = new IssueRequest();
        request.setProjectId(projectId);
        request.setIssueKey(issueKey);
        request.setIssueType(IssueType.FEATURE);
        request.setTitle(title);
        request.setDescription("설명");
        request.setStatus(IssueStatus.REGISTERED);
        request.setPriority(IssuePriority.NORMAL);
        request.setStartDate(LocalDate.of(2026, 5, 4));
        request.setDueDate(LocalDate.of(2026, 5, 10));
        request.setEstimatedMinutes(480);
        return request;
    }

    private IssueEntity createIssueEntity(UUID issueId, UUID projectId) {
        IssueEntity issue = new IssueEntity();
        issue.setIssueId(issueId);
        issue.setProjectId(projectId);
        issue.setIssueKey("DTR-101");
        issue.setIssueType(IssueType.FEATURE);
        issue.setTitle("이슈 등록");
        issue.setDescription("설명");
        issue.setStatus(IssueStatus.REGISTERED);
        issue.setPriority(IssuePriority.NORMAL);
        issue.setStartDate(LocalDate.of(2026, 5, 4));
        issue.setDueDate(LocalDate.of(2026, 5, 10));
        issue.setEstimatedMinutes(480);
        issue.setSpentMinutes(0);
        issue.setCreatedAt(LocalDateTime.of(2026, 5, 4, 9, 0));
        return issue;
    }

    private ProjectEntity createProject(UUID projectId) {
        ProjectEntity project = new ProjectEntity();
        project.setProjectId(projectId);
        project.setProjectCode("DTR");
        project.setProjectName("DevTrace Manager");
        project.setStatus(ProjectStatus.READY);
        project.setCreatedAt(LocalDateTime.of(2026, 5, 4, 9, 0));
        return project;
    }
}
