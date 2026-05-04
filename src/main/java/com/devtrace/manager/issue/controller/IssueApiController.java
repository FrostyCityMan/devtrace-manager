package com.devtrace.manager.issue.controller;

import com.devtrace.manager.common.response.ApiResponse;
import com.devtrace.manager.issue.dto.IssueRequest;
import com.devtrace.manager.issue.dto.IssueResponse;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import com.devtrace.manager.issue.dto.IssueStatusUpdateRequest;
import com.devtrace.manager.issue.service.IssueService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/issues")
public class IssueApiController {

    private final IssueService issueService;

    public IssueApiController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping
    public ApiResponse<List<IssueResponse>> list(IssueSearchCondition condition) {
        return ApiResponse.success(issueService.selectIssueList(condition));
    }

    @GetMapping("/{issueId}")
    public ApiResponse<IssueResponse> detail(@PathVariable UUID issueId) {
        return ApiResponse.success(issueService.selectIssueDetails(issueId));
    }

    @PostMapping
    public ApiResponse<IssueResponse> create(@Valid @RequestBody IssueRequest request) {
        return ApiResponse.success("이슈가 등록되었습니다.", issueService.insertIssue(request));
    }

    @PutMapping("/{issueId}")
    public ApiResponse<IssueResponse> update(
            @PathVariable UUID issueId,
            @Valid @RequestBody IssueRequest request
    ) {
        return ApiResponse.success("이슈가 수정되었습니다.", issueService.updateIssue(issueId, request));
    }

    @PatchMapping("/{issueId}/status")
    public ApiResponse<IssueResponse> updateStatus(
            @PathVariable UUID issueId,
            @Valid @RequestBody IssueStatusUpdateRequest request
    ) {
        return ApiResponse.success("이슈 상태가 변경되었습니다.", issueService.updateIssueStatus(issueId, request.getStatus()));
    }

    @DeleteMapping("/{issueId}")
    public ApiResponse<Void> delete(@PathVariable UUID issueId) {
        issueService.deleteIssue(issueId);
        return ApiResponse.success("이슈가 삭제되었습니다.", null);
    }
}
