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

/**
 * 백로그와 스프린트 관리 기능을 제공하는 REST 컨트롤러입니다.
 *
 * <p>스프린트 CRUD, 시작/종료, 이슈 배정, 우선순위 정렬, 분석 리포트 API를 제공합니다.</p>
 */
@RestController
@RequestMapping("/api/v1/sprints")
public class SprintApiController {

    private final SprintService sprintService;

    /**
     * 스프린트 REST API 컨트롤러를 생성한다.
     *
     * @param sprintService 스프린트 업무 서비스
     */
    public SprintApiController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    /**
     * 스프린트 목록을 조회한다.
     *
     * @param condition 프로젝트, 상태, 키워드 검색 조건
     * @return 공통 API 응답으로 감싼 스프린트 목록
     */
    @GetMapping
    public ApiResponse<List<SprintResponse>> list(SprintSearchCondition condition) {
        return ApiResponse.success(sprintService.selectSprintList(condition));
    }

    /**
     * 스프린트 상세 정보를 조회한다.
     *
     * @param sprintId 조회 대상 스프린트 ID
     * @return 공통 API 응답으로 감싼 스프린트 상세
     */
    @GetMapping("/{sprintId}")
    public ApiResponse<SprintResponse> details(@PathVariable UUID sprintId) {
        return ApiResponse.success(sprintService.selectSprintDetails(sprintId));
    }

    /**
     * 스프린트를 등록한다.
     *
     * @param request 등록 요청
     * @return 등록된 스프린트 응답
     */
    @PostMapping
    public ApiResponse<SprintResponse> create(@Valid @RequestBody SprintRequest request) {
        return ApiResponse.success("스프린트가 등록되었습니다.", sprintService.insertSprint(request));
    }

    /**
     * 스프린트를 수정한다.
     *
     * @param sprintId 수정 대상 스프린트 ID
     * @param request 수정 요청
     * @return 수정된 스프린트 응답
     */
    @PutMapping("/{sprintId}")
    public ApiResponse<SprintResponse> update(@PathVariable UUID sprintId, @Valid @RequestBody SprintRequest request) {
        return ApiResponse.success("스프린트가 수정되었습니다.", sprintService.updateSprint(sprintId, request));
    }

    /**
     * 스프린트를 삭제한다.
     *
     * @param sprintId 삭제 대상 스프린트 ID
     * @return 처리 결과 응답
     */
    @DeleteMapping("/{sprintId}")
    public ApiResponse<Void> delete(@PathVariable UUID sprintId) {
        sprintService.deleteSprint(sprintId);
        return ApiResponse.success("스프린트가 삭제되었습니다.", null);
    }

    /**
     * 스프린트를 시작 상태로 전환한다.
     *
     * @param sprintId 시작 대상 스프린트 ID
     * @return 시작 처리된 스프린트 응답
     */
    @PostMapping("/{sprintId}/start")
    public ApiResponse<SprintResponse> start(@PathVariable UUID sprintId) {
        return ApiResponse.success("스프린트가 시작되었습니다.", sprintService.updateSprintStart(sprintId));
    }

    /**
     * 스프린트를 종료 상태로 전환한다.
     *
     * @param sprintId 종료 대상 스프린트 ID
     * @return 종료 처리된 스프린트 응답
     */
    @PostMapping("/{sprintId}/close")
    public ApiResponse<SprintResponse> close(@PathVariable UUID sprintId) {
        return ApiResponse.success("스프린트가 종료되었습니다.", sprintService.updateSprintClose(sprintId));
    }

    /**
     * 스프린트에 배정 가능한 백로그 이슈 목록을 조회한다.
     *
     * @param condition 프로젝트와 키워드 검색 조건
     * @return 백로그 이슈 목록 응답
     */
    @GetMapping("/backlog")
    public ApiResponse<List<SprintIssueResponse>> backlog(SprintBacklogSearchCondition condition) {
        return ApiResponse.success(sprintService.selectBacklogIssueList(condition));
    }

    /**
     * 스프린트에 배정된 이슈 목록을 조회한다.
     *
     * @param sprintId 스프린트 ID
     * @return 스프린트 이슈 목록 응답
     */
    @GetMapping("/{sprintId}/issues")
    public ApiResponse<List<SprintIssueResponse>> issueList(@PathVariable UUID sprintId) {
        return ApiResponse.success(sprintService.selectSprintIssueList(sprintId));
    }

    /**
     * 스프린트 요약 지표를 조회한다.
     *
     * @param sprintId 스프린트 ID
     * @return 스프린트 요약 응답
     */
    @GetMapping("/{sprintId}/summary")
    public ApiResponse<SprintSummaryResponse> summary(@PathVariable UUID sprintId) {
        return ApiResponse.success(sprintService.selectSprintSummaryDetails(sprintId));
    }

    /**
     * 스프린트 분석 리포트를 조회한다.
     *
     * @param sprintId 분석 대상 스프린트 ID
     * @return 스프린트 분석 리포트 응답
     */
    @GetMapping("/{sprintId}/report")
    public ApiResponse<SprintReportResponse> report(@PathVariable UUID sprintId) {
        return ApiResponse.success(sprintService.selectSprintReportDetails(sprintId));
    }

    /**
     * Burndown Chart 포인트 목록을 조회한다.
     *
     * @param sprintId 분석 대상 스프린트 ID
     * @return Burndown 포인트 목록 응답
     */
    @GetMapping("/{sprintId}/burndown")
    public ApiResponse<List<SprintBurndownPointResponse>> burndown(@PathVariable UUID sprintId) {
        return ApiResponse.success(sprintService.selectSprintBurndownList(sprintId));
    }

    /**
     * 백로그 이슈를 스프린트에 배정한다.
     *
     * @param sprintId 배정 대상 스프린트 ID
     * @param request 배정 요청
     * @return 배정된 스프린트 이슈 응답
     */
    @PostMapping("/{sprintId}/issues")
    public ApiResponse<SprintIssueResponse> addIssue(@PathVariable UUID sprintId, @Valid @RequestBody SprintIssueRequest request) {
        return ApiResponse.success("스프린트에 이슈가 배정되었습니다.", sprintService.insertSprintIssue(sprintId, request));
    }

    /**
     * 스프린트 이슈 표시 순서를 변경한다.
     *
     * @param sprintId 스프린트 ID
     * @param issueId 이슈 ID
     * @param request 표시 순서 변경 요청
     * @return 처리 결과 응답
     */
    @PutMapping("/{sprintId}/issues/{issueId}/order")
    public ApiResponse<Void> updateIssueOrder(
            @PathVariable UUID sprintId,
            @PathVariable UUID issueId,
            @Valid @RequestBody SprintIssueOrderRequest request
    ) {
        sprintService.updateSprintIssueDisplayOrder(sprintId, issueId, request);
        return ApiResponse.success("스프린트 이슈 순서가 변경되었습니다.", null);
    }

    /**
     * 스프린트에서 이슈를 제외한다.
     *
     * @param sprintId 스프린트 ID
     * @param issueId 제외 대상 이슈 ID
     * @return 처리 결과 응답
     */
    @DeleteMapping("/{sprintId}/issues/{issueId}")
    public ApiResponse<Void> deleteIssue(@PathVariable UUID sprintId, @PathVariable UUID issueId) {
        sprintService.deleteSprintIssue(sprintId, issueId);
        return ApiResponse.success("스프린트 이슈가 제외되었습니다.", null);
    }
}
