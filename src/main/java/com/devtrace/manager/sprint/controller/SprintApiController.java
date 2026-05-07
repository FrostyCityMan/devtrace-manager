package com.devtrace.manager.sprint.controller;

import com.devtrace.manager.common.response.ApiResponse;
import com.devtrace.manager.sprint.dto.SprintBacklogSearchCondition;
import com.devtrace.manager.sprint.dto.SprintBurndownPointResponse;
import com.devtrace.manager.sprint.dto.SprintIssueOrderRequest;
import com.devtrace.manager.sprint.dto.SprintIssueRequest;
import com.devtrace.manager.sprint.dto.SprintIssueResponse;
import com.devtrace.manager.sprint.dto.SprintRequest;
import com.devtrace.manager.sprint.dto.SprintResponse;
import com.devtrace.manager.sprint.dto.SprintReportResponse;
import com.devtrace.manager.sprint.dto.SprintSearchCondition;
import com.devtrace.manager.sprint.dto.SprintSummaryResponse;
import com.devtrace.manager.sprint.service.SprintService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sprints")
public class SprintApiController {

    private final SprintService sprintService;

    public SprintApiController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @GetMapping
    public ApiResponse<List<SprintResponse>> list(SprintSearchCondition condition) {
        return ApiResponse.success(sprintService.selectSprintList(condition));
    }

    @GetMapping("/{sprintId}")
    public ApiResponse<SprintResponse> details(@PathVariable UUID sprintId) {
        return ApiResponse.success(sprintService.selectSprintDetails(sprintId));
    }

    @PostMapping
    public ApiResponse<SprintResponse> create(@Valid @RequestBody SprintRequest request) {
        return ApiResponse.success("스프린트가 등록되었습니다.", sprintService.insertSprint(request));
    }

    @PutMapping("/{sprintId}")
    public ApiResponse<SprintResponse> update(@PathVariable UUID sprintId, @Valid @RequestBody SprintRequest request) {
        return ApiResponse.success("스프린트가 수정되었습니다.", sprintService.updateSprint(sprintId, request));
    }

    @DeleteMapping("/{sprintId}")
    public ApiResponse<Void> delete(@PathVariable UUID sprintId) {
        sprintService.deleteSprint(sprintId);
        return ApiResponse.success("스프린트가 삭제되었습니다.", null);
    }

    @PostMapping("/{sprintId}/start")
    public ApiResponse<SprintResponse> start(@PathVariable UUID sprintId) {
        return ApiResponse.success("스프린트가 시작되었습니다.", sprintService.updateSprintStart(sprintId));
    }

    @PostMapping("/{sprintId}/close")
    public ApiResponse<SprintResponse> close(@PathVariable UUID sprintId) {
        return ApiResponse.success("스프린트가 종료되었습니다.", sprintService.updateSprintClose(sprintId));
    }

    @GetMapping("/backlog")
    public ApiResponse<List<SprintIssueResponse>> backlog(SprintBacklogSearchCondition condition) {
        return ApiResponse.success(sprintService.selectBacklogIssueList(condition));
    }

    @GetMapping("/{sprintId}/issues")
    public ApiResponse<List<SprintIssueResponse>> issueList(@PathVariable UUID sprintId) {
        return ApiResponse.success(sprintService.selectSprintIssueList(sprintId));
    }

    @GetMapping("/{sprintId}/summary")
    public ApiResponse<SprintSummaryResponse> summary(@PathVariable UUID sprintId) {
        return ApiResponse.success(sprintService.selectSprintSummaryDetails(sprintId));
    }

    @GetMapping("/{sprintId}/report")
    public ApiResponse<SprintReportResponse> report(@PathVariable UUID sprintId) {
        return ApiResponse.success(sprintService.selectSprintReportDetails(sprintId));
    }

    @GetMapping("/{sprintId}/burndown")
    public ApiResponse<List<SprintBurndownPointResponse>> burndown(@PathVariable UUID sprintId) {
        return ApiResponse.success(sprintService.selectSprintBurndownList(sprintId));
    }

    @PostMapping("/{sprintId}/issues")
    public ApiResponse<SprintIssueResponse> addIssue(@PathVariable UUID sprintId, @Valid @RequestBody SprintIssueRequest request) {
        return ApiResponse.success("스프린트에 이슈가 배정되었습니다.", sprintService.insertSprintIssue(sprintId, request));
    }

    @PutMapping("/{sprintId}/issues/{issueId}/order")
    public ApiResponse<Void> updateIssueOrder(
            @PathVariable UUID sprintId,
            @PathVariable UUID issueId,
            @Valid @RequestBody SprintIssueOrderRequest request
    ) {
        sprintService.updateSprintIssueDisplayOrder(sprintId, issueId, request);
        return ApiResponse.success("스프린트 이슈 순서가 변경되었습니다.", null);
    }

    @DeleteMapping("/{sprintId}/issues/{issueId}")
    public ApiResponse<Void> deleteIssue(@PathVariable UUID sprintId, @PathVariable UUID issueId) {
        sprintService.deleteSprintIssue(sprintId, issueId);
        return ApiResponse.success("스프린트 이슈가 제외되었습니다.", null);
    }
}
