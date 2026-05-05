package com.devtrace.manager.wbs.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class WbsTaskDependencyRequest {

    @NotNull(message = "프로젝트는 필수입니다.")
    private UUID projectId;

    @NotNull(message = "선행 작업은 필수입니다.")
    private UUID predecessorTaskId;

    @NotNull(message = "후행 작업은 필수입니다.")
    private UUID successorTaskId;

    @NotNull(message = "의존성 유형은 필수입니다.")
    private WbsDependencyType dependencyType = WbsDependencyType.FINISH_TO_START;

    @Min(value = 0, message = "지연 일수는 0 이상이어야 합니다.")
    private Integer lagDays = 0;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getPredecessorTaskId() {
        return predecessorTaskId;
    }

    public void setPredecessorTaskId(UUID predecessorTaskId) {
        this.predecessorTaskId = predecessorTaskId;
    }

    public UUID getSuccessorTaskId() {
        return successorTaskId;
    }

    public void setSuccessorTaskId(UUID successorTaskId) {
        this.successorTaskId = successorTaskId;
    }

    public WbsDependencyType getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(WbsDependencyType dependencyType) {
        this.dependencyType = dependencyType;
    }

    public Integer getLagDays() {
        return lagDays;
    }

    public void setLagDays(Integer lagDays) {
        this.lagDays = lagDays;
    }
}
