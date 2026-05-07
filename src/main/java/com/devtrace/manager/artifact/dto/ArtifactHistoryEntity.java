package com.devtrace.manager.artifact.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ArtifactHistoryEntity {

    private UUID artifactId;
    private UUID projectId;
    private ArtifactType artifactType;
    private String fileName;
    private String filePath;
    private UUID generatedBy;
    private LocalDateTime generatedAt;

    /**
     * 산출물 이력 엔티티를 화면/API 응답 DTO로 변환합니다.
     *
     * @return 산출물 이력 응답 DTO
     */
    public ArtifactHistoryResponse toResponse() {
        ArtifactHistoryResponse response = new ArtifactHistoryResponse();
        response.setArtifactId(artifactId);
        response.setProjectId(projectId);
        response.setArtifactType(artifactType);
        response.setFileName(fileName);
        response.setFilePath(filePath);
        response.setGeneratedBy(generatedBy);
        response.setGeneratedAt(generatedAt);
        return response;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public UUID getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(UUID generatedBy) {
        this.generatedBy = generatedBy;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
