package com.devtrace.manager.worklog.controller;

import com.devtrace.manager.issue.dto.IssueStatus;
import com.devtrace.manager.issue.service.IssueService;
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

/**
 * Thymeleaf 기반 작업 공수 관리 화면을 제공하는 컨트롤러입니다.
 *
 * <p>이슈별 작업 공수 등록, 수정, 삭제와 목록 검색 화면 흐름을 담당합니다.</p>
 */
@Controller
@RequestMapping("/work-logs")
public class WorkLogController {

    private final WorkLogService workLogService;
    private final IssueService issueService;

    /**
     * 작업 공수 화면 컨트롤러를 생성한다.
     *
     * @param workLogService 작업 공수 업무 서비스
     * @param issueService 이슈 상세 조회 서비스
     */
    public WorkLogController(WorkLogService workLogService, IssueService issueService) {
        this.workLogService = workLogService;
        this.issueService = issueService;
    }

    /**
     * 이슈 상세 화면에서 작업 공수를 등록한다.
     *
     * @param request 작업 공수 등록 요청
     * @param bindingResult 검증 결과
     * @param model Thymeleaf 모델
     * @return 이슈 상세 리다이렉트 또는 검증 오류가 반영된 이슈 상세 화면
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("workLog") WorkLogRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return renderIssueDetail(request.getIssueId(), request, model);
        }
        workLogService.insertWorkLog(request);
        return "redirect:/issues/" + request.getIssueId();
    }

    /**
     * 작업 공수 수정 화면을 표시한다.
     *
     * @param workLogId 수정 대상 작업 공수 ID
     * @param model Thymeleaf 모델
     * @return 작업 공수 수정 화면명
     */
    @GetMapping("/{workLogId}/edit")
    public String editForm(@PathVariable UUID workLogId, Model model) {
        WorkLogRequest request = workLogService.selectWorkLogDetails(workLogId).toRequest();
        model.addAttribute("workLogId", workLogId);
        model.addAttribute("workLog", request);
        model.addAttribute("issue", issueService.selectIssueDetails(request.getIssueId()));
        return "worklog/form";
    }

    /**
     * 작업 공수를 수정한다.
     *
     * @param workLogId 수정 대상 작업 공수 ID
     * @param request 작업 공수 수정 요청
     * @param bindingResult 검증 결과
     * @param model Thymeleaf 모델
     * @return 이슈 상세 리다이렉트 또는 수정 화면명
     */
    @PostMapping("/{workLogId}")
    public String update(
            @PathVariable UUID workLogId,
            @Valid @ModelAttribute("workLog") WorkLogRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("workLogId", workLogId);
            model.addAttribute("issue", issueService.selectIssueDetails(request.getIssueId()));
            return "worklog/form";
        }
        workLogService.updateWorkLog(workLogId, request);
        return "redirect:/issues/" + request.getIssueId();
    }

    /**
     * 작업 공수를 삭제한다.
     *
     * @param workLogId 삭제 대상 작업 공수 ID
     * @return 원 이슈 상세 화면 리다이렉트
     */
    @PostMapping("/{workLogId}/delete")
    public String delete(@PathVariable UUID workLogId) {
        UUID issueId = workLogService.selectWorkLogDetails(workLogId).getIssueId();
        workLogService.deleteWorkLog(workLogId);
        return "redirect:/issues/" + issueId;
    }

    /**
     * 작업 공수 등록 검증 오류 시 이슈 상세 화면 모델을 다시 구성한다.
     *
     * @param issueId 이슈 ID
     * @param request 사용자가 입력한 작업 공수 요청
     * @param model Thymeleaf 모델
     * @return 이슈 상세 화면명 또는 이슈 목록 리다이렉트
     */
    private String renderIssueDetail(UUID issueId, WorkLogRequest request, Model model) {
        if (issueId == null) {
            return "redirect:/issues";
        }

        WorkLogSearchCondition condition = new WorkLogSearchCondition();
        condition.setIssueId(issueId);
        List<WorkLogResponse> workLogs = workLogService.selectWorkLogList(condition);
        model.addAttribute("issue", issueService.selectIssueDetails(issueId));
        model.addAttribute("issueStatuses", IssueStatus.values());
        model.addAttribute("workLog", request);
        model.addAttribute("workLogs", workLogs);
        model.addAttribute("workLogSummary", WorkLogSummary.from(workLogs));
        return "issue/detail";
    }
}
