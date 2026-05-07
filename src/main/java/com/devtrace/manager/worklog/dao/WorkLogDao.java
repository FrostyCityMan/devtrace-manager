package com.devtrace.manager.worklog.dao;

import com.devtrace.manager.worklog.dto.WorkLogEntity;
import com.devtrace.manager.worklog.dto.WorkLogSearchCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 작업 공수 저장소에 접근하는 MyBatis DAO입니다.
 *
 * <p>공수 원장 조회와 이슈별 공수 합계 계산 SQL을 제공합니다.</p>
 */
public interface WorkLogDao {

    /**
     * 작업 공수를 저장한다.
     *
     * @param workLog 저장할 작업 공수 엔티티
     */
    void insertWorkLog(WorkLogEntity workLog);

    /**
     * 작업 공수를 수정한다.
     *
     * @param workLog 수정할 작업 공수 엔티티
     */
    void updateWorkLog(WorkLogEntity workLog);

    /**
     * 작업 공수를 삭제한다.
     *
     * @param workLogId 삭제 대상 작업 공수 ID
     */
    void deleteWorkLog(UUID workLogId);

    /**
     * 작업 공수 ID로 단건을 조회한다.
     *
     * @param workLogId 조회 대상 작업 공수 ID
     * @return 작업 공수 엔티티
     */
    Optional<WorkLogEntity> selectWorkLogByIdDetails(UUID workLogId);

    /**
     * 검색 조건에 맞는 작업 공수 목록을 조회한다.
     *
     * @param condition 이슈/사용자 검색 조건
     * @return 작업 공수 엔티티 목록
     */
    List<WorkLogEntity> selectWorkLogList(WorkLogSearchCondition condition);

    /**
     * 이슈별 작업 공수 합계를 조회한다.
     *
     * @param issueId 이슈 ID
     * @return 분 단위 작업 공수 합계
     */
    int sumSpentMinutesByIssueId(UUID issueId);
}
