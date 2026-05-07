package com.devtrace.manager.columnspec.controller;

import com.devtrace.manager.columnspec.dto.ColumnSpecRequest;
import com.devtrace.manager.columnspec.dto.ColumnSpecResponse;
import com.devtrace.manager.columnspec.dto.ColumnSpecSearchCondition;
import com.devtrace.manager.columnspec.service.ColumnSpecService;
import com.devtrace.manager.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 컬럼명세 생성 기능을 제공하는 REST 컨트롤러입니다.
 *
 * <p>DDL 파싱 미리보기와 컬럼명세 목록 조회를 공통 API 응답 형식으로 제공합니다.</p>
 */
@RestController
@RequestMapping("/api/v1/column-specs")
public class ColumnSpecApiController {

    private final ColumnSpecService columnSpecService;

    /**
     * 컬럼명세 REST API 컨트롤러를 생성한다.
     *
     * @param columnSpecService 컬럼명세 업무 서비스
     */
    public ColumnSpecApiController(ColumnSpecService columnSpecService) {
        this.columnSpecService = columnSpecService;
    }

    /**
     * 저장된 컬럼명세 목록을 조회한다.
     *
     * @param condition 컬럼명세 검색 조건
     * @return 컬럼명세 목록 API 응답
     */
    @GetMapping
    public ApiResponse<List<ColumnSpecResponse>> list(ColumnSpecSearchCondition condition) {
        return ApiResponse.success(columnSpecService.selectColumnSpecList(condition));
    }

    /**
     * DDL 분석 결과를 미리보기로 생성한다.
     *
     * @param request 컬럼명세 생성 요청
     * @return 컬럼명세 미리보기 목록 API 응답
     */
    @PostMapping("/preview")
    public ApiResponse<List<ColumnSpecResponse>> preview(@Valid @RequestBody ColumnSpecRequest request) {
        return ApiResponse.success("컬럼명세 미리보기가 생성되었습니다.", columnSpecService.selectColumnSpecPreviewList(request));
    }
}
