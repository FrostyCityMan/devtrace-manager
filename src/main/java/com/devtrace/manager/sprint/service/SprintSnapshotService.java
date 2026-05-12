package com.devtrace.manager.sprint.service;

import com.devtrace.manager.sprint.dto.SprintDailySnapshotResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * 스프린트 일자별 스냅샷 업무 계약이다.
 *
 * <p>스프린트 시작, 리포트 조회, 스프린트 이슈 변경, 이슈 상태 변경, 작업 공수 변경 시점에
 * 현재 스프린트 상태를 일자별 기준 데이터로 저장한다.</p>
 */
public interface SprintSnapshotService {

    /**
     * 오늘 기준 스프린트 스냅샷을 저장하거나 갱신한다.
     *
     * @param sprintId 스프린트 ID
     * @return 저장된 스냅샷 응답
     */
    SprintDailySnapshotResponse saveSprintDailySnapshot(UUID sprintId);

    /**
     * 지정 일자 기준 스프린트 스냅샷을 저장하거나 갱신한다.
     *
     * @param sprintId 스프린트 ID
     * @param snapshotDate 스냅샷 일자
     * @return 저장된 스냅샷 응답
     */
    SprintDailySnapshotResponse saveSprintDailySnapshot(UUID sprintId, LocalDate snapshotDate);

    /**
     * 특정 이슈가 속한 모든 스프린트의 오늘 스냅샷을 저장하거나 갱신한다.
     *
     * @param issueId 변경 이벤트가 발생한 이슈 ID
     */
    void saveSprintDailySnapshotByIssueId(UUID issueId);

    /**
     * 저장된 스프린트 일자별 스냅샷 목록을 조회한다.
     *
     * @param sprintId 스프린트 ID
     * @return 스냅샷 응답 목록
     */
    List<SprintDailySnapshotResponse> selectSprintDailySnapshotList(UUID sprintId);
}
