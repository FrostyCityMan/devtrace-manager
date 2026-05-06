package com.devtrace.manager.artifact.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devtrace.manager.artifact.dao.ArtifactDao;
import com.devtrace.manager.artifact.dto.ArtifactFileResponse;
import com.devtrace.manager.artifact.dto.ArtifactHistoryEntity;
import com.devtrace.manager.artifact.dto.ArtifactMarkdownResponse;
import com.devtrace.manager.artifact.dto.ArtifactRequest;
import com.devtrace.manager.artifact.dto.ArtifactType;
import com.devtrace.manager.artifact.dto.DailyReportData;
import com.devtrace.manager.artifact.dto.DailyReportIssueRow;
import com.devtrace.manager.artifact.dto.TestResultEvidenceRow;
import com.devtrace.manager.artifact.dto.TestResultReportData;
import com.devtrace.manager.artifact.dto.WeeklyReportData;
import com.devtrace.manager.artifact.dto.WeeklyReportIssueRow;
import com.devtrace.manager.artifact.dto.WeeklyReportVcsRow;
import com.devtrace.manager.artifact.dto.WeeklyReportWorkLogRow;
import com.devtrace.manager.artifact.excel.TestResultReportExcelGenerator;
import com.devtrace.manager.artifact.markdown.DailyReportMarkdownGenerator;
import com.devtrace.manager.artifact.markdown.TestResultReportMarkdownGenerator;
import com.devtrace.manager.artifact.markdown.WeeklyReportMarkdownGenerator;
import com.devtrace.manager.artifact.service.impl.ArtifactServiceImpl;
import com.devtrace.manager.project.dao.ProjectDao;
import com.devtrace.manager.project.dto.ProjectEntity;
import com.devtrace.manager.project.dto.ProjectStatus;
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
class ArtifactServiceImplTest {

    @Mock
    private ArtifactDao artifactDao;

    @Mock
    private ProjectDao projectDao;

    @Mock
    private WeeklyReportMarkdownGenerator weeklyReportMarkdownGenerator;

    @Mock
    private DailyReportMarkdownGenerator dailyReportMarkdownGenerator;

    @Mock
    private TestResultReportMarkdownGenerator testResultReportMarkdownGenerator;

    @Mock
    private TestResultReportExcelGenerator testResultReportExcelGenerator;

    private ArtifactService artifactService;

    @BeforeEach
    void setUp() {
        artifactService = new ArtifactServiceImpl(
                artifactDao,
                projectDao,
                weeklyReportMarkdownGenerator,
                dailyReportMarkdownGenerator,
                testResultReportMarkdownGenerator,
                testResultReportExcelGenerator
        );
    }

    @Test
    void selectWeeklyReportPreviewDetails() {
        UUID projectId = UUID.randomUUID();
        ArtifactRequest request = createWeeklyRequest(projectId);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(artifactDao.selectWeeklyReportIssueList(eq(projectId), eq(request.getStartDate()), eq(request.getEndDate()))).thenReturn(List.of(new WeeklyReportIssueRow()));
        when(artifactDao.selectWeeklyReportWorkLogList(eq(projectId), eq(request.getStartDate()), eq(request.getEndDate()))).thenReturn(List.of(new WeeklyReportWorkLogRow()));
        when(artifactDao.selectWeeklyReportVcsList(eq(projectId), eq(request.getStartDate()), eq(request.getEndDate()))).thenReturn(List.of(new WeeklyReportVcsRow()));
        when(weeklyReportMarkdownGenerator.generate(any(WeeklyReportData.class))).thenReturn("markdown");

        ArtifactMarkdownResponse response = artifactService.selectWeeklyReportPreviewDetails(request);

        assertThat(response.getFileName()).isEqualTo("weekly-report-DTR-001-2026-04-27-2026-05-03.md");
        assertThat(response.getContent()).isEqualTo("markdown");
        assertThat(response.getIssueCount()).isEqualTo(1);
        assertThat(response.getWorkLogCount()).isEqualTo(1);
        assertThat(response.getVcsLogCount()).isEqualTo(1);
        assertThat(response.getSpentHoursText()).isEqualTo("0.0h");
        verify(artifactDao, never()).insertArtifactHistory(any(ArtifactHistoryEntity.class));
    }

    @Test
    void insertWeeklyReportMarkdown() {
        UUID projectId = UUID.randomUUID();
        ArtifactRequest request = createWeeklyRequest(projectId);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(artifactDao.selectWeeklyReportIssueList(eq(projectId), eq(request.getStartDate()), eq(request.getEndDate()))).thenReturn(List.of());
        when(artifactDao.selectWeeklyReportWorkLogList(eq(projectId), eq(request.getStartDate()), eq(request.getEndDate()))).thenReturn(List.of());
        when(artifactDao.selectWeeklyReportVcsList(eq(projectId), eq(request.getStartDate()), eq(request.getEndDate()))).thenReturn(List.of());
        when(weeklyReportMarkdownGenerator.generate(any(WeeklyReportData.class))).thenReturn("markdown");

        artifactService.insertWeeklyReportMarkdown(request);

        ArtifactHistoryEntity saved = captureHistory();
        assertThat(saved.getArtifactType()).isEqualTo(ArtifactType.WEEKLY_REPORT);
        assertThat(saved.getFileName()).isEqualTo("weekly-report-DTR-001-2026-04-27-2026-05-03.md");
    }

