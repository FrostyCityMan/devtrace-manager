package com.devtrace.manager.sprint.controller;

import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.service.ProjectService;
import com.devtrace.manager.sprint.dto.SprintBacklogSearchCondition;
import com.devtrace.manager.sprint.dto.SprintIssueRequest;
import com.devtrace.manager.sprint.dto.SprintIssueResponse;
import com.devtrace.manager.sprint.dto.SprintRequest;
import com.devtrace.manager.sprint.dto.SprintResponse;
import com.devtrace.manager.sprint.dto.SprintReportResponse;
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

/**
 * Thymeleaf 기반 백로그, 스프린트 계획, 스프린트 분석 화면을 제공하는 컨트롤러입니다.
 *
 * <p>스프린트 CRUD, 이슈 배정/제외, 분석 리포트와 Burndown 화면 흐름을 담당합니다.</p>
 */
@Controller
@RequestMapping("/sprints")
public class SprintController {

    private final SprintService sprintService;
    private final ProjectService projectService;

    /**
     * 스프린트 화면 컨트롤러를 생성한다.
     *
     * @param sprintService 스프린트 업무 서비스
     * @param projectService 프로젝트 선택 목록 조회 서비스
     */
    public SprintController(SprintService sprintService, ProjectService projectService) {
        this.sprintService = sprintService;
        this.projectService = projectService;
    }

    /**
     * 화면 공통 스프린트 상태 목록을 제공한다.
     *
     * @return 스프린트 상태 배열
     */
    @ModelAttribute("statuses")
    public SprintStatus[] statuses() {
        return SprintStatus.values();
    }

    /**
     * 화면 공통 프로젝트 선택 목록을 제공한다.
     *
     * @return 프로젝트 목록
     */
    @ModelAttribute("projects")
    public List<ProjectResponse> projects() {
        return projectService.getProjectList(new ProjectSearchCondition());
    }

    /**
     * 백로그·스프린트 관리 화면을 표시한다.
     *
     * @param condition 스프린트 검색 조건
     * @param sprintId 선택할 스프린트 ID
     * @param model Thymeleaf 모델
     * @return 스프린트 목록/편집 화면명
     */
    @GetMapping
    public String list(
            @ModelAttribute SprintSearchCondition condition,
            @RequestParam(required = false) UUID sprintId,
            Model model
    ) {
        populateModel(condition, sprintId, model);
        return "sprint/list";
    }

    /**
     * 스프린트 분석 리포트 화면을 표시한다.
     *
     * @param sprintId 분석 대상 스프린트 ID
     * @param model Thymeleaf 모델
     * @return 스프린트 분석 화면명
     */
    @GetMapping("/{sprintId}/report")
    public String report(@PathVariable UUID sprintId, Model model) {
        SprintReportResponse report = sprintService.selectSprintReportDetails(sprintId);
        model.addAttribute("report", report);
        return "sprint/report";
    }

    /**
     * 스프린트를 생성하고 생성된 스프린트로 이동한다.
     *
     * @param request 신규 스프린트 요청
     * @param bindingResult 검증 결과
     * @param model Thymeleaf 모델
     * @return 리다이렉트 또는 입력 화면명
     */
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

    /**
     * 선택된 스프린트를 수정한다.
     *
     * @param sprintId 수정 대상 스프린트 ID
     * @param request 수정 요청
     * @param bindingResult 검증 결과
     * @param model Thymeleaf 모델
     * @return 리다이렉트 또는 입력 화면명
     */
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

    /**
     * 선택된 스프린트를 시작 처리한다.
     *
     * @param sprintId 시작 대상 스프린트 ID
     * @return 스프린트 화면 리다이렉트
     */
    @PostMapping("/{sprintId}/start")
    public String start(@PathVariable UUID sprintId) {
        SprintResponse sprint = sprintService.updateSprintStart(sprintId);
        return redirectToSprint(sprint);
    }

    /**
     * 선택된 스프린트를 종료 처리한다.
     *
     * @param sprintId 종료 대상 스프린트 ID
     * @return 스프린트 화면 리다이렉트
     */
    @PostMapping("/{sprintId}/close")
    public String close(@PathVariable UUID sprintId) {
        SprintResponse sprint = sprintService.updateSprintClose(sprintId);
        return redirectToSprint(sprint);
    }

