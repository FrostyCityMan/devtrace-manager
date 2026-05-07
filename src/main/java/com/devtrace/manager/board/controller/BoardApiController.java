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

/**
 * 칸반 보드 기능을 제공하는 REST 컨트롤러입니다.
 *
 * <p>프로젝트별 상태 컬럼 조회와 카드 드래그 앤 드롭 상태 변경 API를 제공합니다.</p>
 */
@RestController
@RequestMapping("/api/v1/boards")
public class BoardApiController {

    private final BoardService boardService;

    /**
     * 칸반 보드 REST API 컨트롤러를 생성한다.
     *
     * @param boardService 보드 업무 서비스
     */
    public BoardApiController(BoardService boardService) {
        this.boardService = boardService;
    }

    /**
     * 상태별 보드 컬럼 목록을 조회한다.
     *
     * @param condition 보드 검색 조건
     * @return 보드 컬럼 목록 API 응답
     */
    @GetMapping
    public ApiResponse<List<BoardColumnResponse>> list(BoardSearchCondition condition) {
        return ApiResponse.success(boardService.selectBoardColumnList(condition));
    }

    /**
     * 보드 담당자 필터 목록을 조회한다.
     *
     * @param projectId 프로젝트 ID
     * @return 담당자 목록 API 응답
     */
    @GetMapping("/assignees")
    public ApiResponse<List<BoardAssigneeResponse>> assigneeList(@RequestParam(required = false) UUID projectId) {
        return ApiResponse.success(boardService.selectBoardAssigneeList(projectId));
    }

    /**
     * 드래그 앤 드롭으로 이동된 이슈의 상태를 변경한다.
     *
     * @param request 보드 상태 변경 요청
     * @return 처리 결과 API 응답
     */
    @PatchMapping("/status")
    public ApiResponse<Void> updateStatus(@Valid @RequestBody BoardStatusUpdateRequest request) {
        boardService.updateBoardIssueStatus(request);
        return ApiResponse.success("Board issue status has been updated.", null);
    }
}
