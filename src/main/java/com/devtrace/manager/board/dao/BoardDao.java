package com.devtrace.manager.board.dao;

import com.devtrace.manager.board.dto.BoardAssigneeResponse;
import com.devtrace.manager.board.dto.BoardIssueCardResponse;
import com.devtrace.manager.board.dto.BoardSearchCondition;
import java.util.List;
import java.util.UUID;
import org.apache.ibatis.annotations.Param;

public interface BoardDao {

    List<BoardIssueCardResponse> selectBoardIssueCardList(BoardSearchCondition condition);

    List<BoardAssigneeResponse> selectBoardAssigneeList(@Param("projectId") UUID projectId);
}
