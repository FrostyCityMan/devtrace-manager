package com.devtrace.manager.board.controller;

import com.devtrace.manager.board.dto.BoardAssigneeResponse;
import com.devtrace.manager.board.dto.BoardColumnResponse;
import com.devtrace.manager.board.dto.BoardSearchCondition;
import com.devtrace.manager.board.dto.BoardStatusUpdateRequest;
import com.devtrace.manager.board.service.BoardService;
import com.devtrace.manager.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/boards")
public class BoardApiController {

    private final BoardService boardService;

    public BoardApiController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public ApiResponse<List<BoardColumnResponse>> list(BoardSearchCondition condition) {
        return ApiResponse.success(boardService.selectBoardColumnList(condition));
    }

    @GetMapping("/assignees")
    public ApiResponse<List<BoardAssigneeResponse>> assigneeList(@RequestParam(required = false) UUID projectId) {
        return ApiResponse.success(boardService.selectBoardAssigneeList(projectId));
    }

    @PatchMapping("/status")
    public ApiResponse<Void> updateStatus(@Valid @RequestBody BoardStatusUpdateRequest request) {
        boardService.updateBoardIssueStatus(request);
        return ApiResponse.success("Board issue status has been updated.", null);
    }
}
