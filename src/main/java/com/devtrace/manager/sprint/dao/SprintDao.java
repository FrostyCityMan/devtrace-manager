package com.devtrace.manager.sprint.dao;

import com.devtrace.manager.sprint.dto.SprintAssigneeWorkloadResponse;
import com.devtrace.manager.sprint.dto.SprintBacklogSearchCondition;
import com.devtrace.manager.sprint.dto.SprintEntity;
import com.devtrace.manager.sprint.dto.SprintIssueEntity;
import com.devtrace.manager.sprint.dto.SprintIssueResponse;
import com.devtrace.manager.sprint.dto.SprintRiskIssueResponse;
import com.devtrace.manager.sprint.dto.SprintSearchCondition;
import com.devtrace.manager.sprint.dto.SprintStatus;
import com.devtrace.manager.sprint.dto.SprintStatusDistributionResponse;
import com.devtrace.manager.sprint.dto.SprintSummaryResponse;
import com.devtrace.manager.sprint.dto.SprintTestEvidenceRiskResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.ibatis.annotations.Param;

/**
 * 스프린트, 스프린트 이슈, 분석 리포트 데이터를 조회하는 MyBatis DAO입니다.
 *
 * <p>백로그와 스프린트 실행 현황을 SQL 호출 단위로 제공하고, 업무 계산은 서비스 계층에 둡니다.</p>
 */
public interface SprintDao {

    /**
     * 스프린트 기본 정보를 저장한다.
     *
     * @param sprint 저장할 스프린트 엔티티
     */
    void insertSprint(SprintEntity sprint);

    /**
     * 스프린트 기본 정보를 갱신한다.
     *
     * @param sprint 수정할 스프린트 엔티티
     */
    void updateSprint(SprintEntity sprint);

    /**
     * 스프린트 상태만 갱신한다.
     *
     * <p>시작/종료 전환처럼 상태 변경만 필요한 업무 흐름에서 사용한다.</p>
     *
     * @param sprintId 상태 변경 대상 스프린트 ID
     * @param status 변경할 상태
     * @param updatedAt 수정 일시
     */
    void updateSprintStatus(
            @Param("sprintId") UUID sprintId,
            @Param("status") SprintStatus status,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    /**
     * 스프린트를 삭제한다.
     *
     * @param sprintId 삭제 대상 스프린트 ID
     */
    void deleteSprint(UUID sprintId);

    /**
     * 스프린트 단건 정보를 조회한다.
     *
     * @param sprintId 조회 대상 스프린트 ID
     * @return 스프린트 엔티티
     */
    Optional<SprintEntity> selectSprintDetails(UUID sprintId);

    /**
     * 프로젝트에 연결된 진행 중 스프린트를 조회한다.
     *
     * <p>프로젝트별 활성 스프린트 중복 시작을 방지하기 위한 검증 조회다.</p>
     *
     * @param projectId 프로젝트 ID
     * @return 활성 스프린트 엔티티
     */
    Optional<SprintEntity> selectActiveSprintByProjectIdDetails(UUID projectId);

    /**
     * 검색 조건에 맞는 스프린트 목록을 조회한다.
     *
     * @param condition 프로젝트, 상태, 키워드 검색 조건
     * @return 스프린트 엔티티 목록
     */
    List<SprintEntity> selectSprintList(SprintSearchCondition condition);

    /**
     * 스프린트에 배정되지 않은 백로그 이슈 목록을 조회한다.
     *
     * @param condition 프로젝트와 키워드 검색 조건
     * @return 백로그 이슈 목록
     */
    List<SprintIssueResponse> selectBacklogIssueList(SprintBacklogSearchCondition condition);

    /**
     * 특정 스프린트에 배정된 이슈 목록을 조회한다.
     *
     * @param sprintId 스프린트 ID
     * @return 스프린트 이슈 목록
     */
    List<SprintIssueResponse> selectSprintIssueList(UUID sprintId);

    /**
     * 스프린트와 이슈의 배정 관계를 조회한다.
     *
     * @param sprintId 스프린트 ID
     * @param issueId 이슈 ID
     * @return 스프린트 이슈 배정 엔티티
     */
    Optional<SprintIssueEntity> selectSprintIssueDetails(
            @Param("sprintId") UUID sprintId,
            @Param("issueId") UUID issueId
    );

    /**
     * 스프린트 내 이슈 표시 순서의 최댓값을 조회한다.
     *
     * <p>신규 배정 시 명시 순서가 없으면 이 값 다음 번호를 사용한다.</p>
     *
     * @param sprintId 스프린트 ID
     * @return 현재 최대 표시 순서
     */
    int selectSprintIssueMaxDisplayOrder(UUID sprintId);

    /**
     * 스프린트 요약 지표를 조회한다.
     *
     * @param sprintId 스프린트 ID
     * @param today 지연 판단 기준일
     * @return 스프린트 요약 응답
     */
    SprintSummaryResponse selectSprintSummaryDetails(
            @Param("sprintId") UUID sprintId,
            @Param("today") LocalDate today
    );

    /**
     * 스프린트 이슈의 상태별 분포를 조회한다.
     *
     * @param sprintId 스프린트 ID
     * @return 상태별 이슈 건수 목록
     */
    List<SprintStatusDistributionResponse> selectSprintStatusDistributionList(UUID sprintId);

    /**
     * 스프린트 담당자별 작업량을 조회한다.
     *
     * <p>담당자, 이슈 수, 예상 공수, 실제 공수 합계를 제공한다.</p>
     *
     * @param sprintId 스프린트 ID
     * @return 담당자별 작업량 목록
     */
    List<SprintAssigneeWorkloadResponse> selectSprintAssigneeWorkloadList(UUID sprintId);

    /**
     * 스프린트 위험 이슈 목록을 조회한다.
     *
     * <p>지연, 고우선순위 미완료, 공수 초과 이슈를 위험 항목으로 본다.</p>
     *
     * @param sprintId 스프린트 ID
     * @param today 지연 판단 기준일
     * @return 위험 이슈 목록
     */
    List<SprintRiskIssueResponse> selectSprintRiskIssueList(
            @Param("sprintId") UUID sprintId,
            @Param("today") LocalDate today
    );

    /**
     * 스프린트 이슈에 연결된 실패/차단 테스트 증적을 조회한다.
     *
     * @param sprintId 스프린트 ID
     * @return 테스트 증적 위험 목록
     */
    List<SprintTestEvidenceRiskResponse> selectSprintTestEvidenceRiskList(UUID sprintId);

    /**
     * 스프린트와 이슈의 배정 관계를 저장한다.
     *
     * @param sprintIssue 저장할 스프린트 이슈 엔티티
     */
    void insertSprintIssue(SprintIssueEntity sprintIssue);

    /**
     * 스프린트 내 이슈 표시 순서를 수정한다.
     *
     * @param sprintId 스프린트 ID
     * @param issueId 이슈 ID
     * @param displayOrder 변경할 표시 순서
     */
    void updateSprintIssueDisplayOrder(
            @Param("sprintId") UUID sprintId,
            @Param("issueId") UUID issueId,
            @Param("displayOrder") int displayOrder
    );

    /**
     * 스프린트와 이슈의 배정 관계를 삭제한다.
     *
     * @param sprintId 스프린트 ID
     * @param issueId 이슈 ID
     */
    void deleteSprintIssue(
            @Param("sprintId") UUID sprintId,
            @Param("issueId") UUID issueId
    );
}
