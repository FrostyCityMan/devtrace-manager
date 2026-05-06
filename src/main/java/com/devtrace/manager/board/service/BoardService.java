package com.devtrace.manager.board.service;

import com.devtrace.manager.board.dto.BoardAssigneeResponse;
import com.devtrace.manager.board.dto.BoardColumnResponse;
import com.devtrace.manager.board.dto.BoardSearchCondition;
import com.devtrace.manager.board.dto.BoardStatusUpdateRequest;
import com.devtrace.manager.issue.dto.IssueStatus;
import java.util.List;
import java.util.UUID;

public interface BoardService {

    List<IssueStatus> selectBoardStatusList();

    List<BoardColumnResponse> selectBoardColumnList(BoardSearchCondition condition);

    List<BoardAssigneeResponse> selectBoardAssigneeList(UUID projectId);

    void updateBoardIssueStatus(BoardStatusUpdateRequest request);
}
