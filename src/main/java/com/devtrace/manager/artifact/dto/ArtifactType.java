package com.devtrace.manager.artifact.dto;

public enum ArtifactType {
    WEEKLY_REPORT("주간 업무보고");

    private final String label;

    ArtifactType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
