package com.devtrace.manager.artifact.service;

import com.devtrace.manager.artifact.dto.ArtifactHistoryResponse;
import com.devtrace.manager.artifact.dto.ArtifactMarkdownResponse;
import com.devtrace.manager.artifact.dto.ArtifactRequest;
import com.devtrace.manager.artifact.dto.ArtifactSearchCondition;
import java.util.List;

public interface ArtifactService {

    ArtifactMarkdownResponse selectWeeklyReportPreviewDetails(ArtifactRequest request);

    ArtifactMarkdownResponse insertWeeklyReportMarkdown(ArtifactRequest request);

    List<ArtifactHistoryResponse> selectArtifactHistoryList(ArtifactSearchCondition condition);
}
