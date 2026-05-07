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

/**
 * 통합 운영 대시보드에 필요한 집계 데이터를 조회하는 MyBatis DAO입니다.
 *
 * <p>대시보드는 여러 도메인의 데이터를 읽기 전용으로 조합하므로,
 * 이 DAO는 화면 블록 단위의 전용 조회 SQL만 제공합니다.</p>
 */
@Mapper
public interface DashboardDao {

    /**
     * 핵심 지표 요약을 조회합니다.
     *
     * @param today 지연 판단 기준일
     * @return 대시보드 핵심 지표
     */
    DashboardSummaryResponse selectDashboardSummaryDetails(@Param("today") LocalDate today);

    /**
     * 오늘 마감 예정 이슈를 조회합니다.
     *
     * @param today 기준일
     * @return 오늘 마감 이슈 목록
     */
    List<DashboardRiskIssueResponse> selectTodayDueIssueList(@Param("today") LocalDate today);

    /**
     * 지연 이슈를 조회합니다.
     *
     * @param today 지연 판단 기준일
     * @return 지연 이슈 목록
     */
    List<DashboardRiskIssueResponse> selectDelayedIssueList(@Param("today") LocalDate today);

    /**
     * 지연된 WBS 작업을 조회합니다.
     *
     * @param today 지연 판단 기준일
     * @return 지연 WBS 작업 목록
     */
    List<DashboardWbsSummaryResponse> selectDelayedWbsTaskList(@Param("today") LocalDate today);

    /**
     * 실패 또는 차단 상태의 테스트 증적을 조회합니다.
     *
     * @return 실패/차단 테스트 증적 목록
     */
    List<DashboardTestEvidenceSummaryResponse> selectFailedTestEvidenceList();

    /**
     * 최근 형상관리 변경이력을 조회합니다.
     *
     * @return 최근 변경이력 목록
     */
    List<DashboardRecentChangeLogResponse> selectRecentChangeLogList();

    /**
     * 프로젝트별 건강도 요약을 조회합니다.
     *
     * @param today 지연 판단 기준일
     * @return 프로젝트 건강도 목록
     */
    List<DashboardProjectHealthResponse> selectProjectHealthList(@Param("today") LocalDate today);

    /**
     * 칸반 상태별 이슈 수를 조회합니다.
     *
     * @return 상태별 이슈 수 목록
     */
    List<DashboardBoardSummaryResponse> selectBoardSummaryList();

    /**
     * 최근 완료 이슈를 조회합니다.
     *
     * @return 최근 완료 이슈 목록
     */
    List<DashboardRiskIssueResponse> selectRecentDoneIssueList();

    /**
     * 최근 등록된 테스트 증적을 조회합니다.
     *
     * @return 최근 테스트 증적 목록
     */
    List<DashboardTestEvidenceSummaryResponse> selectRecentTestEvidenceList();

    /**
     * 최근 생성된 산출물을 조회합니다.
     *
     * @return 최근 산출물 목록
     */
    List<DashboardArtifactSummaryResponse> selectRecentArtifactList();
}
