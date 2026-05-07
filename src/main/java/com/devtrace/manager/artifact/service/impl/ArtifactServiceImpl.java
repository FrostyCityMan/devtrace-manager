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

/**
 * 산출물 생성 업무 규칙을 구현합니다.
 *
 * <p>이 구현체는 산출물별 입력 조건을 검증하고, DAO에서 조회한 운영 데이터를
 * Markdown 또는 Excel 생성기로 전달한 뒤, 실제 다운로드가 발생하는 생성 요청에서는
 * {@code ARTIFACT_HISTORY}에 이력을 남깁니다.</p>
 */
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

    /**
     * 산출물 생성 서비스 구현체를 생성합니다.
     *
     * @param artifactDao 산출물 및 보고서 원천 데이터 DAO
     * @param projectDao 프로젝트 조회 DAO
     * @param weeklyReportMarkdownGenerator 주간 업무보고 Markdown 생성기
     * @param dailyReportMarkdownGenerator 일일 업무보고 Markdown 생성기
     * @param testResultReportMarkdownGenerator 테스트 결과 보고서 Markdown 생성기
     * @param testResultReportExcelGenerator 테스트 결과 보고서 Excel 생성기
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ArtifactMarkdownResponse selectWeeklyReportPreviewDetails(ArtifactRequest request) {
        return generateWeeklyReport(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ArtifactMarkdownResponse insertWeeklyReportMarkdown(ArtifactRequest request) {
        ArtifactMarkdownResponse markdown = generateWeeklyReport(request);
        insertArtifactHistory(request.getProjectId(), ArtifactType.WEEKLY_REPORT, markdown.getFileName(), request.getGeneratedBy());
        return markdown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArtifactMarkdownResponse selectDailyReportPreviewDetails(ArtifactRequest request) {
        return generateDailyReport(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ArtifactMarkdownResponse insertDailyReportMarkdown(ArtifactRequest request) {
        ArtifactMarkdownResponse markdown = generateDailyReport(request);
        insertArtifactHistory(request.getProjectId(), ArtifactType.DAILY_REPORT, markdown.getFileName(), request.getGeneratedBy());
        return markdown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArtifactMarkdownResponse selectTestResultReportPreviewDetails(ArtifactRequest request) {
        return generateTestResultReport(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ArtifactMarkdownResponse insertTestResultReportMarkdown(ArtifactRequest request) {
        ArtifactMarkdownResponse markdown = generateTestResultReport(request);
        insertArtifactHistory(request.getProjectId(), ArtifactType.TEST_RESULT_REPORT, markdown.getFileName(), request.getGeneratedBy());
        return markdown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ArtifactFileResponse insertTestResultReportExcel(ArtifactRequest request) {
        TestResultReportData data = createTestResultReportData(request);
        String fileName = createTestResultReportFileName(data.getProject(), request.getStartDate(), request.getEndDate(), ".xlsx");
        byte[] content = testResultReportExcelGenerator.generate(data);
        insertArtifactHistory(request.getProjectId(), ArtifactType.TEST_RESULT_REPORT, fileName, request.getGeneratedBy());
        return new ArtifactFileResponse(fileName, EXCEL_CONTENT_TYPE, content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ArtifactHistoryResponse> selectArtifactHistoryList(ArtifactSearchCondition condition) {
        ArtifactSearchCondition safeCondition = condition == null ? new ArtifactSearchCondition() : condition;
        return artifactDao.selectArtifactHistoryList(safeCondition).stream()
                .map(ArtifactHistoryEntity::toResponse)
                .toList();
    }

    /**
     * 주간 업무보고 원천 데이터를 조회하고 Markdown 응답으로 조립합니다.
     *
     * @param request 프로젝트와 기간 조건
     * @return 주간 업무보고 Markdown 응답
     */
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

    /**
     * 기준일의 공수, 이슈, 변경이력을 조합하여 일일 업무보고를 생성합니다.
     *
     * @param request 프로젝트와 기준일 조건
     * @return 일일 업무보고 Markdown 응답
     */
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

    /**
     * 테스트 증적 목록을 기반으로 테스트 결과 보고서를 생성합니다.
     *
     * @param request 프로젝트, 기간, 이슈, 판정 필터 조건
     * @return 테스트 결과 보고서 Markdown 응답
     */
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

    /**
     * 일일 업무보고 생성에 필요한 조회 데이터를 구성합니다.
     *
     * @param request 프로젝트와 기준일 조건
     * @return Markdown 생성기에 전달할 일일 업무보고 데이터
     */
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

    /**
     * 테스트 결과 보고서 생성에 필요한 조회 데이터를 구성합니다.
     *
     * @param request 프로젝트, 기간, 이슈, 판정 필터 조건
     * @return Markdown 및 Excel 생성기에 전달할 테스트 결과 보고서 데이터
     */
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

    /**
     * 주간 업무보고 요청의 필수 조건을 검증합니다.
     *
     * @param request 검증 대상 요청
     */
    private void validateWeeklyReportRequest(ArtifactRequest request) {
        validateProjectAndType(request, ArtifactType.WEEKLY_REPORT);
        validatePeriod(request, "주간 업무보고 생성 요청이 올바르지 않습니다.");
    }

    /**
     * 일일 업무보고 요청의 필수 조건을 검증합니다.
     *
     * @param request 검증 대상 요청
     */
    private void validateDailyReportRequest(ArtifactRequest request) {
        validateProjectAndType(request, ArtifactType.DAILY_REPORT);
        if (request.getBaseDate() == null) {
            throw new BusinessException("일일 업무보고 기준일은 필수입니다.", "ARTIFACT_BASE_DATE_REQUIRED");
        }
    }

    /**
     * 테스트 결과 보고서 요청의 필수 조건을 검증합니다.
     *
     * @param request 검증 대상 요청
     */
    private void validateTestResultReportRequest(ArtifactRequest request) {
        validateProjectAndType(request, ArtifactType.TEST_RESULT_REPORT);
        validatePeriod(request, "테스트 결과 보고서 생성 요청이 올바르지 않습니다.");
    }

    /**
     * 공통으로 필요한 프로젝트 ID와 산출물 유형을 검증합니다.
     *
     * @param request 검증 대상 요청
     * @param artifactType 허용할 산출물 유형
     */
    private void validateProjectAndType(ArtifactRequest request, ArtifactType artifactType) {
        if (request == null || request.getProjectId() == null) {
            throw new BusinessException("산출물 생성 요청이 올바르지 않습니다.", "ARTIFACT_REQUEST_INVALID");
        }
        if (request.getArtifactType() != artifactType) {
            throw new BusinessException("지원하지 않는 산출물 유형입니다.", "ARTIFACT_TYPE_NOT_SUPPORTED");
        }
    }

    /**
     * 기간 기반 산출물의 시작일과 종료일을 검증합니다.
     *
     * @param request 검증 대상 요청
     * @param message 기간 누락 시 사용할 업무 오류 메시지
     */
    private void validatePeriod(ArtifactRequest request, String message) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new BusinessException(message, "ARTIFACT_PERIOD_REQUIRED");
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException("시작일은 종료일보다 늦을 수 없습니다.", "ARTIFACT_PERIOD_INVALID");
        }
    }

    /**
     * 산출물 생성 기준 프로젝트를 조회합니다.
     *
     * @param projectId 프로젝트 ID
     * @return 프로젝트 응답 DTO
     */
    private ProjectResponse selectProject(UUID projectId) {
        return projectDao.selectProjectById(projectId)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다.", "PROJECT_NOT_FOUND"))
                .toResponse();
    }

    /**
     * 생성된 산출물의 이력을 저장합니다.
     *
     * @param projectId 프로젝트 ID
     * @param artifactType 산출물 유형
     * @param fileName 생성 파일명
     * @param generatedBy 생성자 ID
     */
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

    /**
     * 주간 업무보고 파일명을 생성합니다.
     *
     * @param project 프로젝트 정보
     * @param startDate 보고 시작일
     * @param endDate 보고 종료일
     * @return 다운로드 파일명
     */
    private String createWeeklyReportFileName(ProjectResponse project, LocalDate startDate, LocalDate endDate) {
        return "weekly-report-" + safeProjectCode(project) + "-" + startDate + "-" + endDate + ".md";
    }

    /**
     * 일일 업무보고 파일명을 생성합니다.
     *
     * @param project 프로젝트 정보
     * @param baseDate 보고 기준일
     * @return 다운로드 파일명
     */
    private String createDailyReportFileName(ProjectResponse project, LocalDate baseDate) {
        return "daily-report-" + safeProjectCode(project) + "-" + baseDate + ".md";
    }

    /**
     * 테스트 결과 보고서 파일명을 생성합니다.
     *
     * @param project 프로젝트 정보
     * @param startDate 보고 시작일
     * @param endDate 보고 종료일
     * @param extension 파일 확장자
     * @return 다운로드 파일명
     */
    private String createTestResultReportFileName(ProjectResponse project, LocalDate startDate, LocalDate endDate, String extension) {
        return "test-result-report-" + safeProjectCode(project) + "-" + startDate + "-" + endDate + extension;
    }

    /**
     * 파일명에 사용할 수 있도록 프로젝트 코드를 안전한 문자열로 정규화합니다.
     *
     * @param project 프로젝트 정보
     * @return 파일명 조각으로 사용할 프로젝트 코드
     */
    private String safeProjectCode(ProjectResponse project) {
        String projectCode = project.getProjectCode() == null ? "project" : project.getProjectCode();
        return projectCode.replaceAll("[^A-Za-z0-9_-]", "_");
    }
}
