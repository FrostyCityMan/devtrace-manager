package com.devtrace.manager.artifact.controller;

import com.devtrace.manager.artifact.dto.ArtifactHistoryResponse;
import com.devtrace.manager.artifact.dto.ArtifactMarkdownResponse;
import com.devtrace.manager.artifact.dto.ArtifactRequest;
import com.devtrace.manager.artifact.dto.ArtifactSearchCondition;
import com.devtrace.manager.artifact.service.ArtifactService;
import com.devtrace.manager.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/artifacts")
public class ArtifactApiController {

    private final ArtifactService artifactService;

    public ArtifactApiController(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    @GetMapping
    public ApiResponse<List<ArtifactHistoryResponse>> list(ArtifactSearchCondition condition) {
        return ApiResponse.success(artifactService.selectArtifactHistoryList(condition));
    }

    @PostMapping("/weekly-report/preview")
    public ApiResponse<ArtifactMarkdownResponse> preview(@Valid @RequestBody ArtifactRequest request) {
        return ApiResponse.success("주간 업무보고 미리보기가 생성되었습니다.", artifactService.selectWeeklyReportPreviewDetails(request));
    }

    @PostMapping("/weekly-report")
    public ApiResponse<ArtifactMarkdownResponse> create(@Valid @RequestBody ArtifactRequest request) {
        return ApiResponse.success("주간 업무보고 산출물이 생성되었습니다.", artifactService.insertWeeklyReportMarkdown(request));
    }
}
