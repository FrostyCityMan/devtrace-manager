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

/**
 * Thymeleaf 기반 컬럼명세 생성 화면을 제공하는 컨트롤러입니다.
 *
 * <p>DDL 입력, 컬럼명세 미리보기, Excel 다운로드, 기존 컬럼명세 목록 조회를 담당합니다.</p>
 */
@Controller
@RequestMapping("/column-specs")
public class ColumnSpecController {

    private final ColumnSpecService columnSpecService;
    private final ProjectService projectService;

    /**
     * 컬럼명세 화면 컨트롤러를 생성한다.
     *
     * @param columnSpecService 컬럼명세 업무 서비스
     * @param projectService 프로젝트 선택 목록 서비스
     */
    public ColumnSpecController(ColumnSpecService columnSpecService, ProjectService projectService) {
        this.columnSpecService = columnSpecService;
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
     * 지원 DB 유형 목록을 화면 공통 모델로 제공한다.
     *
     * @return DB 유형 배열
     */
    @ModelAttribute("databaseTypes")
    public DatabaseType[] databaseTypes() {
        return DatabaseType.values();
    }

    /**
     * 컬럼명세 생성 화면을 표시한다.
     *
     * @param model Thymeleaf 모델
     * @return 컬럼명세 생성 화면명
     */
    @GetMapping
    public String form(Model model) {
        model.addAttribute("columnSpec", new ColumnSpecRequest());
        model.addAttribute("specs", List.of());
        model.addAttribute("summary", ColumnSpecSummary.from(List.of()));
        return "columnspec/form";
    }

    /**
     * DDL을 분석해 컬럼명세 미리보기를 표시한다.
     *
     * @param request 컬럼명세 생성 요청
     * @param bindingResult 검증 결과
     * @param model Thymeleaf 모델
     * @return 컬럼명세 생성 화면명
     */
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

    /**
     * 컬럼명세 Excel 파일을 다운로드한다.
     *
     * @param request 컬럼명세 생성 요청
     * @param bindingResult 검증 결과
     * @param response 파일 다운로드 응답
     * @throws IOException 응답 스트림 처리 실패 시 발생
     */
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
