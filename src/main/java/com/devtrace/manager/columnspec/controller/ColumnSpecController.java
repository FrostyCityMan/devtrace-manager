package com.devtrace.manager.columnspec.controller;

import com.devtrace.manager.columnspec.dto.ColumnSpecRequest;
import com.devtrace.manager.columnspec.dto.ColumnSpecResponse;
import com.devtrace.manager.columnspec.dto.ColumnSpecSummary;
import com.devtrace.manager.columnspec.dto.DatabaseType;
import com.devtrace.manager.columnspec.service.ColumnSpecService;
import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.service.ProjectService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/column-specs")
public class ColumnSpecController {

    private final ColumnSpecService columnSpecService;
    private final ProjectService projectService;

    public ColumnSpecController(ColumnSpecService columnSpecService, ProjectService projectService) {
        this.columnSpecService = columnSpecService;
        this.projectService = projectService;
    }

    @ModelAttribute("projects")
    public List<ProjectResponse> projects() {
        return projectService.getProjectList(new ProjectSearchCondition());
    }

    @ModelAttribute("databaseTypes")
    public DatabaseType[] databaseTypes() {
        return DatabaseType.values();
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("columnSpec", new ColumnSpecRequest());
        model.addAttribute("specs", List.of());
        model.addAttribute("summary", ColumnSpecSummary.from(List.of()));
        return "columnspec/form";
    }

    @PostMapping("/preview")
    public String preview(
            @Valid @ModelAttribute("columnSpec") ColumnSpecRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("specs", List.of());
            model.addAttribute("summary", ColumnSpecSummary.from(List.of()));
            return "columnspec/form";
        }

        List<ColumnSpecResponse> specs;
        try {
            specs = columnSpecService.selectColumnSpecPreviewList(request);
        } catch (BusinessException ex) {
            model.addAttribute("specs", List.of());
            model.addAttribute("summary", ColumnSpecSummary.from(List.of()));
            model.addAttribute("columnSpecError", ex.getMessage());
            return "columnspec/form";
        }

        model.addAttribute("specs", specs);
        model.addAttribute("summary", ColumnSpecSummary.from(specs));
        return "columnspec/form";
    }

    @PostMapping("/download")
    public void download(
            @Valid @ModelAttribute("columnSpec") ColumnSpecRequest request,
            BindingResult bindingResult,
            HttpServletResponse response
    ) throws IOException {
        if (bindingResult.hasErrors()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "컬럼명세 Excel 생성 요청이 올바르지 않습니다.");
            return;
        }

        byte[] excel;
        try {
            excel = columnSpecService.selectColumnSpecExcel(request);
        } catch (BusinessException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            return;
        }

        String fileName = URLEncoder.encode("column-spec.xlsx", StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
        response.setContentLength(excel.length);
        response.getOutputStream().write(excel);
    }
}
