package com.devtrace.manager.testevidence.service;

import com.devtrace.manager.testevidence.dto.TestEvidenceRequest;
import com.devtrace.manager.testevidence.dto.TestEvidenceResponse;
import com.devtrace.manager.testevidence.dto.TestEvidenceScreenshotResponse;
import com.devtrace.manager.testevidence.dto.TestEvidenceSearchCondition;
import java.util.List;
import java.util.UUID;

public interface TestEvidenceService {

    TestEvidenceResponse insertTestEvidence(TestEvidenceRequest request);

    TestEvidenceResponse updateTestEvidence(UUID testEvidenceId, TestEvidenceRequest request);

    void deleteTestEvidence(UUID testEvidenceId);

    TestEvidenceResponse selectTestEvidenceDetails(UUID testEvidenceId);

    List<TestEvidenceResponse> selectTestEvidenceList(TestEvidenceSearchCondition condition);

    TestEvidenceScreenshotResponse selectTestEvidenceScreenshotDetails(UUID testEvidenceId);
}
