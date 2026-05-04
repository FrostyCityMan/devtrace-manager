package com.devtrace.manager.artifact.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devtrace.manager.artifact.dao.ArtifactDao;
import com.devtrace.manager.artifact.dto.ArtifactHistoryEntity;
import com.devtrace.manager.artifact.dto.ArtifactMarkdownResponse;
import com.devtrace.manager.artifact.dto.ArtifactRequest;
import com.devtrace.manager.artifact.dto.ArtifactType;
import com.devtrace.manager.artifact.dto.WeeklyReportData;
import com.devtrace.manager.artifact.dto.WeeklyReportIssueRow;
import com.devtrace.manager.artifact.dto.WeeklyReportVcsRow;
import com.devtrace.manager.artifact.dto.WeeklyReportWorkLogRow;
import com.devtrace.manager.artifact.markdown.WeeklyReportMarkdownGenerator;
import com.devtrace.manager.artifact.service.impl.ArtifactServiceImpl;
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
class ArtifactServiceImplTest {

    @Mock
    private ArtifactDao artifactDao;

    @Mock
    private ProjectDao projectDao;

    @Mock
    private WeeklyReportMarkdownGenerator weeklyReportMarkdownGenerator;

    private ArtifactService artifactService;

    @BeforeEach
    void setUp() {
        artifactService = new ArtifactServiceImpl(artifactDao, projectDao, weeklyReportMarkdownGenerator);
    }

    @Test
    void selectWeeklyReportPreviewDetails() {
        UUID projectId = UUID.randomUUID();
        ArtifactRequest request = createRequest(projectId);
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
        assertThat(response.getEstimatedMinutes()).isZero();
        assertThat(response.getSpentMinutes()).isZero();
        assertThat(response.getSpentHoursText()).isEqualTo("0.0h");
        verify(artifactDao, never()).insertArtifactHistory(any(ArtifactHistoryEntity.class));
    }

    @Test
    void insertWeeklyReportMarkdown() {
        UUID projectId = UUID.randomUUID();
        ArtifactRequest request = createRequest(projectId);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(createProject(projectId)));
        when(artifactDao.selectWeeklyReportIssueList(eq(projectId), eq(request.getStartDate()), eq(request.getEndDate()))).thenReturn(List.of());
        when(artifactDao.selectWeeklyReportWorkLogList(eq(projectId), eq(request.getStartDate()), eq(request.getEndDate()))).thenReturn(List.of());
        when(artifactDao.selectWeeklyReportVcsList(eq(projectId), eq(request.getStartDate()), eq(request.getEndDate()))).thenReturn(List.of());
        when(weeklyReportMarkdownGenerator.generate(any(WeeklyReportData.class))).thenReturn("markdown");

        artifactService.insertWeeklyReportMarkdown(request);

        ArgumentCaptor<ArtifactHistoryEntity> captor = ArgumentCaptor.forClass(ArtifactHistoryEntity.class);
        verify(artifactDao).insertArtifactHistory(captor.capture());
        ArtifactHistoryEntity saved = captor.getValue();
        assertThat(saved.getArtifactId()).isNotNull();
        assertThat(saved.getProjectId()).isEqualTo(projectId);
        assertThat(saved.getArtifactType()).isEqualTo(ArtifactType.WEEKLY_REPORT);
        assertThat(saved.getFileName()).isEqualTo("weekly-report-DTR-001-2026-04-27-2026-05-03.md");
        assertThat(saved.getGeneratedBy()).isEqualTo(ArtifactRequest.DEFAULT_ADMIN_USER_ID);
        assertThat(saved.getGeneratedAt()).isNotNull();
    }

    private ArtifactRequest createRequest(UUID projectId) {
        ArtifactRequest request = new ArtifactRequest();
        request.setProjectId(projectId);
        request.setArtifactType(ArtifactType.WEEKLY_REPORT);
        request.setStartDate(LocalDate.of(2026, 4, 27));
        request.setEndDate(LocalDate.of(2026, 5, 3));
        return request;
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
