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

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @ModelAttribute("statuses")
    public ProjectStatus[] statuses() {
        return ProjectStatus.values();
    }

    @GetMapping
    public String list(@ModelAttribute ProjectSearchCondition condition, Model model) {
        model.addAttribute("projects", projectService.getProjectList(condition));
        model.addAttribute("condition", condition);
        return "project/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("project", new ProjectRequest());
        return "project/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("project") ProjectRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "project/form";
        }
        projectService.createProject(request);
        return "redirect:/projects";
    }

    @GetMapping("/{projectId}")
    public String detail(@PathVariable UUID projectId, Model model) {
        model.addAttribute("project", projectService.getProject(projectId));
        return "project/detail";
    }

    @GetMapping("/{projectId}/edit")
    public String editForm(@PathVariable UUID projectId, Model model) {
        model.addAttribute("projectId", projectId);
        model.addAttribute("project", projectService.getProject(projectId).toRequest());
        return "project/form";
    }

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

    @PostMapping("/{projectId}/delete")
    public String delete(@PathVariable UUID projectId) {
        projectService.deleteProject(projectId);
        return "redirect:/projects";
    }
}
