package com.devtrace.manager.wbs.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class WbsTaskDependencyResponse {

    private UUID dependencyId;
    private UUID projectId;
    private UUID predecessorTaskId;
    private String predecessorWbsCode;
    private String predecessorTaskName;
    private WbsTaskStatus predecessorStatus;
    private UUID successorTaskId;
    private String successorWbsCode;
    private String successorTaskName;
    private WbsTaskStatus successorStatus;
    private WbsDependencyType dependencyType;
    private Integer lagDays;
    private LocalDateTime createdAt;

    public UUID getDependencyId() {
        return dependencyId;
    }

    public void setDependencyId(UUID dependencyId) {
        this.dependencyId = dependencyId;
    }

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

    public String getPredecessorWbsCode() {
        return predecessorWbsCode;
    }

    public void setPredecessorWbsCode(String predecessorWbsCode) {
        this.predecessorWbsCode = predecessorWbsCode;
    }

    public String getPredecessorTaskName() {
        return predecessorTaskName;
    }

    public void setPredecessorTaskName(String predecessorTaskName) {
        this.predecessorTaskName = predecessorTaskName;
    }

    public WbsTaskStatus getPredecessorStatus() {
        return predecessorStatus;
    }

    public void setPredecessorStatus(WbsTaskStatus predecessorStatus) {
        this.predecessorStatus = predecessorStatus;
    }

    public UUID getSuccessorTaskId() {
        return successorTaskId;
    }

    public void setSuccessorTaskId(UUID successorTaskId) {
        this.successorTaskId = successorTaskId;
    }

    public String getSuccessorWbsCode() {
        return successorWbsCode;
    }

    public void setSuccessorWbsCode(String successorWbsCode) {
        this.successorWbsCode = successorWbsCode;
    }

    public String getSuccessorTaskName() {
        return successorTaskName;
    }

    public void setSuccessorTaskName(String successorTaskName) {
        this.successorTaskName = successorTaskName;
    }

    public WbsTaskStatus getSuccessorStatus() {
        return successorStatus;
    }

    public void setSuccessorStatus(WbsTaskStatus successorStatus) {
        this.successorStatus = successorStatus;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
