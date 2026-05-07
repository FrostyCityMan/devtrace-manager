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

/**
 * Thymeleaf 기반 형상관리 변경이력 분석 화면을 제공하는 컨트롤러입니다.
 *
 * <p>Git/SVN 로그 입력, 변경이력 미리보기, Excel 다운로드 화면 흐름을 담당합니다.</p>
 */
@Controller
@RequestMapping("/vcs-logs")
public class VcsLogController {

    private final VcsLogService vcsLogService;
    private final ProjectService projectService;

    /**
     * VCS 변경이력 화면 컨트롤러를 생성한다.
     *
     * @param vcsLogService VCS 변경이력 업무 서비스
     * @param projectService 프로젝트 선택 목록 서비스
     */
    public VcsLogController(VcsLogService vcsLogService, ProjectService projectService) {
        this.vcsLogService = vcsLogService;
        this.projectService = projectService;
    }

    /**
     * 프로젝트 선택 목록을 화면 공통 모델로 제공한다.
     *
     * @return 프로젝트 목록
     */
    @ModelAttribute("projects")
    public List<ProjectResponse> projects() {
        return projectService.getProjectList(new ProjectSearchCondition());
    }

    /**
     * 지원 VCS 유형 목록을 화면 공통 모델로 제공한다.
     *
     * @return VCS 유형 배열
     */
    @ModelAttribute("vcsTypes")
    public VcsType[] vcsTypes() {
        return VcsType.values();
    }

    /**
     * VCS 로그 분석 화면을 표시한다.
     *
     * @param model Thymeleaf 모델
     * @return VCS 로그 분석 화면명
     */
    @GetMapping
    public String form(Model model) {
        model.addAttribute("vcsLog", new VcsLogRequest());
        model.addAttribute("logs", List.of());
        return "vcs/form";
    }

    /**
     * VCS 로그 텍스트를 분석해 변경이력 미리보기를 표시한다.
     *
     * @param request VCS 로그 분석 요청
     * @param bindingResult 검증 결과
     * @param model Thymeleaf 모델
     * @return VCS 로그 분석 화면명
     */
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

    /**
     * 변경이력 Excel 파일을 다운로드한다.
     *
     * @param request VCS 로그 분석 요청
     * @param bindingResult 검증 결과
     * @param response 파일 다운로드 응답
     * @throws IOException 응답 스트림 처리 실패 시 발생
     */
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
