package com.devtrace.manager.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devtrace.manager.board.dao.BoardDao;
import com.devtrace.manager.board.dto.BoardColumnResponse;
import com.devtrace.manager.board.dto.BoardIssueCardResponse;
import com.devtrace.manager.board.dto.BoardSearchCondition;
import com.devtrace.manager.board.dto.BoardStatusUpdateRequest;
import com.devtrace.manager.board.dto.BoardSummaryResponse;
import com.devtrace.manager.board.service.impl.BoardServiceImpl;
import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.issue.dao.IssueDao;
import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.issue.dto.IssueStatus;
import com.devtrace.manager.issue.dto.IssueType;
import com.devtrace.manager.sprint.service.SprintSnapshotService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoardServiceImplTest {

    @Mock
    private BoardDao boardDao;

    @Mock
    private IssueDao issueDao;

    @Mock
    private SprintSnapshotService sprintSnapshotService;

    private BoardService boardService;

    @BeforeEach
    void setUp() {
        boardService = new BoardServiceImpl(boardDao, issueDao, sprintSnapshotService);
    }

    @Test
    void selectBoardColumnListReturnsFixedColumnsAndDelayedCards() {
        BoardIssueCardResponse delayedCard = createCard(IssueStatus.IN_PROGRESS, LocalDate.now().minusDays(1));
        BoardIssueCardResponse doneCard = createCard(IssueStatus.DONE, LocalDate.now().minusDays(1));
        when(boardDao.selectBoardIssueCardList(any(BoardSearchCondition.class))).thenReturn(List.of(delayedCard, doneCard));

        List<BoardColumnResponse> columns = boardService.selectBoardColumnList(new BoardSearchCondition());

        assertThat(columns).hasSize(6);
        assertThat(columns).extracting(BoardColumnResponse::getStatus)
                .containsExactly(
                        IssueStatus.REGISTERED,
                        IssueStatus.ANALYZING,
                        IssueStatus.IN_PROGRESS,
                        IssueStatus.DEV_DONE,
                        IssueStatus.TESTING,
                        IssueStatus.DONE
                );
        assertThat(columns.get(2).getIssueCount()).isEqualTo(1);
        assertThat(columns.get(2).getIssues().get(0).isDelayed()).isTrue();
        assertThat(columns.get(5).getIssues().get(0).isDelayed()).isFalse();

        BoardSummaryResponse summary = BoardSummaryResponse.from(columns);
        assertThat(summary.getTotalCount()).isEqualTo(2);
        assertThat(summary.getActiveCount()).isEqualTo(1);
        assertThat(summary.getDoneCount()).isEqualTo(1);
        assertThat(summary.getDelayedCount()).isEqualTo(1);
        assertThat(summary.getUnassignedCount()).isEqualTo(2);
    }

    @Test
    void updateBoardIssueStatusUpdatesIssueStatus() {
        UUID issueId = UUID.randomUUID();
        when(issueDao.selectIssueByIdDetails(issueId)).thenReturn(Optional.of(createIssue(issueId)));
        BoardStatusUpdateRequest request = new BoardStatusUpdateRequest();
        request.setIssueId(issueId);
        request.setStatus(IssueStatus.DONE);

        boardService.updateBoardIssueStatus(request);

        verify(issueDao).updateIssueStatus(eq(issueId), eq(IssueStatus.DONE), any(LocalDate.class), any(LocalDateTime.class));
        verify(sprintSnapshotService).saveSprintDailySnapshotByIssueId(issueId);
    }

    @Test
    void updateBoardIssueStatusRejectsNonBoardStatus() {
        BoardStatusUpdateRequest request = new BoardStatusUpdateRequest();
        request.setIssueId(UUID.randomUUID());
        request.setStatus(IssueStatus.REVIEWING);

        assertThatThrownBy(() -> boardService.updateBoardIssueStatus(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Unsupported board status.");
    }

    private BoardIssueCardResponse createCard(IssueStatus status, LocalDate dueDate) {
        BoardIssueCardResponse card = new BoardIssueCardResponse();
        card.setIssueId(UUID.randomUUID());
        card.setProjectId(UUID.randomUUID());
        card.setIssueKey("DTR-101");
        card.setTitle("Kanban board");
        card.setIssueType(IssueType.FEATURE);
        card.setStatus(status);
        card.setPriority(IssuePriority.NORMAL);
        card.setDueDate(dueDate);
        card.setEstimatedMinutes(480);
        card.setSpentMinutes(120);
        return card;
    }

    private IssueEntity createIssue(UUID issueId) {
        IssueEntity issue = new IssueEntity();
        issue.setIssueId(issueId);
        issue.setProjectId(UUID.randomUUID());
        issue.setIssueKey("DTR-101");
        issue.setIssueType(IssueType.FEATURE);
        issue.setTitle("Kanban board");
        issue.setStatus(IssueStatus.IN_PROGRESS);
        issue.setPriority(IssuePriority.NORMAL);
        issue.setCreatedAt(LocalDateTime.of(2026, 5, 6, 9, 0));
        return issue;
    }
}
