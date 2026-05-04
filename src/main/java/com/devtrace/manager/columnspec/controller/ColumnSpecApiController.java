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

@RestController
@RequestMapping("/api/v1/column-specs")
public class ColumnSpecApiController {

    private final ColumnSpecService columnSpecService;

    public ColumnSpecApiController(ColumnSpecService columnSpecService) {
        this.columnSpecService = columnSpecService;
    }

    @GetMapping
    public ApiResponse<List<ColumnSpecResponse>> list(ColumnSpecSearchCondition condition) {
        return ApiResponse.success(columnSpecService.selectColumnSpecList(condition));
    }

    @PostMapping("/preview")
    public ApiResponse<List<ColumnSpecResponse>> preview(@Valid @RequestBody ColumnSpecRequest request) {
        return ApiResponse.success("컬럼명세 미리보기가 생성되었습니다.", columnSpecService.selectColumnSpecPreviewList(request));
    }
}
