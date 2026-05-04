package com.devtrace.manager.artifact.service.impl;

import com.devtrace.manager.artifact.dao.ArtifactDao;
import com.devtrace.manager.artifact.dto.ArtifactHistoryEntity;
import com.devtrace.manager.artifact.dto.ArtifactHistoryResponse;
import com.devtrace.manager.artifact.dto.ArtifactMarkdownResponse;
import com.devtrace.manager.artifact.dto.ArtifactRequest;
import com.devtrace.manager.artifact.dto.ArtifactSearchCondition;
import com.devtrace.manager.artifact.dto.ArtifactType;
import com.devtrace.manager.artifact.dto.WeeklyReportData;
import com.devtrace.manager.artifact.markdown.WeeklyReportMarkdownGenerator;
import com.devtrace.manager.artifact.service.ArtifactService;
import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.common.util.DateTimeUtil;
import com.devtrace.manager.project.dao.ProjectDao;
import com.devtrace.manager.project.dto.ProjectResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ArtifactServiceImpl implements ArtifactService {

    private final ArtifactDao artifactDao;
    private final ProjectDao projectDao;
    private final WeeklyReportMarkdownGenerator weeklyReportMarkdownGenerator;

    public ArtifactServiceImpl(
            ArtifactDao artifactDao,
            ProjectDao projectDao,
            WeeklyReportMarkdownGenerator weeklyReportMarkdownGenerator
    ) {
        this.artifactDao = artifactDao;
        this.projectDao = projectDao;
        this.weeklyReportMarkdownGenerator = weeklyReportMarkdownGenerator;
    }

    @Override
    public ArtifactMarkdownResponse selectWeeklyReportPreviewDetails(ArtifactRequest request) {
        return generateWeeklyReport(request);
    }

    @Override
    @Transactional
    public ArtifactMarkdownResponse insertWeeklyReportMarkdown(ArtifactRequest request) {
        ArtifactMarkdownResponse markdown = generateWeeklyReport(request);
        ArtifactHistoryEntity artifactHistory = new ArtifactHistoryEntity();
        artifactHistory.setArtifactId(UUID.randomUUID());
        artifactHistory.setProjectId(request.getProjectId());
        artifactHistory.setArtifactType(ArtifactType.WEEKLY_REPORT);
        artifactHistory.setFileName(markdown.getFileName());
        artifactHistory.setGeneratedBy(request.getGeneratedBy() == null ? ArtifactRequest.DEFAULT_ADMIN_USER_ID : request.getGeneratedBy());
        artifactHistory.setGeneratedAt(DateTimeUtil.now());
        artifactDao.insertArtifactHistory(artifactHistory);
        return markdown;
    }

    @Override
    public List<ArtifactHistoryResponse> selectArtifactHistoryList(ArtifactSearchCondition condition) {
        ArtifactSearchCondition safeCondition = condition == null ? new ArtifactSearchCondition() : condition;
        return artifactDao.selectArtifactHistoryList(safeCondition).stream()
                .map(ArtifactHistoryEntity::toResponse)
                .toList();
    }

    private ArtifactMarkdownResponse generateWeeklyReport(ArtifactRequest request) {
        validateWeeklyReportRequest(request);
        ProjectResponse project = selectProject(request.getProjectId());
        WeeklyReportData data = new WeeklyReportData();
        data.setProject(project);
        data.setStartDate(request.getStartDate());
        data.setEndDate(request.getEndDate());
        var issues = artifactDao.selectWeeklyReportIssueList(request.getProjectId(), request.getStartDate(), request.getEndDate());
        var workLogs = artifactDao.selectWeeklyReportWorkLogList(request.getProjectId(), request.getStartDate(), request.getEndDate());
        var vcsLogs = artifactDao.selectWeeklyReportVcsList(request.getProjectId(), request.getStartDate(), request.getEndDate());
        data.setIssues(issues == null ? List.of() : issues);
        data.setWorkLogs(workLogs == null ? List.of() : workLogs);
        data.setVcsLogs(vcsLogs == null ? List.of() : vcsLogs);
        return new ArtifactMarkdownResponse(
                createFileName(project, request.getStartDate(), request.getEndDate()),
                weeklyReportMarkdownGenerator.generate(data),
                data.getIssues().size(),
                data.getWorkLogs().size(),
                data.getVcsLogs().size(),
                data.getEstimatedMinutesTotal(),
                data.getSpentMinutesTotal()
        );
    }

    private void validateWeeklyReportRequest(ArtifactRequest request) {
        if (request == null || request.getProjectId() == null || request.getStartDate() == null || request.getEndDate() == null) {
            throw new BusinessException("주간 업무보고 생성 요청이 올바르지 않습니다.", "ARTIFACT_REQUEST_INVALID");
        }
        if (request.getArtifactType() != ArtifactType.WEEKLY_REPORT) {
            throw new BusinessException("지원하지 않는 산출물 유형입니다.", "ARTIFACT_TYPE_NOT_SUPPORTED");
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException("시작일은 종료일보다 늦을 수 없습니다.", "ARTIFACT_PERIOD_INVALID");
        }
    }

    private ProjectResponse selectProject(UUID projectId) {
        return projectDao.selectProjectById(projectId)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다.", "PROJECT_NOT_FOUND"))
                .toResponse();
    }

    private String createFileName(ProjectResponse project, LocalDate startDate, LocalDate endDate) {
        String projectCode = project.getProjectCode() == null ? "project" : project.getProjectCode();
        String safeProjectCode = projectCode.replaceAll("[^A-Za-z0-9_-]", "_");
        return "weekly-report-" + safeProjectCode + "-" + startDate + "-" + endDate + ".md";
    }
}
