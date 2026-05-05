package com.devtrace.manager.wbs.controller;

import com.devtrace.manager.issue.dto.IssueResponse;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import com.devtrace.manager.issue.service.IssueService;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.service.ProjectService;
import com.devtrace.manager.wbs.dto.WbsDependencyType;
import com.devtrace.manager.wbs.dto.WbsGanttResponse;
import com.devtrace.manager.wbs.dto.WbsTaskDependencyRequest;
import com.devtrace.manager.wbs.dto.WbsTaskDependencyResponse;
import com.devtrace.manager.wbs.dto.WbsTaskDependencySearchCondition;
import com.devtrace.manager.wbs.dto.WbsTaskRequest;
import com.devtrace.manager.wbs.dto.WbsTaskResponse;
import com.devtrace.manager.wbs.dto.WbsTaskSearchCondition;
import com.devtrace.manager.wbs.dto.WbsTaskStatus;
import com.devtrace.manager.wbs.dto.WbsTaskType;
import com.devtrace.manager.wbs.service.WbsService;
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
@RequestMapping("/wbs")
public class WbsController {

    private final WbsService wbsService;
    private final ProjectService projectService;
    private final IssueService issueService;

    public WbsController(WbsService wbsService, ProjectService projectService, IssueService issueService) {
        this.wbsService = wbsService;
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

    @ModelAttribute("taskTypes")
    public WbsTaskType[] taskTypes() {
        return WbsTaskType.values();
    }

    @ModelAttribute("taskStatuses")
    public WbsTaskStatus[] taskStatuses() {
        return WbsTaskStatus.values();
    }

    @ModelAttribute("dependencyTypes")
    public WbsDependencyType[] dependencyTypes() {
        return WbsDependencyType.values();
    }

    @GetMapping
    public String list(@ModelAttribute WbsTaskSearchCondition condition, Model model) {
        model.addAttribute("tasks", wbsService.selectWbsTaskList(condition));
        model.addAttribute("condition", condition);
        return "wbs/list";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) UUID projectId, Model model) {
        WbsTaskRequest request = createDefaultRequest(projectId);
        model.addAttribute("wbsTask", request);
        model.addAttribute("parentTasks", selectParentCandidates(projectId));
        return "wbs/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("wbsTask") WbsTaskRequest request, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("parentTasks", selectParentCandidates(request.getProjectId()));
            return "wbs/form";
        }
        WbsTaskResponse task = wbsService.insertWbsTask(request);
        return "redirect:/wbs/" + task.getWbsTaskId();
    }

    @GetMapping("/{wbsTaskId}")
    public String detail(@PathVariable UUID wbsTaskId, Model model) {
        WbsTaskResponse task = wbsService.selectWbsTaskDetails(wbsTaskId);
        model.addAttribute("task", task);
        model.addAttribute("projectTasks", selectParentCandidates(task.getProjectId()));
        model.addAttribute("dependencies", selectDependencies(task));
        WbsTaskDependencyRequest dependency = new WbsTaskDependencyRequest();
        dependency.setProjectId(task.getProjectId());
        dependency.setSuccessorTaskId(task.getWbsTaskId());
        dependency.setDependencyType(WbsDependencyType.FINISH_TO_START);
        model.addAttribute("dependency", dependency);
        return "wbs/detail";
    }

    @GetMapping("/{wbsTaskId}/edit")
    public String editForm(@PathVariable UUID wbsTaskId, Model model) {
        WbsTaskResponse task = wbsService.selectWbsTaskDetails(wbsTaskId);
        model.addAttribute("wbsTaskId", wbsTaskId);
        model.addAttribute("wbsTask", task.toRequest());
        model.addAttribute("parentTasks", selectParentCandidates(task.getProjectId()));
        return "wbs/form";
    }

    @PostMapping("/{wbsTaskId}")
    public String update(
            @PathVariable UUID wbsTaskId,
            @Valid @ModelAttribute("wbsTask") WbsTaskRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("wbsTaskId", wbsTaskId);
            model.addAttribute("parentTasks", selectParentCandidates(request.getProjectId()));
            return "wbs/form";
        }
        wbsService.updateWbsTask(wbsTaskId, request);
        return "redirect:/wbs/" + wbsTaskId;
    }

    @PostMapping("/{wbsTaskId}/delete")
    public String delete(@PathVariable UUID wbsTaskId) {
        WbsTaskResponse task = wbsService.selectWbsTaskDetails(wbsTaskId);
        wbsService.deleteWbsTask(wbsTaskId);
        return "redirect:/wbs?projectId=" + task.getProjectId();
    }

    @PostMapping("/dependencies")
    public String createDependency(@Valid @ModelAttribute("dependency") WbsTaskDependencyRequest request, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            wbsService.insertWbsTaskDependency(request);
        }
        return "redirect:/wbs/" + request.getSuccessorTaskId();
    }

    @PostMapping("/dependencies/{dependencyId}/delete")
    public String deleteDependency(@PathVariable UUID dependencyId, @RequestParam UUID redirectTaskId) {
        wbsService.deleteWbsTaskDependency(dependencyId);
        return "redirect:/wbs/" + redirectTaskId;
    }

    @GetMapping("/gantt")
    public String gantt(@RequestParam(required = false) UUID projectId, Model model) {
        UUID selectedProjectId = selectProjectId(projectId);
        WbsGanttResponse gantt = selectedProjectId == null ? createEmptyGantt() : wbsService.selectWbsGanttDetails(selectedProjectId);
        model.addAttribute("selectedProjectId", selectedProjectId);
        model.addAttribute("gantt", gantt);
        return "wbs/gantt";
    }

    private WbsTaskRequest createDefaultRequest(UUID projectId) {
        LocalDate today = LocalDate.now();
        WbsTaskRequest request = new WbsTaskRequest();
        request.setProjectId(projectId);
        request.setPlanStartDate(today);
        request.setPlanEndDate(today.plusDays(6));
        request.setTaskType(WbsTaskType.TASK);
        request.setStatus(WbsTaskStatus.READY);
        return request;
    }

    private List<WbsTaskResponse> selectParentCandidates(UUID projectId) {
        WbsTaskSearchCondition condition = new WbsTaskSearchCondition();
        condition.setProjectId(projectId);
        return wbsService.selectWbsTaskList(condition);
    }

    private List<WbsTaskDependencyResponse> selectDependencies(WbsTaskResponse task) {
        WbsTaskDependencySearchCondition condition = new WbsTaskDependencySearchCondition();
        condition.setProjectId(task.getProjectId());
        condition.setWbsTaskId(task.getWbsTaskId());
        return wbsService.selectWbsTaskDependencyList(condition);
    }

    private UUID selectProjectId(UUID requestedProjectId) {
        if (requestedProjectId != null) {
            return requestedProjectId;
        }
        List<ProjectResponse> projectList = projects();
        return projectList.isEmpty() ? null : projectList.get(0).getProjectId();
    }

    private WbsGanttResponse createEmptyGantt() {
        WbsGanttResponse response = new WbsGanttResponse();
        response.setTimelineStartDate(LocalDate.now());
        response.setTimelineEndDate(LocalDate.now().plusDays(30));
        response.setTasks(List.of());
        return response;
    }
}
