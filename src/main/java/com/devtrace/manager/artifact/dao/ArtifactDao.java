package com.devtrace.manager.artifact.dao;

import com.devtrace.manager.artifact.dto.ArtifactHistoryEntity;
import com.devtrace.manager.artifact.dto.ArtifactSearchCondition;
import com.devtrace.manager.artifact.dto.WeeklyReportIssueRow;
import com.devtrace.manager.artifact.dto.WeeklyReportVcsRow;
import com.devtrace.manager.artifact.dto.WeeklyReportWorkLogRow;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.apache.ibatis.annotations.Param;

public interface ArtifactDao {

    void insertArtifactHistory(ArtifactHistoryEntity artifactHistory);

    List<ArtifactHistoryEntity> selectArtifactHistoryList(ArtifactSearchCondition condition);

    List<WeeklyReportIssueRow> selectWeeklyReportIssueList(
            @Param("projectId") UUID projectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<WeeklyReportWorkLogRow> selectWeeklyReportWorkLogList(
            @Param("projectId") UUID projectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<WeeklyReportVcsRow> selectWeeklyReportVcsList(
            @Param("projectId") UUID projectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
