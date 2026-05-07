package com.devtrace.manager.board.dao;

import com.devtrace.manager.board.dto.BoardAssigneeResponse;
import com.devtrace.manager.board.dto.BoardIssueCardResponse;
import com.devtrace.manager.board.dto.BoardSearchCondition;
import java.util.List;
import java.util.UUID;
import org.apache.ibatis.annotations.Param;

/**
 * 칸반 보드 전용 조회와 상태 변경 SQL을 호출하는 MyBatis DAO입니다.
 */
public interface BoardDao {

    /**
     * 보드 카드 목록을 조회한다.
     *
     * @param condition 보드 검색 조건
     * @return 보드 카드 목록
     */
    List<BoardIssueCardResponse> selectBoardIssueCardList(BoardSearchCondition condition);

    /**
     * 보드 담당자 필터 목록을 조회한다.
     *
     * @param projectId 프로젝트 ID
     * @return 담당자 목록
     */
    List<BoardAssigneeResponse> selectBoardAssigneeList(@Param("projectId") UUID projectId);
}
