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

/**
 * 산출물 생성 기능을 외부 클라이언트에 제공하는 REST 컨트롤러입니다.
 *
 * <p>Thymeleaf 화면과 동일한 서비스 계층을 사용하되, 응답은 공통 API 응답 형식으로
 * 감싸서 산출물 미리보기와 생성 이력 조회를 제공합니다.</p>
 */
@RestController
@RequestMapping("/api/v1/artifacts")
public class ArtifactApiController {

    private final ArtifactService artifactService;

    /**
     * 산출물 API 컨트롤러를 생성합니다.
     *
     * @param artifactService 산출물 생성 서비스
     */
    public ArtifactApiController(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    /**
     * 산출물 생성 이력 목록을 조회합니다.
     *
     * @param condition 산출물 검색 조건
     * @return 생성 이력 목록 API 응답
     */
    @GetMapping
    public ApiResponse<List<ArtifactHistoryResponse>> list(ArtifactSearchCondition condition) {
        return ApiResponse.success(artifactService.selectArtifactHistoryList(condition));
    }

    /**
     * 주간 업무보고 Markdown 미리보기를 생성합니다.
     *
     * @param request 산출물 생성 요청
     * @return Markdown 미리보기 API 응답
     */
    @PostMapping("/weekly-report/preview")
    public ApiResponse<ArtifactMarkdownResponse> previewWeeklyReport(@Valid @RequestBody ArtifactRequest request) {
        return ApiResponse.success("주간 업무보고 미리보기가 생성되었습니다.", artifactService.selectWeeklyReportPreviewDetails(request));
    }

    /**
     * 주간 업무보고 Markdown을 생성하고 이력을 저장합니다.
     *
     * @param request 산출물 생성 요청
     * @return 생성된 Markdown API 응답
     */
    @PostMapping("/weekly-report")
    public ApiResponse<ArtifactMarkdownResponse> createWeeklyReport(@Valid @RequestBody ArtifactRequest request) {
        return ApiResponse.success("주간 업무보고 산출물이 생성되었습니다.", artifactService.insertWeeklyReportMarkdown(request));
    }

    /**
     * 일일 업무보고 Markdown 미리보기를 생성합니다.
     *
     * @param request 산출물 생성 요청
     * @return Markdown 미리보기 API 응답
     */
    @PostMapping("/daily-report/preview")
    public ApiResponse<ArtifactMarkdownResponse> previewDailyReport(@Valid @RequestBody ArtifactRequest request) {
        return ApiResponse.success("일일 업무보고 미리보기가 생성되었습니다.", artifactService.selectDailyReportPreviewDetails(request));
    }

    /**
     * 테스트 결과 보고서 Markdown 미리보기를 생성합니다.
     *
     * @param request 산출물 생성 요청
     * @return Markdown 미리보기 API 응답
     */
    @PostMapping("/test-result/preview")
    public ApiResponse<ArtifactMarkdownResponse> previewTestResultReport(@Valid @RequestBody ArtifactRequest request) {
        return ApiResponse.success("테스트 결과 보고서 미리보기가 생성되었습니다.", artifactService.selectTestResultReportPreviewDetails(request));
    }
}
