package com.devtrace.manager.sprint.controller;

import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.service.ProjectService;
import com.devtrace.manager.sprint.dto.SprintBacklogSearchCondition;
import com.devtrace.manager.sprint.dto.SprintIssueRequest;
import com.devtrace.manager.sprint.dto.SprintIssueResponse;
import com.devtrace.manager.sprint.dto.SprintRequest;
import com.devtrace.manager.sprint.dto.SprintResponse;
import com.devtrace.manager.sprint.dto.SprintSearchCondition;
import com.devtrace.manager.sprint.dto.SprintStatus;
import com.devtrace.manager.sprint.dto.SprintSummaryResponse;
import com.devtrace.manager.sprint.service.SprintService;
import jakarta.validation.Valid;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/sprints")
public class SprintController {

    private final SprintService sprintService;
    private final ProjectService projectService;

    public SprintController(SprintService sprintService, ProjectService projectService) {
        this.sprintService = sprintService;
        this.projectService = projectService;
    }

    @ModelAttribute("statuses")
    public SprintStatus[] statuses() {
        return SprintStatus.values();
    }

    @ModelAttribute("projects")
    public List<ProjectResponse> projects() {
        return projectService.getProjectList(new ProjectSearchCondition());
    }

    @GetMapping
    public String list(
            @ModelAttribute SprintSearchCondition condition,
            @RequestParam(required = false) UUID sprintId,
            Model model
    ) {
        populateModel(condition, sprintId, model);
        return "sprint/list";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("newSprint") SprintRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        SprintSearchCondition condition = new SprintSearchCondition();
        condition.setProjectId(request.getProjectId());
        if (bindingResult.hasErrors()) {
            populateModel(condition, null, model);
            return "sprint/list";
        }
        SprintResponse sprint = sprintService.insertSprint(request);
        return redirectToSprint(sprint);
    }

    @PostMapping("/{sprintId}")
    public String update(
            @PathVariable UUID sprintId,
            @Valid @ModelAttribute("selectedSprintRequest") SprintRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        SprintSearchCondition condition = new SprintSearchCondition();
        condition.setProjectId(request.getProjectId());
        if (bindingResult.hasErrors()) {
            populateModel(condition, sprintId, model);
            return "sprint/list";
        }
        SprintResponse sprint = sprintService.updateSprint(sprintId, request);
        return redirectToSprint(sprint);
    }

    @PostMapping("/{sprintId}/start")
    public String start(@PathVariable UUID sprintId) {
        SprintResponse sprint = sprintService.updateSprintStart(sprintId);
        return redirectToSprint(sprint);
    }

    @PostMapping("/{sprintId}/close")
    public String close(@PathVariable UUID sprintId) {
        SprintResponse sprint = sprintService.updateSprintClose(sprintId);
        return redirectToSprint(sprint);
    }

    @PostMapping("/{sprintId}/delete")
    public String delete(@PathVariable UUID sprintId) {
        SprintResponse sprint = sprintService.selectSprintDetails(sprintId);
        sprintService.deleteSprint(sprintId);
        return "redirect:/sprints?projectId=" + sprint.getProjectId();
    }

    @PostMapping("/{sprintId}/issues")
    public String assignIssue(@PathVariable UUID sprintId, @Valid @ModelAttribute("sprintIssue") SprintIssueRequest request) {
        sprintService.insertSprintIssue(sprintId, request);
        SprintResponse sprint = sprintService.selectSprintDetails(sprintId);
        return redirectToSprint(sprint);
    }

    @PostMapping("/{sprintId}/issues/{issueId}/delete")
    public String deleteIssue(@PathVariable UUID sprintId, @PathVariable UUID issueId) {
        sprintService.deleteSprintIssue(sprintId, issueId);
        SprintResponse sprint = sprintService.selectSprintDetails(sprintId);
        return redirectToSprint(sprint);
    }

    private void populateModel(SprintSearchCondition condition, UUID requestedSprintId, Model model) {
        SprintSearchCondition searchCondition = condition == null ? new SprintSearchCondition() : condition;
        UUID selectedProjectId = selectProjectId(searchCondition.getProjectId());
        searchCondition.setProjectId(selectedProjectId);

        List<SprintResponse> sprints = selectedProjectId == null ? List.of() : sprintService.selectSprintList(searchCondition);
        SprintResponse selectedSprint = selectSprint(requestedSprintId, sprints);
        List<SprintIssueResponse> sprintIssues = selectedSprint == null
                ? List.of()
                : sprintService.selectSprintIssueList(selectedSprint.getSprintId());
        SprintSummaryResponse summary = selectedSprint == null
                ? new SprintSummaryResponse()
                : sprintService.selectSprintSummaryDetails(selectedSprint.getSprintId());

        SprintBacklogSearchCondition backlogCondition = new SprintBacklogSearchCondition();
        backlogCondition.setProjectId(selectedProjectId);
        backlogCondition.setKeyword(searchCondition.getKeyword());

        model.addAttribute("condition", searchCondition);
        model.addAttribute("sprints", sprints);
        model.addAttribute("selectedSprint", selectedSprint);
        model.addAttribute("selectedSprintId", selectedSprint == null ? null : selectedSprint.getSprintId());
        if (!model.containsAttribute("selectedSprintRequest")) {
            model.addAttribute("selectedSprintRequest", selectedSprint == null ? createDefaultRequest(selectedProjectId) : selectedSprint.toRequest());
        }
        if (!model.containsAttribute("newSprint")) {
            model.addAttribute("newSprint", createDefaultRequest(selectedProjectId));
        }
        model.addAttribute("sprintIssue", new SprintIssueRequest());
        model.addAttribute("backlogIssues", selectedProjectId == null ? List.of() : sprintService.selectBacklogIssueList(backlogCondition));
        model.addAttribute("sprintIssues", sprintIssues);
        model.addAttribute("summary", summary);
    }

    private UUID selectProjectId(UUID requestedProjectId) {
        if (requestedProjectId != null) {
            return requestedProjectId;
        }
        List<ProjectResponse> projectList = projects();
        return projectList.isEmpty() ? null : projectList.get(0).getProjectId();
    }

    private SprintResponse selectSprint(UUID requestedSprintId, List<SprintResponse> sprints) {
        if (requestedSprintId != null) {
            return sprintService.selectSprintDetails(requestedSprintId);
        }
        return sprints.isEmpty() ? null : sprints.get(0);
    }

    private SprintRequest createDefaultRequest(UUID projectId) {
        LocalDate today = LocalDate.now();
        SprintRequest request = new SprintRequest();
        request.setProjectId(projectId);
        request.setStatus(SprintStatus.PLANNED);
        request.setStartDate(today);
        request.setEndDate(today.plusDays(13));
        return request;
    }

    private String redirectToSprint(SprintResponse sprint) {
        return "redirect:/sprints?projectId=" + sprint.getProjectId() + "&sprintId=" + sprint.getSprintId();
    }
}
