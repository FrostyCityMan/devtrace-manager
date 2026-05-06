package com.devtrace.manager.artifact.dto;

import com.devtrace.manager.testevidence.dto.TestEvidenceResult;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public class ArtifactRequest {

    public static final UUID DEFAULT_ADMIN_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @NotNull(message = "프로젝트는 필수입니다.")
    private UUID projectId;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate baseDate;

    @NotNull(message = "산출물 유형은 필수입니다.")
    private ArtifactType artifactType = ArtifactType.WEEKLY_REPORT;

    private UUID issueId;
    private TestEvidenceResult resultStatus;
    private UUID generatedBy = DEFAULT_ADMIN_USER_ID;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
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

    public LocalDate getBaseDate() {
        return baseDate;
    }

    public void setBaseDate(LocalDate baseDate) {
        this.baseDate = baseDate;
    }

    public ArtifactType getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(ArtifactType artifactType) {
        this.artifactType = artifactType;
    }

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    public TestEvidenceResult getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(TestEvidenceResult resultStatus) {
        this.resultStatus = resultStatus;
    }

    public UUID getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(UUID generatedBy) {
        this.generatedBy = generatedBy;
    }
}
