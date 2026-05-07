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

/**
 * Thymeleaf 기반 WBS 작업 관리와 Gantt 화면을 제공하는 컨트롤러입니다.
 *
 * <p>프로젝트별 WBS 계층 작업, 선후행 의존성, Gantt 조회 화면을 연결합니다.
 * 화면 전용 기본값과 선택 목록 구성만 담당하고, 업무 규칙은 서비스 계층에 위임합니다.</p>
 */
@Controller
@RequestMapping("/wbs")
public class WbsController {

    private final WbsService wbsService;
    private final ProjectService projectService;
    private final IssueService issueService;

    /**
     * WBS 화면 컨트롤러를 생성합니다.
     *
     * @param wbsService WBS 서비스
     * @param projectService 프로젝트 선택 목록 조회 서비스
     * @param issueService 이슈 선택 목록 조회 서비스
     */
    public WbsController(WbsService wbsService, ProjectService projectService, IssueService issueService) {
        this.wbsService = wbsService;
        this.projectService = projectService;
        this.issueService = issueService;
    }

    /**
     * WBS 화면의 프로젝트 선택 목록을 제공합니다.
     *
     * @return 전체 프로젝트 목록
     */
    @ModelAttribute("projects")
    public List<ProjectResponse> projects() {
        return projectService.getProjectList(new ProjectSearchCondition());
    }

    /**
     * WBS 작업 연결용 이슈 선택 목록을 제공합니다.
     *
     * @return 전체 이슈 목록
     */
    @ModelAttribute("issues")
    public List<IssueResponse> issues() {
        return issueService.selectIssueList(new IssueSearchCondition());
    }

    /**
     * WBS 작업 유형 선택값을 제공합니다.
     *
     * @return 작업 유형 배열
     */
    @ModelAttribute("taskTypes")
    public WbsTaskType[] taskTypes() {
        return WbsTaskType.values();
    }

    /**
     * WBS 작업 상태 선택값을 제공합니다.
     *
     * @return 작업 상태 배열
     */
    @ModelAttribute("taskStatuses")
    public WbsTaskStatus[] taskStatuses() {
        return WbsTaskStatus.values();
    }

    /**
     * WBS 의존성 유형 선택값을 제공합니다.
     *
     * @return 의존성 유형 배열
     */
    @ModelAttribute("dependencyTypes")
    public WbsDependencyType[] dependencyTypes() {
        return WbsDependencyType.values();
    }

    /**
     * 프로젝트별 WBS 작업 목록을 표시합니다.
     *
     * @param condition 검색 조건
     * @param model 화면 모델
     * @return WBS 목록 템플릿
     */
    @GetMapping
    public String list(@ModelAttribute WbsTaskSearchCondition condition, Model model) {
        model.addAttribute("tasks", wbsService.selectWbsTaskList(condition));
        model.addAttribute("condition", condition);
        return "wbs/list";
    }