    /**
     * 선택된 스프린트를 삭제한다.
     *
     * @param sprintId 삭제 대상 스프린트 ID
     * @return 프로젝트가 유지된 스프린트 화면 리다이렉트
     */
    @PostMapping("/{sprintId}/delete")
    public String delete(@PathVariable UUID sprintId) {
        SprintResponse sprint = sprintService.selectSprintDetails(sprintId);
        sprintService.deleteSprint(sprintId);
        return "redirect:/sprints?projectId=" + sprint.getProjectId();
    }

    /**
     * 백로그 이슈를 선택된 스프린트에 배정한다.
     *
     * @param sprintId 배정 대상 스프린트 ID
     * @param request 이슈 배정 요청
     * @return 스프린트 화면 리다이렉트
     */
    @PostMapping("/{sprintId}/issues")
    public String assignIssue(@PathVariable UUID sprintId, @Valid @ModelAttribute("sprintIssue") SprintIssueRequest request) {
        sprintService.insertSprintIssue(sprintId, request);
        SprintResponse sprint = sprintService.selectSprintDetails(sprintId);
        return redirectToSprint(sprint);
    }

    /**
     * 선택된 스프린트에서 이슈를 제외한다.
     *
     * @param sprintId 스프린트 ID
     * @param issueId 제외 대상 이슈 ID
     * @return 스프린트 화면 리다이렉트
     */
    @PostMapping("/{sprintId}/issues/{issueId}/delete")
    public String deleteIssue(@PathVariable UUID sprintId, @PathVariable UUID issueId) {
        sprintService.deleteSprintIssue(sprintId, issueId);
        SprintResponse sprint = sprintService.selectSprintDetails(sprintId);
        return redirectToSprint(sprint);
    }

    /**
     * 스프린트 관리 화면에 필요한 모든 모델 데이터를 구성한다.
     *
     * <p>프로젝트, 스프린트, 백로그, 선택 스프린트 이슈, 요약 지표를 한 번에 구성하여
     * 화면 템플릿의 조건 분기를 단순하게 유지한다.</p>
     *
     * @param condition 검색 조건
     * @param requestedSprintId 사용자가 선택한 스프린트 ID
     * @param model Thymeleaf 모델
     */
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

    /**
     * 선택 프로젝트 ID를 결정한다.
     *
     * <p>명시된 프로젝트가 없으면 프로젝트 목록의 첫 번째 항목을 기본값으로 사용한다.</p>
     *
     * @param requestedProjectId 요청 프로젝트 ID
     * @return 선택 프로젝트 ID
     */
    private UUID selectProjectId(UUID requestedProjectId) {
        if (requestedProjectId != null) {
            return requestedProjectId;
        }
        List<ProjectResponse> projectList = projects();
        return projectList.isEmpty() ? null : projectList.get(0).getProjectId();
    }

    /**
     * 선택 스프린트를 결정한다.
     *
     * @param requestedSprintId 요청 스프린트 ID
     * @param sprints 조회된 스프린트 목록
     * @return 선택 스프린트 또는 null
     */
    private SprintResponse selectSprint(UUID requestedSprintId, List<SprintResponse> sprints) {
        if (requestedSprintId != null) {
            return sprintService.selectSprintDetails(requestedSprintId);
        }
        return sprints.isEmpty() ? null : sprints.get(0);
    }

    /**
     * 신규 스프린트 입력 폼의 기본 요청 객체를 생성한다.
     *
     * <p>기본 기간은 오늘부터 14일로 설정한다.</p>
     *
     * @param projectId 기본 선택 프로젝트 ID
     * @return 신규 입력 폼 기본 요청 객체
     */
    private SprintRequest createDefaultRequest(UUID projectId) {
        LocalDate today = LocalDate.now();
        SprintRequest request = new SprintRequest();
        request.setProjectId(projectId);
        request.setStatus(SprintStatus.PLANNED);
        request.setStartDate(today);
        request.setEndDate(today.plusDays(13));
        return request;
    }

    /**
     * 프로젝트와 스프린트 선택 상태를 유지하는 리다이렉트 URL을 만든다.
     *
     * @param sprint 리다이렉트 기준 스프린트
     * @return 스프린트 관리 화면 리다이렉트 경로
     */
    private String redirectToSprint(SprintResponse sprint) {
        return "redirect:/sprints?projectId=" + sprint.getProjectId() + "&sprintId=" + sprint.getSprintId();
    }
}
