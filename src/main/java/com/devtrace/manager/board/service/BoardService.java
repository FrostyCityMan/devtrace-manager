package com.devtrace.manager.board.service;

import com.devtrace.manager.board.dto.BoardAssigneeResponse;
import com.devtrace.manager.board.dto.BoardColumnResponse;
import com.devtrace.manager.board.dto.BoardSearchCondition;
import com.devtrace.manager.board.dto.BoardStatusUpdateRequest;
import com.devtrace.manager.issue.dto.IssueStatus;
import java.util.List;
import java.util.UUID;

/**
 * 칸반 보드 조회와 이슈 상태 변경 업무를 담당하는 서비스 계약입니다.
 *
 * <p>1차 보드는 별도 보드 설정 테이블 없이 ISSUE.STATUS를 컬럼 기준으로 사용합니다.</p>
 */
public interface BoardService {

    /**
     * 칸반 보드에서 사용하는 이슈 상태 컬럼 목록을 조회한다.
     *
     * @return 보드 상태 목록
     */
    List<IssueStatus> selectBoardStatusList();

    /**
     * 검색 조건에 맞는 보드 컬럼 목록을 조회한다.
     *
     * @param condition 프로젝트, 담당자, 우선순위, 키워드, 스프린트 검색 조건
     * @return 상태별 보드 컬럼 목록
     */
    List<BoardColumnResponse> selectBoardColumnList(BoardSearchCondition condition);

    /**
     * 보드 담당자 필터 목록을 조회한다.
     *
     * @param projectId 프로젝트 ID
     * @return 담당자 목록
     */
    List<BoardAssigneeResponse> selectBoardAssigneeList(UUID projectId);

    /**
     * 보드 카드의 이슈 상태를 변경한다.
     *
     * @param request 상태 변경 요청
     */
    void updateBoardIssueStatus(BoardStatusUpdateRequest request);
}
