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

@RestController
@RequestMapping("/api/v1/test-evidences")
public class TestEvidenceApiController {

    private final TestEvidenceService testEvidenceService;

    public TestEvidenceApiController(TestEvidenceService testEvidenceService) {
        this.testEvidenceService = testEvidenceService;
    }

    @GetMapping
    public ApiResponse<List<TestEvidenceResponse>> list(TestEvidenceSearchCondition condition) {
        return ApiResponse.success(testEvidenceService.selectTestEvidenceList(condition));
    }

    @GetMapping("/{testEvidenceId}")
    public ApiResponse<TestEvidenceResponse> details(@PathVariable UUID testEvidenceId) {
        return ApiResponse.success(testEvidenceService.selectTestEvidenceDetails(testEvidenceId));
    }

    @PostMapping
    public ApiResponse<TestEvidenceResponse> create(@Valid @RequestBody TestEvidenceRequest request) {
        return ApiResponse.success("테스트 증적이 등록되었습니다.", testEvidenceService.insertTestEvidence(request));
    }

    @PutMapping("/{testEvidenceId}")
    public ApiResponse<TestEvidenceResponse> update(
            @PathVariable UUID testEvidenceId,
            @Valid @RequestBody TestEvidenceRequest request
    ) {
        return ApiResponse.success("테스트 증적이 수정되었습니다.", testEvidenceService.updateTestEvidence(testEvidenceId, request));
    }

    @DeleteMapping("/{testEvidenceId}")
    public ApiResponse<Void> delete(@PathVariable UUID testEvidenceId) {
        testEvidenceService.deleteTestEvidence(testEvidenceId);
        return ApiResponse.success("테스트 증적이 삭제되었습니다.", null);
    }
}
