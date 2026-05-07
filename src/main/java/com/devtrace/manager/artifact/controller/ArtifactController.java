package com.devtrace.manager.artifact.controller;

import com.devtrace.manager.artifact.dto.ArtifactFileResponse;
import com.devtrace.manager.artifact.dto.ArtifactHistoryResponse;
import com.devtrace.manager.artifact.dto.ArtifactMarkdownResponse;
import com.devtrace.manager.artifact.dto.ArtifactRequest;
import com.devtrace.manager.artifact.dto.ArtifactSearchCondition;
import com.devtrace.manager.artifact.dto.ArtifactType;
import com.devtrace.manager.artifact.service.ArtifactService;
import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.issue.dto.IssueResponse;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import com.devtrace.manager.issue.service.IssueService;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.service.ProjectService;
import com.devtrace.manager.testevidence.dto.TestEvidenceResult;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Thymeleaf 기반 산출물 생성 화면을 제공하는 컨트롤러입니다.
 *
 * <p>하나의 산출물 화면에서 주간 업무보고, 일일 업무보고, 테스트 결과 보고서의
 * 미리보기와 다운로드를 처리합니다. 실제 생성 이력 저장은 서비스 계층에 위임합니다.</p>
 */
@Controller
@RequestMapping("/artifacts")
public class ArtifactController {

    private final ArtifactService artifactService;
    private final ProjectService projectService;
    private final IssueService issueService;

    /**
     * 산출물 화면 컨트롤러를 생성합니다.
     *
     * @param artifactService 산출물 생성 서비스
     * @param projectService 프로젝트 선택 목록 조회 서비스
     * @param issueService 테스트 결과 보고서 이슈 필터 조회 서비스
     */
    public ArtifactController(ArtifactService artifactService, ProjectService projectService, IssueService issueService) {
        this.artifactService = artifactService;
        this.projectService = projectService;
        this.issueService = issueService;
    }

    /**
     * 산출물 화면의 프로젝트 선택 목록을 제공합니다.
     *
     * @return 전체 프로젝트 목록
     */
    @ModelAttribute("projects")
    public List<ProjectResponse> projects() {
        return projectService.getProjectList(new ProjectSearchCondition());
    }

    /**
     * 산출물 화면의 이슈 선택 목록을 제공합니다.
     *
     * @return 전체 이슈 목록
     */
    @ModelAttribute("issues")
    public List<IssueResponse> issues() {
        return issueService.selectIssueList(new IssueSearchCondition());
    }

    /**
     * 산출물 유형 선택값을 제공합니다.
     *
     * @return 지원 산출물 유형 배열
     */
    @ModelAttribute("artifactTypes")
    public ArtifactType[] artifactTypes() {
        return ArtifactType.values();
    }

    /**
     * 테스트 결과 보고서 판정 필터 값을 제공합니다.
     *
     * @return 테스트 증적 판정 배열
     */
    @ModelAttribute("resultStatuses")
    public TestEvidenceResult[] resultStatuses() {
        return TestEvidenceResult.values();
    }

    /**
     * 산출물 생성 기본 화면을 표시합니다.
     *
     * @param model 화면 모델
     * @return 산출물 화면 템플릿
     */
    @GetMapping
    public String form(Model model) {
        model.addAttribute("artifact", createDefaultRequest());
        addMarkdown(model, ArtifactMarkdownResponse.empty());
        addHistories(model, null);
        return "artifact/weekly-report";
    }

    /**
     * 주간 업무보고 Markdown을 미리보기로 생성합니다.
     *
     * @param request 산출물 생성 요청
     * @param bindingResult 입력 검증 결과
     * @param model 화면 모델
     * @return 산출물 화면 템플릿
     */
    @PostMapping("/weekly-report/preview")
    public String previewWeeklyReport(
            @Valid @ModelAttribute("artifact") ArtifactRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        request.setArtifactType(ArtifactType.WEEKLY_REPORT);
        return previewMarkdown(bindingResult, model, request, () -> artifactService.selectWeeklyReportPreviewDetails(request));
    }

