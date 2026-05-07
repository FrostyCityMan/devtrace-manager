package com.devtrace.manager.issue.dao;

import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import com.devtrace.manager.issue.dto.IssueStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.ibatis.annotations.Param;

/**
 * 이슈 저장소에 접근하는 MyBatis DAO입니다.
 *
 * <p>이슈와 공수 합계 갱신에 필요한 SQL 호출을 제공합니다.</p>
 */
public interface IssueDao {

    /**
     * 이슈를 저장한다.
     *
     * @param issue 저장할 이슈 엔티티
     */
    void insertIssue(IssueEntity issue);

    /**
     * 이슈 기본 정보를 수정한다.
     *
     * @param issue 수정할 이슈 엔티티
     */
    void updateIssue(IssueEntity issue);

    /**
     * 이슈 상태와 완료일을 수정한다.
     *
     * @param issueId 이슈 ID
     * @param status 변경 상태
     * @param resolvedDate 실제 완료일
     * @param updatedAt 수정 일시
     */
    void updateIssueStatus(
            @Param("issueId") UUID issueId,
            @Param("status") IssueStatus status,
            @Param("resolvedDate") LocalDate resolvedDate,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    /**
     * 이슈의 실제 공수 합계를 수정한다.
     *
     * <p>작업 공수 등록/수정/삭제 후 WorkLog 합계를 ISSUE.SPENT_MINUTES에 반영한다.</p>
     *
     * @param issueId 이슈 ID
     * @param spentMinutes 분 단위 실제 공수 합계
     * @param updatedAt 수정 일시
     */
    void updateIssueSpentMinutes(
            @Param("issueId") UUID issueId,
            @Param("spentMinutes") int spentMinutes,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    /**
     * 이슈를 삭제한다.
     *
     * @param issueId 삭제 대상 이슈 ID
     */
    void deleteIssue(UUID issueId);

    /**
     * 이슈 ID로 단건을 조회한다.
     *
     * @param issueId 조회 대상 이슈 ID
     * @return 이슈 엔티티
     */
    Optional<IssueEntity> selectIssueByIdDetails(UUID issueId);

    /**
     * 검색 조건에 맞는 이슈 목록을 조회한다.
     *
     * @param condition 검색 조건
     * @return 이슈 엔티티 목록
     */
    List<IssueEntity> selectIssueList(IssueSearchCondition condition);
}
