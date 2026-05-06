package com.devtrace.manager.artifact.service.impl;

import com.devtrace.manager.artifact.dao.ArtifactDao;
import com.devtrace.manager.artifact.dto.ArtifactFileResponse;
import com.devtrace.manager.artifact.dto.ArtifactHistoryEntity;
import com.devtrace.manager.artifact.dto.ArtifactHistoryResponse;
import com.devtrace.manager.artifact.dto.ArtifactMarkdownResponse;
import com.devtrace.manager.artifact.dto.ArtifactRequest;
import com.devtrace.manager.artifact.dto.ArtifactSearchCondition;
import com.devtrace.manager.artifact.dto.ArtifactType;
import com.devtrace.manager.artifact.dto.DailyReportData;
import com.devtrace.manager.artifact.dto.TestResultReportData;
import com.devtrace.manager.artifact.dto.WeeklyReportData;
import com.devtrace.manager.artifact.excel.TestResultReportExcelGenerator;
import com.devtrace.manager.artifact.markdown.DailyReportMarkdownGenerator;
import com.devtrace.manager.artifact.markdown.TestResultReportMarkdownGenerator;
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

    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final ArtifactDao artifactDao;
    private final ProjectDao projectDao;
    private final WeeklyReportMarkdownGenerator weeklyReportMarkdownGenerator;
    private final DailyReportMarkdownGenerator dailyReportMarkdownGenerator;
    private final TestResultReportMarkdownGenerator testResultReportMarkdownGenerator;
    private final TestResultReportExcelGenerator testResultReportExcelGenerator;

    public ArtifactServiceImpl(
            ArtifactDao artifactDao,
            ProjectDao projectDao,
            WeeklyReportMarkdownGenerator weeklyReportMarkdownGenerator,
            DailyReportMarkdownGenerator dailyReportMarkdownGenerator,
            TestResultReportMarkdownGenerator testResultReportMarkdownGenerator,
            TestResultReportExcelGenerator testResultReportExcelGenerator
    ) {
        this.artifactDao = artifactDao;
        this.projectDao = projectDao;
        this.weeklyReportMarkdownGenerator = weeklyReportMarkdownGenerator;
        this.dailyReportMarkdownGenerator = dailyReportMarkdownGenerator;
        this.testResultReportMarkdownGenerator = testResultReportMarkdownGenerator;
        this.testResultReportExcelGenerator = testResultReportExcelGenerator;
    }

    @Override
    public ArtifactMarkdownResponse selectWeeklyReportPreviewDetails(ArtifactRequest request) {
        return generateWeeklyReport(request);
    }

    @Override
    @Transactional
    public ArtifactMarkdownResponse insertWeeklyReportMarkdown(ArtifactRequest request) {
        ArtifactMarkdownResponse markdown = generateWeeklyReport(request);
        insertArtifactHistory(request.getProjectId(), ArtifactType.WEEKLY_REPORT, markdown.getFileName(), request.getGeneratedBy());
        return markdown;
    }

    @Override
    public ArtifactMarkdownResponse selectDailyReportPreviewDetails(ArtifactRequest request) {
        return generateDailyReport(request);
    }

    @Override
    @Transactional
    public ArtifactMarkdownResponse insertDailyReportMarkdown(ArtifactRequest request) {
        ArtifactMarkdownResponse markdown = generateDailyReport(request);
        insertArtifactHistory(request.getProjectId(), ArtifactType.DAILY_REPORT, markdown.getFileName(), request.getGeneratedBy());
        return markdown;
    }

    @Override
    public ArtifactMarkdownResponse selectTestResultReportPreviewDetails(ArtifactRequest request) {
        return generateTestResultReport(request);
    }

    @Override
    @Transactional
    public ArtifactMarkdownResponse insertTestResultReportMarkdown(ArtifactRequest request) {
        ArtifactMarkdownResponse markdown = generateTestResultReport(request);
        insertArtifactHistory(request.getProjectId(), ArtifactType.TEST_RESULT_REPORT, markdown.getFileName(), request.getGeneratedBy());
        return markdown;
    }

    @Override
    @Transactional
    public ArtifactFileResponse insertTestResultReportExcel(ArtifactRequest request) {
        TestResultReportData data = createTestResultReportData(request);
        String fileName = createTestResultReportFileName(data.getProject(), request.getStartDate(), request.getEndDate(), ".xlsx");
        byte[] content = testResultReportExcelGenerator.generate(data);
        insertArtifactHistory(request.getProjectId(), ArtifactType.TEST_RESULT_REPORT, fileName, request.getGeneratedBy());
        return new ArtifactFileResponse(fileName, EXCEL_CONTENT_TYPE, content);
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
                createWeeklyReportFileName(project, request.getStartDate(), request.getEndDate()),
                weeklyReportMarkdownGenerator.generate(data),
                data.getIssues().size(),
                data.getWorkLogs().size(),
                data.getVcsLogs().size(),
                data.getEstimatedMinutesTotal(),
                data.getSpentMinutesTotal()
        );
    }

    private ArtifactMarkdownResponse generateDailyReport(ArtifactRequest request) {
        DailyReportData data = createDailyReportData(request);
        ArtifactMarkdownResponse response = new ArtifactMarkdownResponse(
                createDailyReportFileName(data.getProject(), request.getBaseDate()),
                dailyReportMarkdownGenerator.generate(data),
                data.getIssues().size(),
                data.getWorkLogs().size(),
                data.getVcsLogs().size(),
                0,
                data.getSpentMinutesTotal()
        );
        return response;
    }

    private ArtifactMarkdownResponse generateTestResultReport(ArtifactRequest request) {
        TestResultReportData data = createTestResultReportData(request);
        ArtifactMarkdownResponse response = new ArtifactMarkdownResponse(
                createTestResultReportFileName(data.getProject(), request.getStartDate(), request.getEndDate(), ".md"),
                testResultReportMarkdownGenerator.generate(data)
        );
        response.setTestCount(data.getTotalCount());
        response.setSuccessCount((int) data.getSuccessCount());
        response.setFailCount((int) data.getFailCount());
        response.setBlockedCount((int) data.getBlockedCount());
        response.setScreenshotCount((int) data.getScreenshotCount());
        response.setIssueCount(data.getEvidences().stream().map(evidence -> evidence.getIssueId()).distinct().toList().size());
        return response;
    }

    private DailyReportData createDailyReportData(ArtifactRequest request) {
        validateDailyReportRequest(request);
        ProjectResponse project = selectProject(request.getProjectId());
        DailyReportData data = new DailyReportData();
        data.setProject(project);
        data.setBaseDate(request.getBaseDate());
        data.setIssues(artifactDao.selectDailyReportIssueList(request.getProjectId()));
        data.setWorkLogs(artifactDao.selectDailyReportWorkLogList(request.getProjectId(), request.getBaseDate()));
        data.setVcsLogs(artifactDao.selectDailyReportVcsList(request.getProjectId(), request.getBaseDate()));
        return data;
    }

    private TestResultReportData createTestResultReportData(ArtifactRequest request) {
        validateTestResultReportRequest(request);
        ProjectResponse project = selectProject(request.getProjectId());
        TestResultReportData data = new TestResultReportData();
        data.setProject(project);
        data.setStartDate(request.getStartDate());
        data.setEndDate(request.getEndDate());
        data.setEvidences(artifactDao.selectTestResultEvidenceList(
                request.getProjectId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getIssueId(),
                request.getResultStatus()
        ));
        return data;
    }

    private void validateWeeklyReportRequest(ArtifactRequest request) {
        validateProjectAndType(request, ArtifactType.WEEKLY_REPORT);
        validatePeriod(request, "주간 업무보고 생성 요청이 올바르지 않습니다.");
    }

    private void validateDailyReportRequest(ArtifactRequest request) {
        validateProjectAndType(request, ArtifactType.DAILY_REPORT);
        if (request.getBaseDate() == null) {
            throw new BusinessException("일일 업무보고 기준일은 필수입니다.", "ARTIFACT_BASE_DATE_REQUIRED");
        }
    }

    private void validateTestResultReportRequest(ArtifactRequest request) {
        validateProjectAndType(request, ArtifactType.TEST_RESULT_REPORT);
        validatePeriod(request, "테스트 결과 보고서 생성 요청이 올바르지 않습니다.");
    }

    private void validateProjectAndType(ArtifactRequest request, ArtifactType artifactType) {
        if (request == null || request.getProjectId() == null) {
            throw new BusinessException("산출물 생성 요청이 올바르지 않습니다.", "ARTIFACT_REQUEST_INVALID");
        }
        if (request.getArtifactType() != artifactType) {
            throw new BusinessException("지원하지 않는 산출물 유형입니다.", "ARTIFACT_TYPE_NOT_SUPPORTED");
        }
    }

    private void validatePeriod(ArtifactRequest request, String message) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new BusinessException(message, "ARTIFACT_PERIOD_REQUIRED");
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

    private void insertArtifactHistory(UUID projectId, ArtifactType artifactType, String fileName, UUID generatedBy) {
        ArtifactHistoryEntity artifactHistory = new ArtifactHistoryEntity();
        artifactHistory.setArtifactId(UUID.randomUUID());
        artifactHistory.setProjectId(projectId);
        artifactHistory.setArtifactType(artifactType);
        artifactHistory.setFileName(fileName);
        artifactHistory.setGeneratedBy(generatedBy == null ? ArtifactRequest.DEFAULT_ADMIN_USER_ID : generatedBy);
        artifactHistory.setGeneratedAt(DateTimeUtil.now());
        artifactDao.insertArtifactHistory(artifactHistory);
    }

    private String createWeeklyReportFileName(ProjectResponse project, LocalDate startDate, LocalDate endDate) {
        return "weekly-report-" + safeProjectCode(project) + "-" + startDate + "-" + endDate + ".md";
    }

    private String createDailyReportFileName(ProjectResponse project, LocalDate baseDate) {
        return "daily-report-" + safeProjectCode(project) + "-" + baseDate + ".md";
    }

    private String createTestResultReportFileName(ProjectResponse project, LocalDate startDate, LocalDate endDate, String extension) {
        return "test-result-report-" + safeProjectCode(project) + "-" + startDate + "-" + endDate + extension;
    }

    private String safeProjectCode(ProjectResponse project) {
        String projectCode = project.getProjectCode() == null ? "project" : project.getProjectCode();
        return projectCode.replaceAll("[^A-Za-z0-9_-]", "_");
    }
}
