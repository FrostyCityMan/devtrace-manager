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

/**
 * 이슈 관리 기능을 제공하는 REST 컨트롤러입니다.
 *
 * <p>이슈 CRUD와 상태 변경 API를 공통 응답 형식으로 제공합니다.</p>
 */
@RestController
@RequestMapping("/api/v1/issues")
public class IssueApiController {

    private final IssueService issueService;

    /**
     * 이슈 REST API 컨트롤러를 생성한다.
     *
     * @param issueService 이슈 업무 서비스
     */
    public IssueApiController(IssueService issueService) {
        this.issueService = issueService;
    }

    /**
     * 이슈 목록을 조회한다.
     *
     * @param condition 이슈 검색 조건
     * @return 이슈 목록 API 응답
     */
    @GetMapping
    public ApiResponse<List<IssueResponse>> list(IssueSearchCondition condition) {
        return ApiResponse.success(issueService.selectIssueList(condition));
    }

    /**
     * 이슈 상세 정보를 조회한다.
     *
     * @param issueId 조회 대상 이슈 ID
     * @return 이슈 상세 API 응답
     */
    @GetMapping("/{issueId}")
    public ApiResponse<IssueResponse> detail(@PathVariable UUID issueId) {
        return ApiResponse.success(issueService.selectIssueDetails(issueId));
    }

    /**
     * 이슈를 등록한다.
     *
     * @param request 이슈 등록 요청
     * @return 등록된 이슈 API 응답
     */
    @PostMapping
    public ApiResponse<IssueResponse> create(@Valid @RequestBody IssueRequest request) {
        return ApiResponse.success("이슈가 등록되었습니다.", issueService.insertIssue(request));
    }

    /**
     * 이슈를 수정한다.
     *
     * @param issueId 수정 대상 이슈 ID
     * @param request 이슈 수정 요청
     * @return 수정된 이슈 API 응답
     */
    @PutMapping("/{issueId}")
    public ApiResponse<IssueResponse> update(
            @PathVariable UUID issueId,
            @Valid @RequestBody IssueRequest request
    ) {
        return ApiResponse.success("이슈가 수정되었습니다.", issueService.updateIssue(issueId, request));
    }

    /**
     * 이슈 상태를 변경한다.
     *
     * @param issueId 상태 변경 대상 이슈 ID
     * @param request 상태 변경 요청
     * @return 상태 변경 후 이슈 API 응답
     */
    @PatchMapping("/{issueId}/status")
    public ApiResponse<IssueResponse> updateStatus(
            @PathVariable UUID issueId,
            @Valid @RequestBody IssueStatusUpdateRequest request
    ) {
        return ApiResponse.success("이슈 상태가 변경되었습니다.", issueService.updateIssueStatus(issueId, request.getStatus()));
    }

    /**
     * 이슈를 삭제한다.
     *
     * @param issueId 삭제 대상 이슈 ID
     * @return 처리 결과 API 응답
     */
    @DeleteMapping("/{issueId}")
    public ApiResponse<Void> delete(@PathVariable UUID issueId) {
        issueService.deleteIssue(issueId);
        return ApiResponse.success("이슈가 삭제되었습니다.", null);
    }
}
