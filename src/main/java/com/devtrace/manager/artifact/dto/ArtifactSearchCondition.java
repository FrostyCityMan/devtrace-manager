package com.devtrace.manager.artifact.dto;

import java.util.UUID;

public class ArtifactSearchCondition {

    private UUID projectId;
    private ArtifactType artifactType;

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
}
