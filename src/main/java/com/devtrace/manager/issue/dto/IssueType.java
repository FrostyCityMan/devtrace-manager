package com.devtrace.manager.issue.dto;

public enum IssueType {
    REQUIREMENT("요구사항"),
    FEATURE("기능"),
    BUG("결함"),
    IMPROVEMENT("개선"),
    TEST("테스트"),
    DEPLOY("배포"),
    DOCUMENT("문서"),
    MEETING("회의"),
    QUESTION("문의"),
    INCIDENT("장애");

    private final String label;

    IssueType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