    @Test
    void selectDailyReportPreviewDetails() {
        UUID projectId = UUID.randomUUID();
        ArtifactRequest request = createDailyRequest(projectId);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(artifactDao.selectDailyReportIssueList(projectId)).thenReturn(List.of(new DailyReportIssueRow()));
        when(artifactDao.selectDailyReportWorkLogList(projectId, request.getBaseDate())).thenReturn(List.of(new WeeklyReportWorkLogRow()));
        when(artifactDao.selectDailyReportVcsList(projectId, request.getBaseDate())).thenReturn(List.of(new WeeklyReportVcsRow()));
        when(dailyReportMarkdownGenerator.generate(any(DailyReportData.class))).thenReturn("daily");

        ArtifactMarkdownResponse response = artifactService.selectDailyReportPreviewDetails(request);

        assertThat(response.getFileName()).isEqualTo("daily-report-DTR-001-2026-05-06.md");
        assertThat(response.getContent()).isEqualTo("daily");
        assertThat(response.getIssueCount()).isEqualTo(1);
        assertThat(response.getWorkLogCount()).isEqualTo(1);
        assertThat(response.getVcsLogCount()).isEqualTo(1);
    }

    @Test
    void insertTestResultReportMarkdown() {
        UUID projectId = UUID.randomUUID();
        ArtifactRequest request = createTestResultRequest(projectId);
        TestResultEvidenceRow evidence = createEvidence(projectId, TestEvidenceResult.SUCCESS);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(artifactDao.selectTestResultEvidenceList(eq(projectId), eq(request.getStartDate()), eq(request.getEndDate()), isNull(), isNull())).thenReturn(List.of(evidence));
        when(testResultReportMarkdownGenerator.generate(any(TestResultReportData.class))).thenReturn("test result");

        ArtifactMarkdownResponse response = artifactService.insertTestResultReportMarkdown(request);

        assertThat(response.getFileName()).isEqualTo("test-result-report-DTR-001-2026-05-01-2026-05-06.md");
        assertThat(response.getTestCount()).isEqualTo(1);
        assertThat(response.getSuccessCount()).isEqualTo(1);
        ArtifactHistoryEntity saved = captureHistory();
        assertThat(saved.getArtifactType()).isEqualTo(ArtifactType.TEST_RESULT_REPORT);
    }

    @Test
    void insertTestResultReportExcel() {
        UUID projectId = UUID.randomUUID();
        ArtifactRequest request = createTestResultRequest(projectId);
        TestResultEvidenceRow evidence = createEvidence(projectId, TestEvidenceResult.FAIL);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(artifactDao.selectTestResultEvidenceList(eq(projectId), eq(request.getStartDate()), eq(request.getEndDate()), isNull(), isNull())).thenReturn(List.of(evidence));
        when(testResultReportExcelGenerator.generate(any(TestResultReportData.class))).thenReturn(new byte[]{1, 2, 3});

        ArtifactFileResponse response = artifactService.insertTestResultReportExcel(request);

        assertThat(response.getFileName()).isEqualTo("test-result-report-DTR-001-2026-05-01-2026-05-06.xlsx");
        assertThat(response.getContent()).containsExactly(1, 2, 3);
        ArtifactHistoryEntity saved = captureHistory();
        assertThat(saved.getArtifactType()).isEqualTo(ArtifactType.TEST_RESULT_REPORT);
    }

    private ArtifactHistoryEntity captureHistory() {
        ArgumentCaptor<ArtifactHistoryEntity> captor = ArgumentCaptor.forClass(ArtifactHistoryEntity.class);
        verify(artifactDao).insertArtifactHistory(captor.capture());
        ArtifactHistoryEntity saved = captor.getValue();
        assertThat(saved.getArtifactId()).isNotNull();
        assertThat(saved.getGeneratedBy()).isEqualTo(ArtifactRequest.DEFAULT_ADMIN_USER_ID);
        assertThat(saved.getGeneratedAt()).isNotNull();
        return saved;
    }

    private ArtifactRequest createWeeklyRequest(UUID projectId) {
        ArtifactRequest request = new ArtifactRequest();
        request.setProjectId(projectId);
        request.setArtifactType(ArtifactType.WEEKLY_REPORT);
        request.setStartDate(LocalDate.of(2026, 4, 27));
        request.setEndDate(LocalDate.of(2026, 5, 3));
        return request;
    }

    private ArtifactRequest createDailyRequest(UUID projectId) {
        ArtifactRequest request = new ArtifactRequest();
        request.setProjectId(projectId);
        request.setArtifactType(ArtifactType.DAILY_REPORT);
        request.setBaseDate(LocalDate.of(2026, 5, 6));
        return request;
    }

    private ArtifactRequest createTestResultRequest(UUID projectId) {
        ArtifactRequest request = new ArtifactRequest();
        request.setProjectId(projectId);
        request.setArtifactType(ArtifactType.TEST_RESULT_REPORT);
        request.setStartDate(LocalDate.of(2026, 5, 1));
        request.setEndDate(LocalDate.of(2026, 5, 6));
        return request;
    }

    private TestResultEvidenceRow createEvidence(UUID projectId, TestEvidenceResult resultStatus) {
        TestResultEvidenceRow evidence = new TestResultEvidenceRow();
        evidence.setTestEvidenceId(UUID.randomUUID());
        evidence.setProjectId(projectId);
        evidence.setIssueId(UUID.randomUUID());
        evidence.setIssueKey("DTR-101");
        evidence.setTestName("로그인 테스트");
        evidence.setResultStatus(resultStatus);
        return evidence;
    }

    private ProjectEntity createProject(UUID projectId) {
        ProjectEntity project = new ProjectEntity();
        project.setProjectId(projectId);
        project.setProjectCode("DTR-001");
        project.setProjectName("DevTrace Manager");
        project.setClientName("고객사");
        project.setStatus(ProjectStatus.DEVELOPMENT);
        project.setCreatedAt(LocalDateTime.of(2026, 5, 1, 9, 0));
        return project;
    }
}
