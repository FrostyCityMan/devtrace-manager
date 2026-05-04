package com.devtrace.manager.issue.dto;

public enum IssueStatus {
    REGISTERED("등록", "planned", false, false),
    ANALYZING("분석 중", "hold", true, false),
    IN_PROGRESS("진행 중", "progress", true, false),
    DEV_DONE("개발 완료", "progress", true, false),
    REVIEWING("검토 중", "hold", true, false),
    TESTING("테스트 중", "hold", true, false),
    REJECTED("반려", "danger", false, false),
    DONE("완료", "done", false, true),
    HOLD("보류", "hold", false, false),
    CLOSED("종료", "done", false, true);

    private final String label;
    private final String cssClass;
    private final boolean active;
    private final boolean completed;

    IssueStatus(String label, String cssClass, boolean active, boolean completed) {
        this.label = label;
        this.cssClass = cssClass;
        this.active = active;
        this.completed = completed;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isCompleted() {
        return completed;
    }
}