    /**
     * 주간 업무보고 Markdown 파일을 다운로드합니다.
     *
     * @param request 산출물 생성 요청
     * @param bindingResult 입력 검증 결과
     * @param response 파일 응답
     * @throws IOException 응답 스트림 쓰기 실패 시
     */
    @PostMapping("/weekly-report/download")
    public void downloadWeeklyReport(
            @Valid @ModelAttribute("artifact") ArtifactRequest request,
            BindingResult bindingResult,
            HttpServletResponse response
    ) throws IOException {
        request.setArtifactType(ArtifactType.WEEKLY_REPORT);
        downloadMarkdown(bindingResult, response, "주간 업무보고 Markdown 생성 요청이 올바르지 않습니다.", () -> artifactService.insertWeeklyReportMarkdown(request));
    }

    /**
     * 일일 업무보고 Markdown을 미리보기로 생성합니다.
     *
     * @param request 산출물 생성 요청
     * @param bindingResult 입력 검증 결과
     * @param model 화면 모델
     * @return 산출물 화면 템플릿
     */
    @PostMapping("/daily-report/preview")
    public String previewDailyReport(
            @Valid @ModelAttribute("artifact") ArtifactRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        request.setArtifactType(ArtifactType.DAILY_REPORT);
        return previewMarkdown(bindingResult, model, request, () -> artifactService.selectDailyReportPreviewDetails(request));
    }

    /**
     * 일일 업무보고 Markdown 파일을 다운로드합니다.
     *
     * @param request 산출물 생성 요청
     * @param bindingResult 입력 검증 결과
     * @param response 파일 응답
     * @throws IOException 응답 스트림 쓰기 실패 시
     */
    @PostMapping("/daily-report/download")
    public void downloadDailyReport(
            @Valid @ModelAttribute("artifact") ArtifactRequest request,
            BindingResult bindingResult,
            HttpServletResponse response
    ) throws IOException {
        request.setArtifactType(ArtifactType.DAILY_REPORT);
        downloadMarkdown(bindingResult, response, "일일 업무보고 Markdown 생성 요청이 올바르지 않습니다.", () -> artifactService.insertDailyReportMarkdown(request));
    }

    /**
     * 테스트 결과 보고서 Markdown을 미리보기로 생성합니다.
     *
     * @param request 산출물 생성 요청
     * @param bindingResult 입력 검증 결과
     * @param model 화면 모델
     * @return 산출물 화면 템플릿
     */
    @PostMapping("/test-result/preview")
    public String previewTestResultReport(
            @Valid @ModelAttribute("artifact") ArtifactRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        request.setArtifactType(ArtifactType.TEST_RESULT_REPORT);
        return previewMarkdown(bindingResult, model, request, () -> artifactService.selectTestResultReportPreviewDetails(request));
    }

    /**
     * 테스트 결과 보고서 Markdown 파일을 다운로드합니다.
     *
     * @param request 산출물 생성 요청
     * @param bindingResult 입력 검증 결과
     * @param response 파일 응답
     * @throws IOException 응답 스트림 쓰기 실패 시
     */
    @PostMapping("/test-result/download-markdown")
    public void downloadTestResultReportMarkdown(
            @Valid @ModelAttribute("artifact") ArtifactRequest request,
            BindingResult bindingResult,
            HttpServletResponse response
    ) throws IOException {
        request.setArtifactType(ArtifactType.TEST_RESULT_REPORT);
        downloadMarkdown(bindingResult, response, "테스트 결과 보고서 Markdown 생성 요청이 올바르지 않습니다.", () -> artifactService.insertTestResultReportMarkdown(request));
    }

