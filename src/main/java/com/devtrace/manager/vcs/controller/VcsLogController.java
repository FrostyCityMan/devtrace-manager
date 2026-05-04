package com.devtrace.manager.vcs.controller;

import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.service.ProjectService;
import com.devtrace.manager.vcs.dto.VcsChangeLogResponse;
import com.devtrace.manager.vcs.dto.VcsLogRequest;
import com.devtrace.manager.vcs.dto.VcsType;
import com.devtrace.manager.vcs.service.VcsLogService;
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
@RequestMapping("/vcs-logs")
public class VcsLogController {

    private final VcsLogService vcsLogService;
    private final ProjectService projectService;

    public VcsLogController(VcsLogService vcsLogService, ProjectService projectService) {
        this.vcsLogService = vcsLogService;
        this.projectService = projectService;
    }

    @ModelAttribute("projects")
    public List<ProjectResponse> projects() {
        return projectService.getProjectList(new ProjectSearchCondition());
    }

    @ModelAttribute("vcsTypes")
    public VcsType[] vcsTypes() {
        return VcsType.values();
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("vcsLog", new VcsLogRequest());
        model.addAttribute("logs", List.of());
        return "vcs/form";
    }

    @PostMapping("/preview")
    public String preview(@Valid @ModelAttribute("vcsLog") VcsLogRequest request, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("logs", List.of());
            return "vcs/form";
        }
        List<VcsChangeLogResponse> logs = vcsLogService.selectChangeLogPreviewList(request);
        model.addAttribute("logs", logs);
        return "vcs/form";
    }

    @PostMapping("/download")
    public void download(
            @Valid @ModelAttribute("vcsLog") VcsLogRequest request,
            BindingResult bindingResult,
            HttpServletResponse response
    ) throws IOException {
        if (bindingResult.hasErrors()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "변경이력 Excel 생성 요청이 올바르지 않습니다.");
            return;
        }
        byte[] excel = vcsLogService.selectChangeLogExcel(request);
        String fileName = URLEncoder.encode("vcs-change-log.xlsx", StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
        response.setContentLength(excel.length);
        response.getOutputStream().write(excel);
    }
}
