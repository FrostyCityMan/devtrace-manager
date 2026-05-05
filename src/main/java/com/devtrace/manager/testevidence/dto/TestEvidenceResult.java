package com.devtrace.manager.testevidence.dto;

public enum TestEvidenceResult {
    SUCCESS("성공", "done"),
    FAIL("실패", "danger"),
    BLOCKED("차단", "hold");

    private final String label;
    private final String cssClass;

    TestEvidenceResult(String label, String cssClass) {
        this.label = label;
        this.cssClass = cssClass;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }
}
