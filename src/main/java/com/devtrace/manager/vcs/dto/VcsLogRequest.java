package com.devtrace.manager.vcs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class VcsLogRequest {

    @NotNull(message = "프로젝트는 필수입니다.")
    private UUID projectId;

    @NotNull(message = "VCS 유형은 필수입니다.")
    private VcsType vcsType = VcsType.GIT;

    @NotBlank(message = "로그 텍스트는 필수입니다.")
    private String logText;

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

    public String getLogText() {
        return logText;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }
}
