package com.devtrace.manager.project.controller;

import com.devtrace.manager.project.dto.ProjectRequest;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.dto.ProjectStatus;
import com.devtrace.manager.project.service.ProjectService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Thymeleaf 기반 프로젝트 관리 화면을 제공하는 컨트롤러입니다.
 *
 * <p>프로젝트 목록, 등록, 상세, 수정, 삭제 화면 흐름을 담당합니다.</p>
 */
@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 프로젝트 화면 컨트롤러를 생성한다.
     *
     * @param projectService 프로젝트 업무 서비스
     */
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * 프로젝트 상태 선택 목록을 화면 공통 모델로 제공한다.
     *
     * @return 프로젝트 상태 배열
     */
    @ModelAttribute("statuses")
    public ProjectStatus[] statuses() {
        return ProjectStatus.values();
    }

    /**
     * 프로젝트 목록 화면을 표시한다.
     *
     * <p>검색어와 상태 필터를 기준으로 프로젝트 목록을 조회한다.</p>
     *
     * @param condition 프로젝트 검색 조건
     * @param model Thymeleaf 모델
     * @return 프로젝트 목록 화면명
     */
    @GetMapping
    public String list(@ModelAttribute ProjectSearchCondition condition, Model model) {
        model.addAttribute("projects", projectService.getProjectList(condition));
        model.addAttribute("condition", condition);
        return "project/list";
    }

    /**
     * 프로젝트 등록 화면을 표시한다.
     *
     * @param model Thymeleaf 모델
     * @return 프로젝트 입력 화면명
     */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("project", new ProjectRequest());
        return "project/form";
    }

    /**
     * 프로젝트를 등록한다.
     *
     * @param request 프로젝트 등록 요청
     * @param bindingResult 검증 결과
     * @return 목록 화면 리다이렉트 또는 입력 화면명
     */
    @PostMapping
    public String create(@Valid @ModelAttribute("project") ProjectRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "project/form";
        }
        projectService.createProject(request);
        return "redirect:/projects";
    }

    /**
     * 프로젝트 상세 화면을 표시한다.
     *
     * @param projectId 조회 대상 프로젝트 ID
     * @param model Thymeleaf 모델
     * @return 프로젝트 상세 화면명
     */
    @GetMapping("/{projectId}")
    public String detail(@PathVariable UUID projectId, Model model) {
        model.addAttribute("project", projectService.getProject(projectId));
        return "project/detail";
    }

    /**
     * 프로젝트 수정 화면을 표시한다.
     *
     * @param projectId 수정 대상 프로젝트 ID
     * @param model Thymeleaf 모델
     * @return 프로젝트 입력 화면명
     */
    @GetMapping("/{projectId}/edit")
    public String editForm(@PathVariable UUID projectId, Model model) {
        model.addAttribute("projectId", projectId);
        model.addAttribute("project", projectService.getProject(projectId).toRequest());
        return "project/form";
    }

    /**
     * 프로젝트를 수정한다.
     *
     * @param projectId 수정 대상 프로젝트 ID
     * @param request 프로젝트 수정 요청
     * @param bindingResult 검증 결과
     * @param model Thymeleaf 모델
     * @return 상세 화면 리다이렉트 또는 입력 화면명
     */
    @PostMapping("/{projectId}")
    public String update(
            @PathVariable UUID projectId,
            @Valid @ModelAttribute("project") ProjectRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("projectId", projectId);
            return "project/form";
        }
        projectService.updateProject(projectId, request);
        return "redirect:/projects/" + projectId;
    }

    /**
     * 프로젝트를 삭제한다.
     *
     * @param projectId 삭제 대상 프로젝트 ID
     * @return 프로젝트 목록 화면 리다이렉트
     */
    @PostMapping("/{projectId}/delete")
    public String delete(@PathVariable UUID projectId) {
        projectService.deleteProject(projectId);
        return "redirect:/projects";
    }
}
