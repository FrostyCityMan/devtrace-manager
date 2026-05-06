package com.devtrace.manager.artifact.dto;

public enum ArtifactType {
    WEEKLY_REPORT("주간 업무보고"),
    DAILY_REPORT("일일 업무보고"),
    TEST_RESULT_REPORT("테스트 결과 보고서"),
    ISSUE_STATUS_REPORT("이슈 처리 현황"),
    WORKLOG_SUMMARY("공수 집계표");

    private final String label;

    ArtifactType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
