package com.devtrace.manager.testevidence.service;

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
import com.devtrace.manager.testevidence.dao.TestEvidenceDao;
import com.devtrace.manager.testevidence.dto.TestEvidenceEntity;
import com.devtrace.manager.testevidence.dto.TestEvidenceRequest;
import com.devtrace.manager.testevidence.dto.TestEvidenceResponse;
import com.devtrace.manager.testevidence.dto.TestEvidenceResult;
import com.devtrace.manager.testevidence.dto.TestEvidenceSearchCondition;
import com.devtrace.manager.testevidence.service.impl.TestEvidenceServiceImpl;
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
class TestEvidenceServiceImplTest {

    @Mock
    private TestEvidenceDao testEvidenceDao;

    @Mock
    private ProjectDao projectDao;

    @Mock
    private IssueDao issueDao;

    private TestEvidenceService testEvidenceService;

    @BeforeEach
    void setUp() {
        testEvidenceService = new TestEvidenceServiceImpl(testEvidenceDao, projectDao, issueDao);
    }

    @Test
    void insertTestEvidence() {
        UUID projectId = UUID.randomUUID();
        UUID issueId = UUID.randomUUID();
        TestEvidenceRequest request = createRequest(projectId, issueId, TestEvidenceResult.SUCCESS);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(issueDao.selectIssueByIdDetails(issueId)).thenReturn(Optional.of(createIssue(projectId, issueId)));

        TestEvidenceResponse response = testEvidenceService.insertTestEvidence(request);

        ArgumentCaptor<TestEvidenceEntity> captor = ArgumentCaptor.forClass(TestEvidenceEntity.class);
        verify(testEvidenceDao).insertTestEvidence(captor.capture());
        TestEvidenceEntity saved = captor.getValue();
        assertThat(saved.getTestEvidenceId()).isNotNull();
        assertThat(saved.getProjectId()).isEqualTo(projectId);
        assertThat(saved.getIssueId()).isEqualTo(issueId);
        assertThat(saved.getTestName()).isEqualTo("로그인 성공 테스트");
        assertThat(saved.getResultStatus()).isEqualTo(TestEvidenceResult.SUCCESS);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(response.getTestName()).isEqualTo("로그인 성공 테스트");
    }

    @Test
    void updateTestEvidence() {
        UUID projectId = UUID.randomUUID();
        UUID issueId = UUID.randomUUID();
        UUID testEvidenceId = UUID.randomUUID();
        TestEvidenceEntity entity = createEntity(testEvidenceId, projectId, issueId);
        TestEvidenceRequest request = createRequest(projectId, issueId, TestEvidenceResult.FAIL);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(issueDao.selectIssueByIdDetails(issueId)).thenReturn(Optional.of(createIssue(projectId, issueId)));
        when(testEvidenceDao.selectTestEvidenceDetails(testEvidenceId)).thenReturn(Optional.of(entity));

        TestEvidenceResponse response = testEvidenceService.updateTestEvidence(testEvidenceId, request);

        verify(testEvidenceDao).updateTestEvidence(entity);
        assertThat(response.getResultStatus()).isEqualTo(TestEvidenceResult.FAIL);
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void selectTestEvidenceList() {
        TestEvidenceEntity entity = createEntity(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        when(testEvidenceDao.selectTestEvidenceList(any(TestEvidenceSearchCondition.class))).thenReturn(List.of(entity));

        List<TestEvidenceResponse> evidences = testEvidenceService.selectTestEvidenceList(new TestEvidenceSearchCondition());

        assertThat(evidences).hasSize(1);
        assertThat(evidences.get(0).getTestName()).isEqualTo("로그인 성공 테스트");
    }

    @Test
    void rejectIssueFromDifferentProject() {
        UUID projectId = UUID.randomUUID();
        UUID issueId = UUID.randomUUID();
        TestEvidenceRequest request = createRequest(projectId, issueId, TestEvidenceResult.SUCCESS);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(issueDao.selectIssueByIdDetails(issueId)).thenReturn(Optional.of(createIssue(UUID.randomUUID(), issueId)));

        assertThatThrownBy(() -> testEvidenceService.insertTestEvidence(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이슈가 선택한 프로젝트에 속하지 않습니다.");
    }

    private TestEvidenceRequest createRequest(UUID projectId, UUID issueId, TestEvidenceResult result) {
        TestEvidenceRequest request = new TestEvidenceRequest();
        request.setProjectId(projectId);
        request.setIssueId(issueId);
        request.setTestName("로그인 성공 테스트");
        request.setTestTarget("/login");
        request.setTestProcedure("관리자 계정으로 로그인한다.");
        request.setExpectedResult("대시보드로 이동한다.");
        request.setActualResult("대시보드로 이동했다.");
        request.setResultStatus(result);
        request.setTesterId(TestEvidenceRequest.DEFAULT_ADMIN_USER_ID);
        request.setTestedAt(LocalDateTime.of(2026, 5, 4, 10, 0));
        return request;
    }

    private TestEvidenceEntity createEntity(UUID testEvidenceId, UUID projectId, UUID issueId) {
        TestEvidenceEntity entity = new TestEvidenceEntity();
        entity.setTestEvidenceId(testEvidenceId);
        entity.setProjectId(projectId);
        entity.setProjectCode("DTR");
        entity.setProjectName("DevTrace Manager");
        entity.setIssueId(issueId);
        entity.setIssueKey("DTR-101");
        entity.setIssueTitle("로그인 기능");
        entity.setTestName("로그인 성공 테스트");
        entity.setTestTarget("/login");
        entity.setTestProcedure("관리자 계정으로 로그인한다.");
        entity.setExpectedResult("대시보드로 이동한다.");
        entity.setActualResult("대시보드로 이동했다.");
        entity.setResultStatus(TestEvidenceResult.SUCCESS);
        entity.setTesterId(TestEvidenceRequest.DEFAULT_ADMIN_USER_ID);
        entity.setTesterName("관리자");
        entity.setTestedAt(LocalDateTime.of(2026, 5, 4, 10, 0));
        entity.setCreatedAt(LocalDateTime.of(2026, 5, 4, 10, 5));
        return entity;
    }

    private ProjectEntity createProject(UUID projectId) {
        ProjectEntity project = new ProjectEntity();
        project.setProjectId(projectId);
        project.setProjectCode("DTR");
        project.setProjectName("DevTrace Manager");
        project.setStatus(ProjectStatus.TEST);
        project.setCreatedAt(LocalDateTime.of(2026, 5, 4, 9, 0));
        return project;
    }

    private IssueEntity createIssue(UUID projectId, UUID issueId) {
        IssueEntity issue = new IssueEntity();
        issue.setIssueId(issueId);
        issue.setProjectId(projectId);
        issue.setIssueKey("DTR-101");
        issue.setIssueType(IssueType.TEST);
        issue.setTitle("로그인 기능");
        issue.setStatus(IssueStatus.TESTING);
        issue.setPriority(IssuePriority.NORMAL);
        issue.setCreatedAt(LocalDateTime.of(2026, 5, 4, 9, 0));
        return issue;
    }
}
