package com.devtrace.manager.testevidence.controller;

import com.devtrace.manager.common.response.ApiResponse;
import com.devtrace.manager.testevidence.dto.TestEvidenceRequest;
import com.devtrace.manager.testevidence.dto.TestEvidenceResponse;
import com.devtrace.manager.testevidence.dto.TestEvidenceSearchCondition;
import com.devtrace.manager.testevidence.service.TestEvidenceService;
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
 * 테스트 증적 기능을 제공하는 REST 컨트롤러입니다.
 *
 * <p>화면 기능과 동일한 서비스 계약을 사용하여 테스트 증적의 조회, 등록, 수정, 삭제를
 * 공통 API 응답 형식으로 제공합니다.</p>
 */
@RestController
@RequestMapping("/api/v1/test-evidences")
public class TestEvidenceApiController {

    private final TestEvidenceService testEvidenceService;

    /**
     * 테스트 증적 API 컨트롤러를 생성합니다.
     *
     * @param testEvidenceService 테스트 증적 서비스
     */
    public TestEvidenceApiController(TestEvidenceService testEvidenceService) {
        this.testEvidenceService = testEvidenceService;
    }

    /**
     * 테스트 증적 목록을 조회합니다.
     *
     * @param condition 검색 조건
     * @return 테스트 증적 목록 API 응답
     */
    @GetMapping
    public ApiResponse<List<TestEvidenceResponse>> list(TestEvidenceSearchCondition condition) {
        return ApiResponse.success(testEvidenceService.selectTestEvidenceList(condition));
    }

    /**
     * 테스트 증적 상세 정보를 조회합니다.
     *
     * @param testEvidenceId 테스트 증적 ID
     * @return 테스트 증적 상세 API 응답
     */
    @GetMapping("/{testEvidenceId}")
    public ApiResponse<TestEvidenceResponse> details(@PathVariable UUID testEvidenceId) {
        return ApiResponse.success(testEvidenceService.selectTestEvidenceDetails(testEvidenceId));
    }

    /**
     * 테스트 증적을 등록합니다.
     *
     * @param request 등록 요청
     * @return 등록된 테스트 증적 API 응답
     */
    @PostMapping
    public ApiResponse<TestEvidenceResponse> create(@Valid @RequestBody TestEvidenceRequest request) {
        return ApiResponse.success("테스트 증적이 등록되었습니다.", testEvidenceService.insertTestEvidence(request));
    }

    /**
     * 테스트 증적을 수정합니다.
     *
     * @param testEvidenceId 테스트 증적 ID
     * @param request 수정 요청
     * @return 수정된 테스트 증적 API 응답
     */
    @PutMapping("/{testEvidenceId}")
    public ApiResponse<TestEvidenceResponse> update(
            @PathVariable UUID testEvidenceId,
            @Valid @RequestBody TestEvidenceRequest request
    ) {
        return ApiResponse.success("테스트 증적이 수정되었습니다.", testEvidenceService.updateTestEvidence(testEvidenceId, request));
    }

    /**
     * 테스트 증적을 삭제합니다.
     *
     * @param testEvidenceId 테스트 증적 ID
     * @return 삭제 결과 API 응답
     */
    @DeleteMapping("/{testEvidenceId}")
    public ApiResponse<Void> delete(@PathVariable UUID testEvidenceId) {
        testEvidenceService.deleteTestEvidence(testEvidenceId);
        return ApiResponse.success("테스트 증적이 삭제되었습니다.", null);
    }
}
