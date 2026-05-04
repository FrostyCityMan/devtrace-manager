package com.devtrace.manager.project.dto;

public class ProjectSearchCondition {

    private String keyword;
    private ProjectStatus status;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
}
