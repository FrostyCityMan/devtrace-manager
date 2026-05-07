package com.devtrace.manager.worklog.controller;

import com.devtrace.manager.common.response.ApiResponse;
import com.devtrace.manager.worklog.dto.WorkLogRequest;
import com.devtrace.manager.worklog.dto.WorkLogResponse;
import com.devtrace.manager.worklog.dto.WorkLogSearchCondition;
import com.devtrace.manager.worklog.service.WorkLogService;
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
 * 작업 공수 관리 기능을 제공하는 REST 컨트롤러입니다.
 *
 * <p>작업 공수 CRUD와 목록 조회 API를 공통 응답 형식으로 제공합니다.</p>
 */
@RestController
@RequestMapping("/api/v1/work-logs")
public class WorkLogApiController {

    private final WorkLogService workLogService;

    /**
     * 작업 공수 REST API 컨트롤러를 생성한다.
     *
     * @param workLogService 작업 공수 업무 서비스
     */
    public WorkLogApiController(WorkLogService workLogService) {
        this.workLogService = workLogService;
    }

    /**
     * 작업 공수 목록을 조회한다.
     *
     * @param condition 이슈/사용자 검색 조건
     * @return 작업 공수 목록 API 응답
     */
    @GetMapping
    public ApiResponse<List<WorkLogResponse>> list(WorkLogSearchCondition condition) {
        return ApiResponse.success(workLogService.selectWorkLogList(condition));
    }

    /**
     * 작업 공수 상세를 조회한다.
     *
     * @param workLogId 조회 대상 작업 공수 ID
     * @return 작업 공수 상세 API 응답
     */
    @GetMapping("/{workLogId}")
    public ApiResponse<WorkLogResponse> detail(@PathVariable UUID workLogId) {
        return ApiResponse.success(workLogService.selectWorkLogDetails(workLogId));
    }

    /**
     * 작업 공수를 등록한다.
     *
     * @param request 작업 공수 등록 요청
     * @return 등록된 작업 공수 API 응답
     */
    @PostMapping
    public ApiResponse<WorkLogResponse> create(@Valid @RequestBody WorkLogRequest request) {
        return ApiResponse.success("작업 공수가 등록되었습니다.", workLogService.insertWorkLog(request));
    }

    /**
     * 작업 공수를 수정한다.
     *
     * @param workLogId 수정 대상 작업 공수 ID
     * @param request 작업 공수 수정 요청
     * @return 수정된 작업 공수 API 응답
     */
    @PutMapping("/{workLogId}")
    public ApiResponse<WorkLogResponse> update(
            @PathVariable UUID workLogId,
            @Valid @RequestBody WorkLogRequest request
    ) {
        return ApiResponse.success("작업 공수가 수정되었습니다.", workLogService.updateWorkLog(workLogId, request));
    }

    /**
     * 작업 공수를 삭제한다.
     *
     * @param workLogId 삭제 대상 작업 공수 ID
     * @return 처리 결과 API 응답
     */
    @DeleteMapping("/{workLogId}")
    public ApiResponse<Void> delete(@PathVariable UUID workLogId) {
        workLogService.deleteWorkLog(workLogId);
        return ApiResponse.success("작업 공수가 삭제되었습니다.", null);
    }
}