    /**
     * 테스트 결과 보고서 Excel 파일을 다운로드합니다.
     *
     * @param request 산출물 생성 요청
     * @param bindingResult 입력 검증 결과
     * @param response 파일 응답
     * @throws IOException 응답 스트림 쓰기 실패 시
     */
    @PostMapping("/test-result/download-excel")
    public void downloadTestResultReportExcel(
            @Valid @ModelAttribute("artifact") ArtifactRequest request,
            BindingResult bindingResult,
            HttpServletResponse response
    ) throws IOException {
        request.setArtifactType(ArtifactType.TEST_RESULT_REPORT);
        if (bindingResult.hasErrors()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "테스트 결과 보고서 Excel 생성 요청이 올바르지 않습니다.");
            return;
        }
        ArtifactFileResponse file;
        try {
            file = artifactService.insertTestResultReportExcel(request);
        } catch (BusinessException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            return;
        }
        writeFile(response, file.getFileName(), file.getContentType(), file.getContent());
    }

    /**
     * Markdown 미리보기 요청의 공통 화면 흐름을 처리합니다.
     *
     * @param bindingResult 입력 검증 결과
     * @param model 화면 모델
     * @param request 산출물 생성 요청
     * @param supplier 산출물 생성 함수
     * @return 산출물 화면 템플릿
     */
    private String previewMarkdown(
            BindingResult bindingResult,
            Model model,
            ArtifactRequest request,
            MarkdownSupplier supplier
    ) {
        if (bindingResult.hasErrors()) {
            addMarkdown(model, ArtifactMarkdownResponse.empty());
            addHistories(model, request.getProjectId());
            return "artifact/weekly-report";
        }

        try {
            addMarkdown(model, supplier.get());
        } catch (BusinessException ex) {
            addMarkdown(model, ArtifactMarkdownResponse.empty());
            model.addAttribute("artifactError", ex.getMessage());
        }
        addHistories(model, request.getProjectId());
        return "artifact/weekly-report";
    }

    /**
     * Markdown 다운로드 요청의 공통 응답 흐름을 처리합니다.
     *
     * @param bindingResult 입력 검증 결과
     * @param response 파일 응답
     * @param invalidMessage 입력 오류 메시지
     * @param supplier 산출물 생성 함수
     * @throws IOException 응답 스트림 쓰기 실패 시
     */
    private void downloadMarkdown(
            BindingResult bindingResult,
            HttpServletResponse response,
            String invalidMessage,
            MarkdownSupplier supplier
    ) throws IOException {
        if (bindingResult.hasErrors()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, invalidMessage);
            return;
        }

        ArtifactMarkdownResponse markdown;
        try {
            markdown = supplier.get();
        } catch (BusinessException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            return;
        }

        writeFile(response, markdown.getFileName(), "text/markdown;charset=UTF-8", markdown.getContent().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 산출물 화면 최초 진입 시 사용할 기본 검색 조건을 생성합니다.
     *
     * @return 기본 산출물 생성 요청
     */
    private ArtifactRequest createDefaultRequest() {
        LocalDate today = LocalDate.now();
        ArtifactRequest request = new ArtifactRequest();
        request.setArtifactType(ArtifactType.WEEKLY_REPORT);
        request.setBaseDate(today);
        request.setStartDate(today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
        request.setEndDate(today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));
        return request;
    }

    /**
     * Markdown 미리보기 결과를 화면 모델에 추가합니다.
     *
     * @param model 화면 모델
     * @param markdown Markdown 응답
     */
    private void addMarkdown(Model model, ArtifactMarkdownResponse markdown) {
        model.addAttribute("markdown", markdown.getContent());
        model.addAttribute("fileName", markdown.getFileName());
        model.addAttribute("markdownSummary", markdown);
    }

    /**
     * 산출물 생성 이력 목록을 화면 모델에 추가합니다.
     *
     * @param model 화면 모델
     * @param projectId 프로젝트 ID
     */
    private void addHistories(Model model, UUID projectId) {
        ArtifactSearchCondition condition = new ArtifactSearchCondition();
        condition.setProjectId(projectId);
        List<ArtifactHistoryResponse> histories = artifactService.selectArtifactHistoryList(condition);
        model.addAttribute("histories", histories);
    }

    /**
     * 브라우저 다운로드 응답을 작성합니다.
     *
     * @param response HTTP 응답
     * @param fileName 다운로드 파일명
     * @param contentType MIME 타입
     * @param content 파일 본문
     * @throws IOException 응답 스트림 쓰기 실패 시
     */
    private void writeFile(HttpServletResponse response, String fileName, String contentType, byte[] content) throws IOException {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
        response.setContentLength(content.length);
        response.getOutputStream().write(content);
    }

    /**
     * Markdown 생성 로직을 공통 흐름에 전달하기 위한 함수형 인터페이스입니다.
     */
    @FunctionalInterface
    private interface MarkdownSupplier {

        /**
         * 산출물 Markdown을 생성합니다.
         *
         * @return Markdown 응답
         */
        ArtifactMarkdownResponse get();
    }
}
