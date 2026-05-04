package com.devtrace.manager.issue.controller;

import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.issue.dto.IssueRequest;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import com.devtrace.manager.issue.dto.IssueStatus;
import com.devtrace.manager.issue.dto.IssueType;
import com.devtrace.manager.issue.service.IssueService;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.service.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/issues")
public class IssueController {

    private final IssueService issueService;
    private final ProjectService projectService;

    public IssueController(IssueService issueService, ProjectService projectService) {
        this.issueService = issueService;
        this.projectService = projectService;
    }

    @ModelAttribute("issueStatuses")
    public IssueStatus[] issueStatuses() {
        return IssueStatus.values();
    }

    @ModelAttribute("issueTypes")
    public IssueType[] issueTypes() {
        return IssueType.values();
    }

    @ModelAttribute("priorities")
    public IssuePriority[] priorities() {
        return IssuePriority.values();
    }

    @ModelAttribute("projects")
    public List<ProjectResponse> projects() {
        return projectService.getProjectList(new ProjectSearchCondition());
    }

    @GetMapping
    public String list(@ModelAttribute IssueSearchCondition condition, Model model) {
        model.addAttribute("issues", issueService.getIssueList(condition));
        model.addAttribute("condition", condition);
        return "issue/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("issue", new IssueRequest());
        return "issue/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("issue") IssueRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "issue/form";
        }
        issueService.createIssue(request);
        return "redirect:/issues";
    }

    @GetMapping("/{issueId}")
    public String detail(@PathVariable UUID issueId, Model model) {
        model.addAttribute("issue", issueService.getIssue(issueId));
        return "issue/detail";
    }

    @GetMapping("/{issueId}/edit")
    public String editForm(@PathVariable UUID issueId, Model model) {
        model.addAttribute("issueId", issueId);
        model.addAttribute("issue", issueService.getIssue(issueId).toRequest());
        return "issue/form";
    }

    @PostMapping("/{issueId}")
    public String update(
            @PathVariable UUID issueId,
            @Valid @ModelAttribute("issue") IssueRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("issueId", issueId);
            return "issue/form";
        }
        issueService.updateIssue(issueId, request);
        return "redirect:/issues/" + issueId;
    }

    @PostMapping("/{issueId}/delete")
    public String delete(@PathVariable UUID issueId) {
        issueService.deleteIssue(issueId);
        return "redirect:/issues";
    }
}
