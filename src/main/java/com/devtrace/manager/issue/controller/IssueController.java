package com.devtrace.manager.issue.controller;

import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.issue.dto.IssueListSummary;
import com.devtrace.manager.issue.dto.IssueRequest;
import com.devtrace.manager.issue.dto.IssueResponse;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import com.devtrace.manager.issue.dto.IssueStatus;
import com.devtrace.manager.issue.dto.IssueType;
import com.devtrace.manager.issue.service.IssueService;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.service.ProjectService;
import com.devtrace.manager.worklog.dto.WorkLogRequest;
import com.devtrace.manager.worklog.dto.WorkLogResponse;
import com.devtrace.manager.worklog.dto.WorkLogSearchCondition;
import com.devtrace.manager.worklog.dto.WorkLogSummary;
import com.devtrace.manager.worklog.service.WorkLogService;
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
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Thymeleaf 기반 이슈 관리 화면을 제공하는 컨트롤러입니다.
 *
 * <p>프로젝트별 이슈 목록, 등록, 상세, 수정, 삭제와 이슈 상세의 공수 영역을 담당합니다.</p>
 */
@Controller
@RequestMapping("/issues")
public class IssueController {

    private final IssueService issueService;
    private final ProjectService projectService;
    private final WorkLogService workLogService;

    /**
     * 이슈 화면 컨트롤러를 생성한다.
     *
     * @param issueService 이슈 업무 서비스
     * @param projectService 프로젝트 선택 목록 서비스
     * @param workLogService 이슈 상세의 작업 공수 서비스
     */
    public IssueController(IssueService issueService, ProjectService projectService, WorkLogService workLogService) {
        this.issueService = issueService;
        this.projectService = projectService;
        this.workLogService = workLogService;
    }

    /**
     * 이슈 상태 선택 목록을 화면 공통 모델로 제공한다.
     *
     * @return 이슈 상태 배열
     */
    @ModelAttribute("issueStatuses")
    public IssueStatus[] issueStatuses() {
        return IssueStatus.values();
    }

    /**
     * 이슈 유형 선택 목록을 화면 공통 모델로 제공한다.
     *
     * @return 이슈 유형 배열
     */
    @ModelAttribute("issueTypes")
    public IssueType[] issueTypes() {
        return IssueType.values();
    }

    /**
     * 이슈 우선순위 선택 목록을 화면 공통 모델로 제공한다.
     *
     * @return 이슈 우선순위 배열
     */
    @ModelAttribute("priorities")
    public IssuePriority[] priorities() {
        return IssuePriority.values();
    }

    /**
     * 이슈 화면의 프로젝트 선택 목록을 제공한다.
     *
     * @return 프로젝트 목록
     */
    @ModelAttribute("projects")
    public List<ProjectResponse> projects() {
        return projectService.getProjectList(new ProjectSearchCondition());
    }

    /**
     * 이슈 목록 화면을 표시한다.
     *
     * @param condition 프로젝트, 상태, 우선순위, 담당자, 기간, 키워드 검색 조건
     * @param model Thymeleaf 모델
     * @return 이슈 목록 화면명
     */
    @GetMapping
    public String list(@ModelAttribute IssueSearchCondition condition, Model model) {
        List<IssueResponse> issues = issueService.selectIssueList(condition);
        model.addAttribute("issues", issues);
        model.addAttribute("summary", IssueListSummary.from(issues));
        model.addAttribute("condition", condition);
        return "issue/list";
    }

    /**
     * 이슈 등록 화면을 표시한다.
     *
     * @param model Thymeleaf 모델
     * @return 이슈 입력 화면명
     */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("issue", new IssueRequest());
        return "issue/form";
    }

    /**
     * 이슈를 등록한다.
     *
     * @param request 이슈 등록 요청
     * @param bindingResult 검증 결과
     * @return 이슈 목록 리다이렉트 또는 입력 화면명
     */
    @PostMapping
    public String create(@Valid @ModelAttribute("issue") IssueRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "issue/form";
        }
        issueService.insertIssue(request);
        return "redirect:/issues";
    }

    /**
     * 이슈 상세 화면을 표시한다.
     *
     * <p>이슈 기본 정보와 이슈별 작업 공수 목록 및 합계를 함께 구성한다.</p>
     *
     * @param issueId 조회 대상 이슈 ID
     * @param model Thymeleaf 모델
     * @return 이슈 상세 화면명
     */
    @GetMapping("/{issueId}")
    public String detail(@PathVariable UUID issueId, Model model) {
        model.addAttribute("issue", issueService.selectIssueDetails(issueId));
        WorkLogRequest workLog = new WorkLogRequest();
        workLog.setIssueId(issueId);
        model.addAttribute("workLog", workLog);
        WorkLogSearchCondition condition = new WorkLogSearchCondition();
        condition.setIssueId(issueId);
        List<WorkLogResponse> workLogs = workLogService.selectWorkLogList(condition);
        model.addAttribute("workLogs", workLogs);
        model.addAttribute("workLogSummary", WorkLogSummary.from(workLogs));
        return "issue/detail";
    }

    /**
     * 이슈 수정 화면을 표시한다.
     *
     * @param issueId 수정 대상 이슈 ID
     * @param model Thymeleaf 모델
     * @return 이슈 입력 화면명
     */
    @GetMapping("/{issueId}/edit")
    public String editForm(@PathVariable UUID issueId, Model model) {
        model.addAttribute("issueId", issueId);
        model.addAttribute("issue", issueService.selectIssueDetails(issueId).toRequest());
        return "issue/form";
    }

    /**
     * 이슈를 수정한다.
     *
     * @param issueId 수정 대상 이슈 ID
     * @param request 이슈 수정 요청
     * @param bindingResult 검증 결과
     * @param model Thymeleaf 모델
     * @return 이슈 상세 리다이렉트 또는 입력 화면명
     */
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

    /**
     * 이슈 상태를 변경한다.
     *
     * @param issueId 상태 변경 대상 이슈 ID
     * @param status 변경할 이슈 상태
     * @return 이슈 상세 화면 리다이렉트
     */
    @PostMapping("/{issueId}/status")
    public String updateStatus(@PathVariable UUID issueId, @RequestParam IssueStatus status) {
        issueService.updateIssueStatus(issueId, status);
        return "redirect:/issues/" + issueId;
    }

    /**
     * 이슈를 삭제한다.
     *
     * @param issueId 삭제 대상 이슈 ID
     * @return 이슈 목록 화면 리다이렉트
     */
    @PostMapping("/{issueId}/delete")
    public String delete(@PathVariable UUID issueId) {
        issueService.deleteIssue(issueId);
        return "redirect:/issues";
    }
}
