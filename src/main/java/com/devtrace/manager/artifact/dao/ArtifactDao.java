package com.devtrace.manager.artifact.dao;

import com.devtrace.manager.artifact.dto.ArtifactHistoryEntity;
import com.devtrace.manager.artifact.dto.ArtifactSearchCondition;
import com.devtrace.manager.artifact.dto.DailyReportIssueRow;
import com.devtrace.manager.artifact.dto.TestResultEvidenceRow;
import com.devtrace.manager.artifact.dto.WeeklyReportIssueRow;
import com.devtrace.manager.artifact.dto.WeeklyReportVcsRow;
import com.devtrace.manager.artifact.dto.WeeklyReportWorkLogRow;
import com.devtrace.manager.testevidence.dto.TestEvidenceResult;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.apache.ibatis.annotations.Param;

/**
 * 산출물 생성에 필요한 이력과 원천 데이터를 조회하는 MyBatis DAO입니다.
 *
 * <p>DAO는 SQL 호출만 담당하며, 보고서 조립과 검증은 서비스 계층에서 수행합니다.</p>
 */
public interface ArtifactDao {

    /**
     * 산출물 생성 이력을 등록합니다.
     *
     * @param artifactHistory 저장할 산출물 이력 엔티티
     */
    void insertArtifactHistory(ArtifactHistoryEntity artifactHistory);

    /**
     * 산출물 생성 이력을 조회합니다.
     *
     * @param condition 프로젝트와 산출물 유형 검색 조건
     * @return 산출물 생성 이력 목록
     */
    List<ArtifactHistoryEntity> selectArtifactHistoryList(ArtifactSearchCondition condition);

    /**
     * 주간 업무보고에 포함할 이슈 목록을 조회합니다.
     *
     * @param projectId 프로젝트 ID
     * @param startDate 보고 시작일
     * @param endDate 보고 종료일
     * @return 주간 업무보고 이슈 행 목록
     */
    List<WeeklyReportIssueRow> selectWeeklyReportIssueList(
            @Param("projectId") UUID projectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 주간 업무보고에 포함할 작업 공수 목록을 조회합니다.
     *
     * @param projectId 프로젝트 ID
     * @param startDate 보고 시작일
     * @param endDate 보고 종료일
     * @return 주간 업무보고 공수 행 목록
     */
    List<WeeklyReportWorkLogRow> selectWeeklyReportWorkLogList(
            @Param("projectId") UUID projectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 주간 업무보고에 포함할 형상관리 변경이력을 조회합니다.
     *
     * @param projectId 프로젝트 ID
     * @param startDate 보고 시작일
     * @param endDate 보고 종료일
     * @return 주간 업무보고 변경이력 행 목록
     */
    List<WeeklyReportVcsRow> selectWeeklyReportVcsList(
            @Param("projectId") UUID projectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 일일 업무보고 진행 현황 계산에 사용할 프로젝트 이슈 목록을 조회합니다.
     *
     * @param projectId 프로젝트 ID
     * @return 일일 업무보고 이슈 행 목록
     */
    List<DailyReportIssueRow> selectDailyReportIssueList(
            @Param("projectId") UUID projectId
    );

    /**
     * 일일 업무보고에 포함할 기준일 작업 공수를 조회합니다.
     *
     * @param projectId 프로젝트 ID
     * @param baseDate 보고 기준일
     * @return 일일 업무보고 공수 행 목록
     */
    List<WeeklyReportWorkLogRow> selectDailyReportWorkLogList(
            @Param("projectId") UUID projectId,
            @Param("baseDate") LocalDate baseDate
    );

    /**
     * 일일 업무보고에 포함할 기준일 형상관리 변경이력을 조회합니다.
     *
     * @param projectId 프로젝트 ID
     * @param baseDate 보고 기준일
     * @return 일일 업무보고 변경이력 행 목록
     */
    List<WeeklyReportVcsRow> selectDailyReportVcsList(
            @Param("projectId") UUID projectId,
            @Param("baseDate") LocalDate baseDate
    );

    /**
     * 테스트 결과 보고서에 포함할 테스트 증적 목록을 조회합니다.
     *
     * @param projectId 프로젝트 ID
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @param issueId 선택 이슈 ID
     * @param resultStatus 판정 필터
     * @return 테스트 결과 보고서 증적 행 목록
     */
    List<TestResultEvidenceRow> selectTestResultEvidenceList(
            @Param("projectId") UUID projectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("issueId") UUID issueId,
            @Param("resultStatus") TestEvidenceResult resultStatus
    );
}
