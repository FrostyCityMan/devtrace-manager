package com.devtrace.manager.sprint.service;

import com.devtrace.manager.sprint.dto.SprintBacklogSearchCondition;
import com.devtrace.manager.sprint.dto.SprintBurndownPointResponse;
import com.devtrace.manager.sprint.dto.SprintIssueOrderRequest;
import com.devtrace.manager.sprint.dto.SprintIssueRequest;
import com.devtrace.manager.sprint.dto.SprintIssueResponse;
import com.devtrace.manager.sprint.dto.SprintRequest;
import com.devtrace.manager.sprint.dto.SprintResponse;
import com.devtrace.manager.sprint.dto.SprintReportResponse;
import com.devtrace.manager.sprint.dto.SprintSearchCondition;
import com.devtrace.manager.sprint.dto.SprintSummaryResponse;
import java.util.List;
import java.util.UUID;

/**
 * 백로그, 스프린트 계획, 스프린트 분석 업무를 담당하는 서비스 계약입니다.
 *
 * <p>칸반 실행 전 계획 단위와 실행 결과 분석 데이터를 함께 제공합니다.</p>
 */
public interface SprintService {

    /**
     * 신규 스프린트를 등록한다.
     *
     * <p>프로젝트 존재 여부와 기간 유효성을 검증한 뒤, 상태가 비어 있으면
     * {@link com.devtrace.manager.sprint.dto.SprintStatus#PLANNED PLANNED} 상태로 저장한다.</p>
     *
     * @param request 스프린트 등록 요청
     * @return 등록된 스프린트 상세 응답
     */
    SprintResponse insertSprint(SprintRequest request);

    /**
     * 기존 스프린트 기본 정보를 수정한다.
     *
     * <p>스프린트 ID로 기존 데이터를 확인한 뒤 프로젝트, 명칭, 목표, 기간, 상태를 갱신한다.</p>
     *
     * @param sprintId 수정 대상 스프린트 ID
     * @param request 스프린트 수정 요청
     * @return 수정된 스프린트 상세 응답
     */
    SprintResponse updateSprint(UUID sprintId, SprintRequest request);

    /**
     * 계획 상태의 스프린트를 시작 상태로 전환한다.
     *
     * <p>동일 프로젝트에 이미 진행 중인 스프린트가 있으면 시작을 거부한다.
     * 이는 프로젝트별 실행 스프린트를 하나로 유지하기 위한 업무 규칙이다.</p>
     *
     * @param sprintId 시작할 스프린트 ID
     * @return 시작 처리된 스프린트 상세 응답
     */
    SprintResponse updateSprintStart(UUID sprintId);

    /**
     * 스프린트를 종료 상태로 전환한다.
     *
     * <p>이미 종료된 스프린트는 별도 변경 없이 현재 상태를 반환한다.</p>
     *
     * @param sprintId 종료할 스프린트 ID
     * @return 종료 처리된 스프린트 상세 응답
     */
    SprintResponse updateSprintClose(UUID sprintId);

    /**
     * 스프린트를 삭제한다.
     *
     * <p>삭제 전 대상 스프린트 존재 여부를 확인한다.</p>
     *
     * @param sprintId 삭제 대상 스프린트 ID
     */
    void deleteSprint(UUID sprintId);

    /**
     * 스프린트 단건 상세 정보를 조회한다.
     *
     * @param sprintId 조회 대상 스프린트 ID
     * @return 스프린트 상세 응답
     */
    SprintResponse selectSprintDetails(UUID sprintId);

    /**
     * 검색 조건에 맞는 스프린트 목록을 조회한다.
     *
     * @param condition 프로젝트, 상태, 키워드 검색 조건
     * @return 스프린트 목록
     */
    List<SprintResponse> selectSprintList(SprintSearchCondition condition);

    /**
     * 스프린트에 아직 배정되지 않은 백로그 이슈 목록을 조회한다.
     *
     * <p>조회 결과에는 현재일 기준 지연 여부를 계산하여 포함한다.</p>
     *
     * @param condition 프로젝트와 키워드 검색 조건
     * @return 배정 가능한 백로그 이슈 목록
     */
    List<SprintIssueResponse> selectBacklogIssueList(SprintBacklogSearchCondition condition);

    /**
     * 특정 스프린트에 배정된 이슈 목록을 조회한다.
     *
     * <p>화면 표시 순서와 이슈 지연 여부를 함께 제공한다.</p>
     *
     * @param sprintId 스프린트 ID
     * @return 스프린트 이슈 목록
     */
    List<SprintIssueResponse> selectSprintIssueList(UUID sprintId);

    /**
     * 스프린트 요약 지표를 조회한다.
     *
     * <p>전체/완료/진행/지연 이슈 수와 예상/실제 공수 합계를 포함한다.</p>
     *
     * @param sprintId 스프린트 ID
     * @return 스프린트 요약 응답
     */
    SprintSummaryResponse selectSprintSummaryDetails(UUID sprintId);

    /**
     * 스프린트 분석 리포트 전체 데이터를 조회한다.
     *
     * <p>상단 요약, Burndown Chart, 상태별 분포, 담당자별 작업량,
     * 위험 이슈, 실패/차단 테스트 증적을 하나의 응답으로 조합한다.</p>
     *
     * @param sprintId 분석 대상 스프린트 ID
     * @return 스프린트 분석 리포트 응답
     */
    SprintReportResponse selectSprintReportDetails(UUID sprintId);

    /**
     * Burndown Chart 표시용 일자별 포인트 목록을 조회한다.
     *
     * <p>조회 시점의 스프린트 일자별 스냅샷을 갱신하고, 저장된
     * {@code SPRINT_DAILY_SNAPSHOT.REMAINING_ESTIMATED_MINUTES}와
     * {@code SPRINT_DAILY_SNAPSHOT.SPENT_MINUTES}를 기준으로 실제선과 공수선을 만든다.</p>
     *
     * @param sprintId 분석 대상 스프린트 ID
     * @return Burndown Chart 포인트 목록
     */
    List<SprintBurndownPointResponse> selectSprintBurndownList(UUID sprintId);

    /**
     * 백로그 이슈를 스프린트에 배정한다.
     *
     * <p>스프린트와 이슈의 프로젝트가 일치해야 하며, 이미 배정된 이슈는 중복 배정하지 않는다.</p>
     *
     * @param sprintId 배정 대상 스프린트 ID
     * @param request 이슈 배정 요청
     * @return 배정된 스프린트 이슈 응답
     */
    SprintIssueResponse insertSprintIssue(UUID sprintId, SprintIssueRequest request);

    /**
     * 스프린트 내 이슈 표시 순서를 수정한다.
     *
     * @param sprintId 스프린트 ID
     * @param issueId 순서 변경 대상 이슈 ID
     * @param request 표시 순서 변경 요청
     */
    void updateSprintIssueDisplayOrder(UUID sprintId, UUID issueId, SprintIssueOrderRequest request);

    /**
     * 스프린트에서 이슈를 제외한다.
     *
     * <p>이슈 자체를 삭제하지 않고, 스프린트 배정 관계만 제거한다.</p>
     *
     * @param sprintId 스프린트 ID
     * @param issueId 제외 대상 이슈 ID
     */
    void deleteSprintIssue(UUID sprintId, UUID issueId);
}
