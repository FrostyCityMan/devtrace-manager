package com.devtrace.manager.wbs.dto;

public enum WbsDependencyType {
    FINISH_TO_START("완료 후 시작");

    private final String label;

    WbsDependencyType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
