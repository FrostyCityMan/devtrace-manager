package com.devtrace.manager.wbs.dto;

public enum WbsTaskStatus {
    READY("대기", "planned", false),
    IN_PROGRESS("진행 중", "progress", false),
    DONE("완료", "done", true),
    HOLD("보류", "hold", false);

    private final String label;
    private final String cssClass;
    private final boolean completed;

    WbsTaskStatus(String label, String cssClass, boolean completed) {
        this.label = label;
        this.cssClass = cssClass;
        this.completed = completed;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }

    public boolean isCompleted() {
        return completed;
    }
}
