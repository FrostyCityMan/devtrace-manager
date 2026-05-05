package com.devtrace.manager.testevidence.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class TestEvidenceSummaryTest {

    @Test
    void fromEvidenceList() {
        TestEvidenceSummary summary = TestEvidenceSummary.from(List.of(
                createEvidence(TestEvidenceResult.SUCCESS, true),
                createEvidence(TestEvidenceResult.FAIL, false),
                createEvidence(TestEvidenceResult.BLOCKED, true)
        ));

        assertThat(summary.getTotalCount()).isEqualTo(3);
        assertThat(summary.getSuccessCount()).isEqualTo(1);
        assertThat(summary.getFailCount()).isEqualTo(1);
        assertThat(summary.getBlockedCount()).isEqualTo(1);
        assertThat(summary.getScreenshotCount()).isEqualTo(2);
    }

    private TestEvidenceResponse createEvidence(TestEvidenceResult resultStatus, boolean hasScreenshot) {
        TestEvidenceResponse response = new TestEvidenceResponse();
        response.setResultStatus(resultStatus);
        if (hasScreenshot) {
            response.setScreenshotFilePath("uploads/test-evidences/sample.png");
        }
        return response;
    }
}
