package com.devtrace.manager.dashboard.dto;

import com.devtrace.manager.artifact.dto.ArtifactType;
import java.time.LocalDateTime;
import java.util.UUID;

public class DashboardArtifactSummaryResponse {

    private UUID artifactId;
    private UUID projectId;
    private String projectCode;
    private ArtifactType artifactType;
    private String fileName;
    private LocalDateTime generatedAt;

    public String getArtifactTypeLabel() {
        if (artifactType == null) {
            return "-";
        }
        return switch (artifactType) {
            case WEEKLY_REPORT -> "주간 업무보고";
            case DAILY_REPORT -> "일일 업무보고";
            case TEST_RESULT_REPORT -> "테스트 결과 보고서";
            case ISSUE_STATUS_REPORT -> "이슈 처리 현황";
            case WORKLOG_SUMMARY -> "공수 집계표";
        };
    }

    public UUID getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(UUID artifactId) {
        this.artifactId = artifactId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public ArtifactType getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(ArtifactType artifactType) {
        this.artifactType = artifactType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
