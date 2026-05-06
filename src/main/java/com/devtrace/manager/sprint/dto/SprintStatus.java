package com.devtrace.manager.sprint.dto;

public enum SprintStatus {
    PLANNED("계획", "planned"),
    ACTIVE("진행 중", "progress"),
    CLOSED("종료", "done");

    private final String label;
    private final String cssClass;

    SprintStatus(String label, String cssClass) {
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
