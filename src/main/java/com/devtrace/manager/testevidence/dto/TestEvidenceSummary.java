package com.devtrace.manager.testevidence.dto;

import java.util.List;

public class TestEvidenceSummary {

    private final int totalCount;
    private final int successCount;
    private final int failCount;
    private final int blockedCount;
    private final int screenshotCount;

    private TestEvidenceSummary(int totalCount, int successCount, int failCount, int blockedCount, int screenshotCount) {
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failCount = failCount;
        this.blockedCount = blockedCount;
        this.screenshotCount = screenshotCount;
    }

    /**
     * 테스트 증적 목록에서 판정별 건수 요약을 집계합니다.
     *
     * @param evidences 테스트 증적 목록
     * @return 테스트 증적 요약
     */
    public static TestEvidenceSummary from(List<TestEvidenceResponse> evidences) {
        List<TestEvidenceResponse> safeEvidences = evidences == null ? List.of() : evidences;
        int successCount = 0;
        int failCount = 0;
        int blockedCount = 0;
        int screenshotCount = 0;

        for (TestEvidenceResponse evidence : safeEvidences) {
            if (evidence.getResultStatus() == TestEvidenceResult.SUCCESS) {
                successCount++;
            } else if (evidence.getResultStatus() == TestEvidenceResult.FAIL) {
                failCount++;
            } else if (evidence.getResultStatus() == TestEvidenceResult.BLOCKED) {
                blockedCount++;
            }
            if (evidence.isHasScreenshot()) {
                screenshotCount++;
            }
        }

        return new TestEvidenceSummary(safeEvidences.size(), successCount, failCount, blockedCount, screenshotCount);
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public int getBlockedCount() {
        return blockedCount;
    }

    public int getScreenshotCount() {
        return screenshotCount;
    }
}
