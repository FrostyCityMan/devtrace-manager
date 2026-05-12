package com.devtrace.manager.sprint.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.sprint.dao.SprintDao;
import com.devtrace.manager.sprint.dao.SprintSnapshotDao;
import com.devtrace.manager.sprint.dto.SprintDailySnapshotEntity;
import com.devtrace.manager.sprint.dto.SprintDailySnapshotResponse;
import com.devtrace.manager.sprint.dto.SprintEntity;
import com.devtrace.manager.sprint.dto.SprintStatus;
import com.devtrace.manager.sprint.service.impl.SprintSnapshotServiceImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SprintSnapshotServiceImplTest {

    @Mock
    private SprintSnapshotDao sprintSnapshotDao;

    @Mock
    private SprintDao sprintDao;

    private SprintSnapshotService sprintSnapshotService;

    @BeforeEach
    void setUp() {
        sprintSnapshotService = new SprintSnapshotServiceImpl(sprintSnapshotDao, sprintDao);
    }

    @Test
    void saveSprintDailySnapshotCalculatesAndUpsertsSnapshot() {
        UUID sprintId = UUID.randomUUID();
        LocalDate snapshotDate = LocalDate.of(2026, 5, 8);
        SprintDailySnapshotEntity source = createSnapshotSource(sprintId, snapshotDate);
        when(sprintDao.selectSprintDetails(sprintId)).thenReturn(Optional.of(createSprint(sprintId)));
        when(sprintSnapshotDao.selectSprintDailySnapshotSourceDetails(sprintId, snapshotDate)).thenReturn(source);

        SprintDailySnapshotResponse response = sprintSnapshotService.saveSprintDailySnapshot(sprintId, snapshotDate);

        ArgumentCaptor<SprintDailySnapshotEntity> captor = ArgumentCaptor.forClass(SprintDailySnapshotEntity.class);
        verify(sprintSnapshotDao).saveSprintDailySnapshot(captor.capture());
        SprintDailySnapshotEntity saved = captor.getValue();
        assertThat(saved.getSnapshotId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getRemainingEstimatedMinutes()).isEqualTo(360);
        assertThat(response.getSnapshotId()).isEqualTo(saved.getSnapshotId());
        assertThat(response.getSpentMinutes()).isEqualTo(180);
    }

    @Test
    void saveSprintDailySnapshotRejectsMissingSprint() {
        UUID sprintId = UUID.randomUUID();
        when(sprintDao.selectSprintDetails(sprintId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sprintSnapshotService.saveSprintDailySnapshot(sprintId, LocalDate.of(2026, 5, 8)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("스프린트를 찾을 수 없습니다.");
    }

    @Test
    void saveSprintDailySnapshotByIssueIdUpdatesAssignedSprints() {
        UUID issueId = UUID.randomUUID();
        UUID sprintId = UUID.randomUUID();
        when(sprintSnapshotDao.selectSprintIdByIssueIdList(issueId)).thenReturn(List.of(sprintId));
        when(sprintDao.selectSprintDetails(sprintId)).thenReturn(Optional.of(createSprint(sprintId)));
        when(sprintSnapshotDao.selectSprintDailySnapshotSourceDetails(eq(sprintId), any(LocalDate.class)))
                .thenAnswer(invocation -> createSnapshotSource(sprintId, invocation.getArgument(1)));

        sprintSnapshotService.saveSprintDailySnapshotByIssueId(issueId);

        verify(sprintSnapshotDao).saveSprintDailySnapshot(any(SprintDailySnapshotEntity.class));
    }

    @Test
    void selectSprintDailySnapshotListReturnsResponses() {
        UUID sprintId = UUID.randomUUID();
        LocalDate snapshotDate = LocalDate.of(2026, 5, 8);
        when(sprintDao.selectSprintDetails(sprintId)).thenReturn(Optional.of(createSprint(sprintId)));
        when(sprintSnapshotDao.selectSprintDailySnapshotList(sprintId))
                .thenReturn(List.of(createSavedSnapshot(sprintId, snapshotDate)));

        List<SprintDailySnapshotResponse> responses = sprintSnapshotService.selectSprintDailySnapshotList(sprintId);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getSnapshotDate()).isEqualTo(snapshotDate);
        assertThat(responses.get(0).getRemainingEstimatedMinutes()).isEqualTo(360);
    }

    private SprintDailySnapshotEntity createSnapshotSource(UUID sprintId, LocalDate snapshotDate) {
        SprintDailySnapshotEntity snapshot = new SprintDailySnapshotEntity();
        snapshot.setSprintId(sprintId);
        snapshot.setSnapshotDate(snapshotDate);
        snapshot.setTotalIssueCount(3);
        snapshot.setDoneIssueCount(1);
        snapshot.setRemainingIssueCount(2);
        snapshot.setTotalEstimatedMinutes(600);
        snapshot.setDoneEstimatedMinutes(240);
        snapshot.setRemainingEstimatedMinutes(360);
        snapshot.setSpentMinutes(180);
        return snapshot;
    }

    private SprintDailySnapshotEntity createSavedSnapshot(UUID sprintId, LocalDate snapshotDate) {
        SprintDailySnapshotEntity snapshot = createSnapshotSource(sprintId, snapshotDate);
        snapshot.setSnapshotId(UUID.randomUUID());
        snapshot.setCreatedAt(LocalDateTime.of(2026, 5, 8, 9, 0));
        return snapshot;
    }

    private SprintEntity createSprint(UUID sprintId) {
        SprintEntity sprint = new SprintEntity();
        sprint.setSprintId(sprintId);
        sprint.setProjectId(UUID.randomUUID());
        sprint.setSprintName("2026년 5월 1차 스프린트");
        sprint.setStatus(SprintStatus.ACTIVE);
        sprint.setStartDate(LocalDate.of(2026, 5, 4));
        sprint.setEndDate(LocalDate.of(2026, 5, 15));
        sprint.setCreatedAt(LocalDateTime.of(2026, 5, 4, 9, 0));
        return sprint;
    }
}
