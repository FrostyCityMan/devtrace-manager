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

@Controller
@RequestMapping("/work-logs")
public class WorkLogController {

    private final WorkLogService workLogService;
    private final IssueService issueService;

    public WorkLogController(WorkLogService workLogService, IssueService issueService) {
        this.workLogService = workLogService;
        this.issueService = issueService;
    }

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

    @GetMapping("/{workLogId}/edit")
    public String editForm(@PathVariable UUID workLogId, Model model) {
        WorkLogRequest request = workLogService.selectWorkLogDetails(workLogId).toRequest();
        model.addAttribute("workLogId", workLogId);
        model.addAttribute("workLog", request);
        model.addAttribute("issue", issueService.selectIssueDetails(request.getIssueId()));
        return "worklog/form";
    }

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

    @PostMapping("/{workLogId}/delete")
    public String delete(@PathVariable UUID workLogId) {
        UUID issueId = workLogService.selectWorkLogDetails(workLogId).getIssueId();
        workLogService.deleteWorkLog(workLogId);
        return "redirect:/issues/" + issueId;
    }

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
