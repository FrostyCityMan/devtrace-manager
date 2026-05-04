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

@RestController
@RequestMapping("/api/v1/work-logs")
public class WorkLogApiController {

    private final WorkLogService workLogService;

    public WorkLogApiController(WorkLogService workLogService) {
        this.workLogService = workLogService;
    }

    @GetMapping
    public ApiResponse<List<WorkLogResponse>> list(WorkLogSearchCondition condition) {
        return ApiResponse.success(workLogService.selectWorkLogList(condition));
    }

    @GetMapping("/{workLogId}")
    public ApiResponse<WorkLogResponse> detail(@PathVariable UUID workLogId) {
        return ApiResponse.success(workLogService.selectWorkLogDetails(workLogId));
    }

    @PostMapping
    public ApiResponse<WorkLogResponse> create(@Valid @RequestBody WorkLogRequest request) {
        return ApiResponse.success("작업 공수가 등록되었습니다.", workLogService.insertWorkLog(request));
    }

    @PutMapping("/{workLogId}")
    public ApiResponse<WorkLogResponse> update(
            @PathVariable UUID workLogId,
            @Valid @RequestBody WorkLogRequest request
    ) {
        return ApiResponse.success("작업 공수가 수정되었습니다.", workLogService.updateWorkLog(workLogId, request));
    }

    @DeleteMapping("/{workLogId}")
    public ApiResponse<Void> delete(@PathVariable UUID workLogId) {
        workLogService.deleteWorkLog(workLogId);
        return ApiResponse.success("작업 공수가 삭제되었습니다.", null);
    }
}
