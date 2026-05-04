package com.devtrace.manager.vcs.controller;

import com.devtrace.manager.common.response.ApiResponse;
import com.devtrace.manager.vcs.dto.VcsChangeLogResponse;
import com.devtrace.manager.vcs.dto.VcsLogRequest;
import com.devtrace.manager.vcs.dto.VcsLogSearchCondition;
import com.devtrace.manager.vcs.service.VcsLogService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vcs-logs")
public class VcsLogApiController {

    private final VcsLogService vcsLogService;

    public VcsLogApiController(VcsLogService vcsLogService) {
        this.vcsLogService = vcsLogService;
    }

    @GetMapping
    public ApiResponse<List<VcsChangeLogResponse>> list(VcsLogSearchCondition condition) {
        return ApiResponse.success(vcsLogService.selectChangeLogList(condition));
    }

    @PostMapping("/preview")
    public ApiResponse<List<VcsChangeLogResponse>> preview(@Valid @RequestBody VcsLogRequest request) {
        return ApiResponse.success("변경이력 미리보기가 생성되었습니다.", vcsLogService.selectChangeLogPreviewList(request));
    }
}
