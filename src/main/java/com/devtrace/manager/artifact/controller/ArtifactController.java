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

@Controller
@RequestMapping("/artifacts")
public class ArtifactController {

    private final ArtifactService artifactService;
    private final ProjectService projectService;
    private final IssueService issueService;

    public ArtifactController(ArtifactService artifactService, ProjectService projectService, IssueService issueService) {
        this.artifactService = artifactService;
        this.projectService = projectService;
        this.issueService = issueService;
    }

    @ModelAttribute("projects")
    public List<ProjectResponse> projects() {
        return projectService.getProjectList(new ProjectSearchCondition());
    }

    @ModelAttribute("issues")
    public List<IssueResponse> issues() {
        return issueService.selectIssueList(new IssueSearchCondition());
    }

    @ModelAttribute("artifactTypes")
    public ArtifactType[] artifactTypes() {
        return ArtifactType.values();
    }

    @ModelAttribute("resultStatuses")
    public TestEvidenceResult[] resultStatuses() {
        return TestEvidenceResult.values();
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("artifact", createDefaultRequest());
        addMarkdown(model, ArtifactMarkdownResponse.empty());
        addHistories(model, null);
        return "artifact/weekly-report";
    }

    @PostMapping("/weekly-report/preview")
    public String previewWeeklyReport(
            @Valid @ModelAttribute("artifact") ArtifactRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        request.setArtifactType(ArtifactType.WEEKLY_REPORT);
        return previewMarkdown(bindingResult, model, request, () -> artifactService.selectWeeklyReportPreviewDetails(request));
    }

    @PostMapping("/weekly-report/download")
    public void downloadWeeklyReport(
            @Valid @ModelAttribute("artifact") ArtifactRequest request,
            BindingResult bindingResult,
            HttpServletResponse response
    ) throws IOException {
        request.setArtifactType(ArtifactType.WEEKLY_REPORT);
        downloadMarkdown(bindingResult, response, "주간 업무보고 Markdown 생성 요청이 올바르지 않습니다.", () -> artifactService.insertWeeklyReportMarkdown(request));
    }

    @PostMapping("/daily-report/preview")
    public String previewDailyReport(
            @Valid @ModelAttribute("artifact") ArtifactRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        request.setArtifactType(ArtifactType.DAILY_REPORT);
        return previewMarkdown(bindingResult, model, request, () -> artifactService.selectDailyReportPreviewDetails(request));
    }

    @PostMapping("/daily-report/download")
    public void downloadDailyReport(
            @Valid @ModelAttribute("artifact") ArtifactRequest request,
            BindingResult bindingResult,
            HttpServletResponse response
    ) throws IOException {
        request.setArtifactType(ArtifactType.DAILY_REPORT);
        downloadMarkdown(bindingResult, response, "일일 업무보고 Markdown 생성 요청이 올바르지 않습니다.", () -> artifactService.insertDailyReportMarkdown(request));
    }

    @PostMapping("/test-result/preview")
    public String previewTestResultReport(
            @Valid @ModelAttribute("artifact") ArtifactRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        request.setArtifactType(ArtifactType.TEST_RESULT_REPORT);
        return previewMarkdown(bindingResult, model, request, () -> artifactService.selectTestResultReportPreviewDetails(request));
    }

    @PostMapping("/test-result/download-markdown")
    public void downloadTestResultReportMarkdown(
            @Valid @ModelAttribute("artifact") ArtifactRequest request,
            BindingResult bindingResult,
            HttpServletResponse response
    ) throws IOException {
        request.setArtifactType(ArtifactType.TEST_RESULT_REPORT);
        downloadMarkdown(bindingResult, response, "테스트 결과 보고서 Markdown 생성 요청이 올바르지 않습니다.", () -> artifactService.insertTestResultReportMarkdown(request));
    }

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

    private ArtifactRequest createDefaultRequest() {
        LocalDate today = LocalDate.now();
        ArtifactRequest request = new ArtifactRequest();
        request.setArtifactType(ArtifactType.WEEKLY_REPORT);
        request.setBaseDate(today);
        request.setStartDate(today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
        request.setEndDate(today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));
        return request;
    }

    private void addMarkdown(Model model, ArtifactMarkdownResponse markdown) {
        model.addAttribute("markdown", markdown.getContent());
        model.addAttribute("fileName", markdown.getFileName());
        model.addAttribute("markdownSummary", markdown);
    }

    private void addHistories(Model model, UUID projectId) {
        ArtifactSearchCondition condition = new ArtifactSearchCondition();
        condition.setProjectId(projectId);
        List<ArtifactHistoryResponse> histories = artifactService.selectArtifactHistoryList(condition);
        model.addAttribute("histories", histories);
    }

    private void writeFile(HttpServletResponse response, String fileName, String contentType, byte[] content) throws IOException {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
        response.setContentLength(content.length);
        response.getOutputStream().write(content);
    }

    @FunctionalInterface
    private interface MarkdownSupplier {
        ArtifactMarkdownResponse get();
    }
}
