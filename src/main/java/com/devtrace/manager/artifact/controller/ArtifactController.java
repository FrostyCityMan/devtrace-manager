package com.devtrace.manager.artifact.controller;

import com.devtrace.manager.artifact.dto.ArtifactHistoryResponse;
import com.devtrace.manager.artifact.dto.ArtifactMarkdownResponse;
import com.devtrace.manager.artifact.dto.ArtifactRequest;
import com.devtrace.manager.artifact.dto.ArtifactSearchCondition;
import com.devtrace.manager.artifact.dto.ArtifactType;
import com.devtrace.manager.artifact.service.ArtifactService;
import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.service.ProjectService;
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

    public ArtifactController(ArtifactService artifactService, ProjectService projectService) {
        this.artifactService = artifactService;
        this.projectService = projectService;
    }

    @ModelAttribute("projects")
    public List<ProjectResponse> projects() {
        return projectService.getProjectList(new ProjectSearchCondition());
    }

    @ModelAttribute("artifactTypes")
    public ArtifactType[] artifactTypes() {
        return ArtifactType.values();
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("artifact", createDefaultRequest());
        addMarkdown(model, ArtifactMarkdownResponse.empty());
        addHistories(model, null);
        return "artifact/weekly-report";
    }

    @PostMapping("/weekly-report/preview")
    public String preview(
            @Valid @ModelAttribute("artifact") ArtifactRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            addMarkdown(model, ArtifactMarkdownResponse.empty());
            addHistories(model, request.getProjectId());
            return "artifact/weekly-report";
        }

        try {
            ArtifactMarkdownResponse markdown = artifactService.selectWeeklyReportPreviewDetails(request);
            addMarkdown(model, markdown);
        } catch (BusinessException ex) {
            addMarkdown(model, ArtifactMarkdownResponse.empty());
            model.addAttribute("artifactError", ex.getMessage());
        }
        addHistories(model, request.getProjectId());
        return "artifact/weekly-report";
    }

    @PostMapping("/weekly-report/download")
    public void download(
            @Valid @ModelAttribute("artifact") ArtifactRequest request,
            BindingResult bindingResult,
            HttpServletResponse response
    ) throws IOException {
        if (bindingResult.hasErrors()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "주간 업무보고 Markdown 생성 요청이 올바르지 않습니다.");
            return;
        }

        ArtifactMarkdownResponse markdown;
        try {
            markdown = artifactService.insertWeeklyReportMarkdown(request);
        } catch (BusinessException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            return;
        }

        byte[] content = markdown.getContent().getBytes(StandardCharsets.UTF_8);
        String fileName = URLEncoder.encode(markdown.getFileName(), StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType("text/markdown;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
        response.setContentLength(content.length);
        response.getOutputStream().write(content);
    }

    private ArtifactRequest createDefaultRequest() {
        LocalDate today = LocalDate.now();
        ArtifactRequest request = new ArtifactRequest();
        request.setArtifactType(ArtifactType.WEEKLY_REPORT);
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
        condition.setArtifactType(ArtifactType.WEEKLY_REPORT);
        List<ArtifactHistoryResponse> histories = artifactService.selectArtifactHistoryList(condition);
        model.addAttribute("histories", histories);
    }
}
