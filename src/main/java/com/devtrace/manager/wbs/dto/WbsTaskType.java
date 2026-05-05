package com.devtrace.manager.wbs.dto;

public enum WbsTaskType {
    PHASE("단계"),
    DELIVERABLE("산출물"),
    TASK("작업");

    private final String label;

    WbsTaskType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
