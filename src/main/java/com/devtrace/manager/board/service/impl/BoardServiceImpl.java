package com.devtrace.manager.board.service.impl;

import com.devtrace.manager.board.dao.BoardDao;
import com.devtrace.manager.board.dto.BoardAssigneeResponse;
import com.devtrace.manager.board.dto.BoardColumnResponse;
import com.devtrace.manager.board.dto.BoardIssueCardResponse;
import com.devtrace.manager.board.dto.BoardSearchCondition;
import com.devtrace.manager.board.dto.BoardStatusUpdateRequest;
import com.devtrace.manager.board.service.BoardService;
import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.common.util.DateTimeUtil;
import com.devtrace.manager.issue.dao.IssueDao;
import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.issue.dto.IssueStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private static final List<IssueStatus> BOARD_STATUSES = List.of(
            IssueStatus.REGISTERED,
            IssueStatus.ANALYZING,
            IssueStatus.IN_PROGRESS,
            IssueStatus.DEV_DONE,
            IssueStatus.TESTING,
            IssueStatus.DONE
    );

    private final BoardDao boardDao;
    private final IssueDao issueDao;

    public BoardServiceImpl(BoardDao boardDao, IssueDao issueDao) {
        this.boardDao = boardDao;
        this.issueDao = issueDao;
    }

    @Override
    public List<IssueStatus> selectBoardStatusList() {
        return BOARD_STATUSES;
    }

    @Override
    public List<BoardColumnResponse> selectBoardColumnList(BoardSearchCondition condition) {
        BoardSearchCondition searchCondition = condition == null ? new BoardSearchCondition() : condition;
        List<BoardIssueCardResponse> cards = boardDao.selectBoardIssueCardList(searchCondition);
        LocalDate today = LocalDate.now();
        cards.forEach(card -> card.setDelayed(isDelayed(card, today)));

        Map<IssueStatus, List<BoardIssueCardResponse>> cardsByStatus = cards.stream()
                .collect(Collectors.groupingBy(BoardIssueCardResponse::getStatus, () -> new EnumMap<>(IssueStatus.class), Collectors.toList()));

        return BOARD_STATUSES.stream()
                .map(status -> new BoardColumnResponse(status, cardsByStatus.getOrDefault(status, List.of())))
                .toList();
    }

    @Override
    public List<BoardAssigneeResponse> selectBoardAssigneeList(UUID projectId) {
        return boardDao.selectBoardAssigneeList(projectId);
    }

    @Override
    @Transactional
    public void updateBoardIssueStatus(BoardStatusUpdateRequest request) {
        if (request == null || request.getIssueId() == null) {
            throw new BusinessException("Issue ID is required.", "BOARD_ISSUE_ID_REQUIRED");
        }
        if (request.getStatus() == null) {
            throw new BusinessException("Board status is required.", "BOARD_STATUS_REQUIRED");
        }
        if (!BOARD_STATUSES.contains(request.getStatus())) {
            throw new BusinessException("Unsupported board status.", "BOARD_STATUS_UNSUPPORTED");
        }

        IssueEntity issue = selectIssueDetails(request.getIssueId());
        LocalDateTime now = DateTimeUtil.now();
        LocalDate resolvedDate = issue.getResolvedDate();
        if (request.getStatus().isCompleted() && resolvedDate == null) {
            resolvedDate = now.toLocalDate();
        }

        issueDao.updateIssueStatus(issue.getIssueId(), request.getStatus(), resolvedDate, now);
    }

    private boolean isDelayed(BoardIssueCardResponse card, LocalDate today) {
        return card.getDueDate() != null
                && card.getDueDate().isBefore(today)
                && card.getStatus() != null
                && !card.getStatus().isCompleted();
    }

    private IssueEntity selectIssueDetails(UUID issueId) {
        return issueDao.selectIssueByIdDetails(issueId)
                .orElseThrow(() -> new BusinessException("Issue not found.", "ISSUE_NOT_FOUND"));
    }
}
