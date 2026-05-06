package com.devtrace.manager.dashboard.dao;

import com.devtrace.manager.dashboard.dto.DashboardArtifactSummaryResponse;
import com.devtrace.manager.dashboard.dto.DashboardBoardSummaryResponse;
import com.devtrace.manager.dashboard.dto.DashboardProjectHealthResponse;
import com.devtrace.manager.dashboard.dto.DashboardRecentChangeLogResponse;
import com.devtrace.manager.dashboard.dto.DashboardRiskIssueResponse;
import com.devtrace.manager.dashboard.dto.DashboardSummaryResponse;
import com.devtrace.manager.dashboard.dto.DashboardTestEvidenceSummaryResponse;
import com.devtrace.manager.dashboard.dto.DashboardWbsSummaryResponse;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DashboardDao {

    DashboardSummaryResponse selectDashboardSummaryDetails(@Param("today") LocalDate today);

    List<DashboardRiskIssueResponse> selectTodayDueIssueList(@Param("today") LocalDate today);

    List<DashboardRiskIssueResponse> selectDelayedIssueList(@Param("today") LocalDate today);

    List<DashboardWbsSummaryResponse> selectDelayedWbsTaskList(@Param("today") LocalDate today);

    List<DashboardTestEvidenceSummaryResponse> selectFailedTestEvidenceList();

    List<DashboardRecentChangeLogResponse> selectRecentChangeLogList();

    List<DashboardProjectHealthResponse> selectProjectHealthList(@Param("today") LocalDate today);

    List<DashboardBoardSummaryResponse> selectBoardSummaryList();

    List<DashboardRiskIssueResponse> selectRecentDoneIssueList();

    List<DashboardTestEvidenceSummaryResponse> selectRecentTestEvidenceList();

    List<DashboardArtifactSummaryResponse> selectRecentArtifactList();
}
