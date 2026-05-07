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

/**
 * 형상관리 변경이력 기능을 제공하는 REST 컨트롤러입니다.
 *
 * <p>로그 분석 미리보기와 변경이력 목록 조회 API를 공통 응답 형식으로 제공합니다.</p>
 */
@RestController
@RequestMapping("/api/v1/vcs-logs")
public class VcsLogApiController {

    private final VcsLogService vcsLogService;

    /**
     * VCS 변경이력 REST API 컨트롤러를 생성한다.
     *
     * @param vcsLogService VCS 변경이력 업무 서비스
     */
    public VcsLogApiController(VcsLogService vcsLogService) {
        this.vcsLogService = vcsLogService;
    }

    /**
     * 저장된 변경이력 목록을 조회한다.
     *
     * @param condition 변경이력 검색 조건
     * @return 변경이력 목록 API 응답
     */
    @GetMapping
    public ApiResponse<List<VcsChangeLogResponse>> list(VcsLogSearchCondition condition) {
        return ApiResponse.success(vcsLogService.selectChangeLogList(condition));
    }

    /**
     * VCS 로그 텍스트를 분석해 변경이력 미리보기를 생성한다.
     *
     * @param request VCS 로그 분석 요청
     * @return 변경이력 미리보기 목록 API 응답
     */
    @PostMapping("/preview")
    public ApiResponse<List<VcsChangeLogResponse>> preview(@Valid @RequestBody VcsLogRequest request) {
        return ApiResponse.success("변경이력 미리보기가 생성되었습니다.", vcsLogService.selectChangeLogPreviewList(request));
    }
}
