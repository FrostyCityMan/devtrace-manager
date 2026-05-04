package com.devtrace.manager.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class ProjectRequest {

    @NotBlank(message = "프로젝트 코드는 필수입니다.")
    @Size(max = 50, message = "프로젝트 코드는 50자 이하여야 합니다.")
    private String projectCode;

    @NotBlank(message = "프로젝트명은 필수입니다.")
    @Size(max = 200, message = "프로젝트명은 200자 이하여야 합니다.")
    private String projectName;

    @Size(max = 200, message = "고객사명은 200자 이하여야 합니다.")
    private String clientName;

    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    @NotNull(message = "상태는 필수입니다.")
    private ProjectStatus status = ProjectStatus.READY;

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
}
