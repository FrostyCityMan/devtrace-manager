package com.devtrace.manager.sprint.service.impl;

import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.common.util.DateTimeUtil;
import com.devtrace.manager.sprint.dao.SprintDao;
import com.devtrace.manager.sprint.dao.SprintSnapshotDao;
import com.devtrace.manager.sprint.dto.SprintDailySnapshotEntity;
import com.devtrace.manager.sprint.dto.SprintDailySnapshotResponse;
import com.devtrace.manager.sprint.service.SprintSnapshotService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 스프린트 일자별 스냅샷 업무 규칙을 구현한다.
 *
 * <p>스프린트의 현재 상태를 특정 일자 기준으로 계산하고, 같은 일자의 스냅샷은
 * 갱신하여 Burndown Chart와 분석 리포트가 일관된 기준 데이터를 사용하게 한다.</p>
 */
@Service
@Transactional(readOnly = true)
public class SprintSnapshotServiceImpl implements SprintSnapshotService {

    private final SprintSnapshotDao sprintSnapshotDao;
    private final SprintDao sprintDao;

    /**
     * 스프린트 스냅샷 서비스 구현체를 생성한다.
     *
     * @param sprintSnapshotDao 스냅샷 SQL 호출 DAO
     * @param sprintDao 스프린트 존재 검증 DAO
     */
    public SprintSnapshotServiceImpl(SprintSnapshotDao sprintSnapshotDao, SprintDao sprintDao) {
        this.sprintSnapshotDao = sprintSnapshotDao;
        this.sprintDao = sprintDao;
    }

    /**
     * 오늘 기준 스프린트 스냅샷을 저장하거나 갱신한다.
     *
     * @param sprintId 스프린트 ID
     * @return 저장된 스냅샷 응답
     */
    @Override
    @Transactional
    public SprintDailySnapshotResponse saveSprintDailySnapshot(UUID sprintId) {
        return saveSprintDailySnapshot(sprintId, LocalDate.now());
    }

    /**
     * 지정 일자 기준 스프린트 스냅샷을 저장하거나 갱신한다.
     *
     * <p>스프린트 존재 여부를 먼저 검증하고, DAO의 집계 SQL 결과에 스냅샷 ID와 생성 일시를
     * 부여한 뒤 upsert로 저장한다.</p>
     *
     * @param sprintId 스프린트 ID
     * @param snapshotDate 스냅샷 일자
     * @return 저장된 스냅샷 응답
     */
    @Override
    @Transactional
    public SprintDailySnapshotResponse saveSprintDailySnapshot(UUID sprintId, LocalDate snapshotDate) {
        if (snapshotDate == null) {
            throw new BusinessException("스냅샷 일자는 필수입니다.", "SPRINT_SNAPSHOT_DATE_REQUIRED");
        }
        sprintDao.selectSprintDetails(sprintId)
                .orElseThrow(() -> new BusinessException("스프린트를 찾을 수 없습니다.", "SPRINT_NOT_FOUND"));

        SprintDailySnapshotEntity snapshot = sprintSnapshotDao.selectSprintDailySnapshotSourceDetails(sprintId, snapshotDate);
        if (snapshot == null) {
            throw new BusinessException("스프린트 스냅샷 원천 데이터를 찾을 수 없습니다.", "SPRINT_SNAPSHOT_SOURCE_NOT_FOUND");
        }
        snapshot.setSnapshotId(UUID.randomUUID());
        snapshot.setCreatedAt(DateTimeUtil.now());
        sprintSnapshotDao.saveSprintDailySnapshot(snapshot);
        return snapshot.toResponse();
    }

    /**
     * 특정 이슈가 속한 모든 스프린트의 오늘 스냅샷을 저장하거나 갱신한다.
     *
     * <p>이슈가 어떤 스프린트에도 배정되어 있지 않으면 조용히 종료한다. 이는 백로그 상태의
     * 이슈 변경이 스프린트 분석 데이터에 영향을 주지 않는다는 업무 기준을 반영한다.</p>
     *
     * @param issueId 변경 이벤트가 발생한 이슈 ID
     */
    @Override
    @Transactional
    public void saveSprintDailySnapshotByIssueId(UUID issueId) {
        sprintSnapshotDao.selectSprintIdByIssueIdList(issueId)
                .forEach(this::saveSprintDailySnapshot);
    }

    /**
     * 저장된 스프린트 일자별 스냅샷 목록을 조회한다.
     *
     * @param sprintId 스프린트 ID
     * @return 스냅샷 응답 목록
     */
    @Override
    public List<SprintDailySnapshotResponse> selectSprintDailySnapshotList(UUID sprintId) {
        sprintDao.selectSprintDetails(sprintId)
                .orElseThrow(() -> new BusinessException("스프린트를 찾을 수 없습니다.", "SPRINT_NOT_FOUND"));
        return sprintSnapshotDao.selectSprintDailySnapshotList(sprintId).stream()
                .map(SprintDailySnapshotEntity::toResponse)
                .toList();
    }
}
