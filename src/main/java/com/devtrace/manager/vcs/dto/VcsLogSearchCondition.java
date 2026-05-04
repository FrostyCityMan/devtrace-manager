package com.devtrace.manager.vcs.dto;

import java.util.UUID;

public class VcsLogSearchCondition {

    private UUID projectId;
    private VcsType vcsType;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public VcsType getVcsType() {
        return vcsType;
    }

    public void setVcsType(VcsType vcsType) {
        this.vcsType = vcsType;
    }
}
