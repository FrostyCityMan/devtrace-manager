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
import com.devtrace.manager.sprint.service.SprintSnapshotService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 칸반 보드 조회와 카드 상태 변경 업무 규칙을 구현합니다.
 *
 * <p>상태별 컬럼을 고정 순서로 구성하고, 카드의 지연 여부와 보드 요약을 계산합니다.</p>
 */
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
    private final SprintSnapshotService sprintSnapshotService;

    /**
     * 보드 서비스 구현체를 생성한다.
     *
     * @param boardDao 보드 조회 DAO
     * @param issueDao 이슈 상태 변경 DAO
     * @param sprintSnapshotService 스프린트 일자별 스냅샷 서비스
     */
    public BoardServiceImpl(BoardDao boardDao, IssueDao issueDao, SprintSnapshotService sprintSnapshotService) {
        this.boardDao = boardDao;
        this.issueDao = issueDao;
        this.sprintSnapshotService = sprintSnapshotService;
    }

    /**
     * 칸반 보드 컬럼 상태 목록을 반환한다.
     *
     * @return 보드 상태 목록
     */
    @Override
    public List<IssueStatus> selectBoardStatusList() {
        return BOARD_STATUSES;
    }

    /**
     * 검색 조건에 맞는 보드 컬럼 목록을 조회한다.
     *
     * <p>ISSUE.STATUS를 기준으로 카드들을 그룹화하고, 보드에 표시할 고정 상태 컬럼 순서로 재구성한다.</p>
     *
     * @param condition 보드 검색 조건
     * @return 상태별 보드 컬럼 목록
     */
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

    /**
     * 보드 담당자 필터 목록을 조회한다.
     *
     * @param projectId 프로젝트 ID
     * @return 담당자 목록
     */
    @Override
    public List<BoardAssigneeResponse> selectBoardAssigneeList(UUID projectId) {
        return boardDao.selectBoardAssigneeList(projectId);
    }

    /**
     * 보드 카드 이동에 따른 이슈 상태를 변경한다.
     *
     * <p>보드에서 지원하지 않는 상태로의 변경은 거부하며, 완료 상태로 이동할 경우 완료일을 보정한다.</p>
     *
     * @param request 상태 변경 요청
     */
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
        sprintSnapshotService.saveSprintDailySnapshotByIssueId(issue.getIssueId());
    }

    /**
     * 보드 카드의 지연 여부를 판단한다.
     *
     * @param card 보드 카드 응답
     * @param today 지연 판단 기준일
     * @return 예정일이 지났고 완료되지 않았으면 true
     */
    private boolean isDelayed(BoardIssueCardResponse card, LocalDate today) {
        return card.getDueDate() != null
                && card.getDueDate().isBefore(today)
                && card.getStatus() != null
                && !card.getStatus().isCompleted();
    }

    /**
     * 이슈 엔티티를 조회하고 없으면 업무 예외를 발생시킨다.
     *
     * @param issueId 조회 대상 이슈 ID
     * @return 이슈 엔티티
     */
    private IssueEntity selectIssueDetails(UUID issueId) {
        return issueDao.selectIssueByIdDetails(issueId)
                .orElseThrow(() -> new BusinessException("Issue not found.", "ISSUE_NOT_FOUND"));
    }
}