    /**
     * WBS 작업 등록 화면을 표시합니다.
     *
     * @param projectId 선택 프로젝트 ID
     * @param model 화면 모델
     * @return WBS 입력 템플릿
     */
    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) UUID projectId, Model model) {
        WbsTaskRequest request = createDefaultRequest(projectId);
        model.addAttribute("wbsTask", request);
        model.addAttribute("parentTasks", selectParentCandidates(projectId));
        return "wbs/form";
    }

    /**
     * WBS 작업을 등록합니다.
     *
     * @param request 등록 요청
     * @param bindingResult 입력 검증 결과
     * @param model 화면 모델
     * @return 성공 시 상세 화면으로 이동, 실패 시 입력 화면
     */
    @PostMapping
    public String create(@Valid @ModelAttribute("wbsTask") WbsTaskRequest request, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("parentTasks", selectParentCandidates(request.getProjectId()));
            return "wbs/form";
        }
        WbsTaskResponse task = wbsService.insertWbsTask(request);
        return "redirect:/wbs/" + task.getWbsTaskId();
    }

    /**
     * WBS 작업 상세와 선후행 작업 입력 영역을 표시합니다.
     *
     * @param wbsTaskId WBS 작업 ID
     * @param model 화면 모델
     * @return WBS 상세 템플릿
     */
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

    /**
     * WBS 작업 수정 화면을 표시합니다.
     *
     * @param wbsTaskId WBS 작업 ID
     * @param model 화면 모델
     * @return WBS 입력 템플릿
     */
    @GetMapping("/{wbsTaskId}/edit")
    public String editForm(@PathVariable UUID wbsTaskId, Model model) {
        WbsTaskResponse task = wbsService.selectWbsTaskDetails(wbsTaskId);
        model.addAttribute("wbsTaskId", wbsTaskId);
        model.addAttribute("wbsTask", task.toRequest());
        model.addAttribute("parentTasks", selectParentCandidates(task.getProjectId()));
        return "wbs/form";
    }

    /**
     * WBS 작업을 수정합니다.
     *
     * @param wbsTaskId WBS 작업 ID
     * @param request 수정 요청
     * @param bindingResult 입력 검증 결과
     * @param model 화면 모델
     * @return 성공 시 상세 화면으로 이동, 실패 시 입력 화면
     */
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

    /**
     * WBS 작업을 삭제합니다.
     *
     * @param wbsTaskId WBS 작업 ID
     * @return 프로젝트 WBS 목록 이동 경로
     */
    @PostMapping("/{wbsTaskId}/delete")
    public String delete(@PathVariable UUID wbsTaskId) {
        WbsTaskResponse task = wbsService.selectWbsTaskDetails(wbsTaskId);
        wbsService.deleteWbsTask(wbsTaskId);
        return "redirect:/wbs?projectId=" + task.getProjectId();
    }

    /**
     * WBS 선후행 의존성을 등록합니다.
     *
     * @param request 의존성 등록 요청
     * @param bindingResult 입력 검증 결과
     * @return 후행 작업 상세 화면 이동 경로
     */
    @PostMapping("/dependencies")
    public String createDependency(@Valid @ModelAttribute("dependency") WbsTaskDependencyRequest request, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            wbsService.insertWbsTaskDependency(request);
        }
        return "redirect:/wbs/" + request.getSuccessorTaskId();
    }

    /**
     * WBS 선후행 의존성을 삭제합니다.
     *
     * @param dependencyId 의존성 ID
     * @param redirectTaskId 삭제 후 돌아갈 WBS 작업 ID
     * @return WBS 작업 상세 화면 이동 경로
     */
    @PostMapping("/dependencies/{dependencyId}/delete")
    public String deleteDependency(@PathVariable UUID dependencyId, @RequestParam UUID redirectTaskId) {
        wbsService.deleteWbsTaskDependency(dependencyId);
        return "redirect:/wbs/" + redirectTaskId;
    }

    /**
     * 프로젝트별 WBS Gantt 화면을 표시합니다.
     *
     * @param projectId 선택 프로젝트 ID
     * @param model 화면 모델
     * @return WBS Gantt 템플릿
     */
    @GetMapping("/gantt")
    public String gantt(@RequestParam(required = false) UUID projectId, Model model) {
        UUID selectedProjectId = selectProjectId(projectId);
        WbsGanttResponse gantt = selectedProjectId == null ? createEmptyGantt() : wbsService.selectWbsGanttDetails(selectedProjectId);
        model.addAttribute("selectedProjectId", selectedProjectId);
        model.addAttribute("gantt", gantt);
        return "wbs/gantt";
    }

    /**
     * WBS 작업 등록 화면의 기본 입력값을 생성합니다.
     *
     * @param projectId 선택 프로젝트 ID
     * @return 기본 WBS 작업 요청
     */
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

    /**
     * 상위 작업 후보 목록을 조회합니다.
     *
     * @param projectId 프로젝트 ID
     * @return 선택 가능한 WBS 작업 목록
     */
    private List<WbsTaskResponse> selectParentCandidates(UUID projectId) {
        WbsTaskSearchCondition condition = new WbsTaskSearchCondition();
        condition.setProjectId(projectId);
        return wbsService.selectWbsTaskList(condition);
    }

    /**
     * 작업 상세 화면에 표시할 선후행 의존성을 조회합니다.
     *
     * @param task 기준 WBS 작업
     * @return 의존성 목록
     */
    private List<WbsTaskDependencyResponse> selectDependencies(WbsTaskResponse task) {
        WbsTaskDependencySearchCondition condition = new WbsTaskDependencySearchCondition();
        condition.setProjectId(task.getProjectId());
        condition.setWbsTaskId(task.getWbsTaskId());
        return wbsService.selectWbsTaskDependencyList(condition);
    }

    /**
     * Gantt 화면에서 사용할 프로젝트 ID를 선택합니다.
     *
     * @param requestedProjectId 사용자가 요청한 프로젝트 ID
     * @return 요청 프로젝트 ID 또는 첫 번째 프로젝트 ID
     */
    private UUID selectProjectId(UUID requestedProjectId) {
        if (requestedProjectId != null) {
            return requestedProjectId;
        }
        List<ProjectResponse> projectList = projects();
        return projectList.isEmpty() ? null : projectList.get(0).getProjectId();
    }

    /**
     * 프로젝트가 없는 경우 화면을 비우지 않기 위한 기본 Gantt 응답을 생성합니다.
     *
     * @return 빈 Gantt 응답
     */
    private WbsGanttResponse createEmptyGantt() {
        WbsGanttResponse response = new WbsGanttResponse();
        response.setTimelineStartDate(LocalDate.now());
        response.setTimelineEndDate(LocalDate.now().plusDays(30));
        response.setTasks(List.of());
        return response;
    }
}
