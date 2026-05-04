package com.devtrace.manager.issue.dto;

public enum IssuePriority {
    URGENT("긴급", "stripe-red", "danger"),
    HIGH("높음", "stripe-amber", "planned"),
    NORMAL("보통", "stripe-teal", "progress"),
    LOW("낮음", "stripe-blue", "hold");

    private final String label;
    private final String rowClass;
    private final String cssClass;

    IssuePriority(String label, String rowClass, String cssClass) {
        this.label = label;
        this.rowClass = rowClass;
        this.cssClass = cssClass;
    }

    public String getLabel() {
        return label;
    }

    public String getRowClass() {
        return rowClass;
    }

    public String getCssClass() {
        return cssClass;
    }
}
