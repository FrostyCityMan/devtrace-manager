package com.devtrace.manager.sprint.dao;

import com.devtrace.manager.sprint.dto.SprintDailySnapshotEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.apache.ibatis.annotations.Param;

/**
 * 스프린트 일자별 스냅샷 SQL 호출 DAO다.
 *
 * <p>현재 스프린트 배정 이슈와 작업 공수를 기준으로 저장할 스냅샷 값을 조회하고,
 * 일자별 스냅샷을 upsert 방식으로 보존한다. 업무 판단과 이벤트 흐름은 서비스 계층이 담당한다.</p>
 */
public interface SprintSnapshotDao {

    /**
     * 스프린트의 특정 일자 스냅샷 산출 원천 데이터를 조회한다.
     *
     * <p>완료/잔여 이슈 수, 완료/잔여 예상 공수, 해당 일자까지의 누적 실제 공수를 계산한다.</p>
     *
     * @param sprintId 스프린트 ID
     * @param snapshotDate 스냅샷 일자
     * @return 저장할 스냅샷 원천 엔티티
     */
    SprintDailySnapshotEntity selectSprintDailySnapshotSourceDetails(
            @Param("sprintId") UUID sprintId,
            @Param("snapshotDate") LocalDate snapshotDate
    );

    /**
     * 스프린트 일자별 스냅샷을 저장하거나 갱신한다.
     *
     * @param snapshot 저장할 스냅샷 엔티티
     */
    void saveSprintDailySnapshot(SprintDailySnapshotEntity snapshot);

    /**
     * 스프린트의 저장된 일자별 스냅샷 목록을 조회한다.
     *
     * @param sprintId 스프린트 ID
     * @return 스냅샷 목록
     */
    List<SprintDailySnapshotEntity> selectSprintDailySnapshotList(UUID sprintId);

    /**
     * 특정 이슈가 배정된 스프린트 ID 목록을 조회한다.
     *
     * <p>이슈 상태 변경 또는 작업 공수 변경 이벤트에서 영향을 받는 스프린트의 당일 스냅샷을
     * 갱신하기 위해 사용한다.</p>
     *
     * @param issueId 이슈 ID
     * @return 이슈가 배정된 스프린트 ID 목록
     */
    List<UUID> selectSprintIdByIssueIdList(UUID issueId);
}
