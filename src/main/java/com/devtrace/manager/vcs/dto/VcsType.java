package com.devtrace.manager.vcs.dto;

public enum VcsType {
    GIT("Git"),
    SVN("SVN");

    private final String label;

    VcsType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
