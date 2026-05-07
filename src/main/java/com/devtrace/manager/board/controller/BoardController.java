package com.devtrace.manager.board.controller;

import com.devtrace.manager.board.dto.BoardSearchCondition;
import com.devtrace.manager.board.dto.BoardSummaryResponse;
import com.devtrace.manager.board.service.BoardService;
import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.service.ProjectService;
import com.devtrace.manager.sprint.dto.SprintResponse;
import com.devtrace.manager.sprint.dto.SprintSearchCondition;
import com.devtrace.manager.sprint.dto.SprintStatus;
import com.devtrace.manager.sprint.service.SprintService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Thymeleaf 기반 칸반 보드 화면을 제공하는 컨트롤러입니다.
 *
 * <p>프로젝트별 이슈 상태 컬럼, 담당자/우선순위/키워드/스프린트 필터, 보드 요약을 화면에 전달합니다.</p>
 */
@Controller
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;
    private final ProjectService projectService;
    private final SprintService sprintService;

    /**
     * 칸반 보드 화면 컨트롤러를 생성한다.
     *
     * @param boardService 보드 업무 서비스
     * @param projectService 프로젝트 선택 목록 서비스
     * @param sprintService 스프린트 필터 목록 서비스
     */
    public BoardController(BoardService boardService, ProjectService projectService, SprintService sprintService) {
        this.boardService = boardService;
        this.projectService = projectService;
        this.sprintService = sprintService;
    }

    /**
     * 우선순위 필터 목록을 화면 공통 모델로 제공한다.
     *
     * @return 이슈 우선순위 배열
     */
    @ModelAttribute("priorities")
    public IssuePriority[] priorities() {
        return IssuePriority.values();
    }

    /**
     * 프로젝트 필터 목록을 화면 공통 모델로 제공한다.
     *
     * @return 프로젝트 목록
     */
    @ModelAttribute("projects")
    public List<ProjectResponse> projects() {
        return projectService.getProjectList(new ProjectSearchCondition());
    }

    /**
     * 칸반 보드 화면을 표시한다.
     *
     * <p>프로젝트, 담당자, 우선순위, 키워드, 스프린트 필터를 기준으로 상태별 컬럼을 구성한다.</p>
     *
     * @param condition 보드 검색 조건
     * @param model Thymeleaf 모델
     * @return 칸반 보드 화면명
     */
    @GetMapping
    public String list(@ModelAttribute BoardSearchCondition condition, Model model) {
        var columns = boardService.selectBoardColumnList(condition);
        model.addAttribute("condition", condition);
        model.addAttribute("columns", columns);
        model.addAttribute("summary", BoardSummaryResponse.from(columns));
        model.addAttribute("boardStatuses", boardService.selectBoardStatusList());
        model.addAttribute("assignees", boardService.selectBoardAssigneeList(condition.getProjectId()));
        model.addAttribute("sprints", selectSprintList(condition));
        return "board/kanban";
    }

    /**
     * 보드 화면에 노출할 스프린트 필터 목록을 조회한다.
     *
     * <p>선택 스프린트가 없으면 활성 스프린트를 우선 노출하고, 활성 스프린트가 없을 때 전체 스프린트를 제공한다.</p>
     *
     * @param condition 보드 검색 조건
     * @return 스프린트 필터 목록
     */
    private List<SprintResponse> selectSprintList(BoardSearchCondition condition) {
        SprintSearchCondition sprintCondition = new SprintSearchCondition();
        sprintCondition.setProjectId(condition.getProjectId());
        if (condition.getSprintId() != null) {
            return sprintService.selectSprintList(sprintCondition);
        }
        sprintCondition.setStatus(SprintStatus.ACTIVE);
        List<SprintResponse> activeSprints = sprintService.selectSprintList(sprintCondition);
        if (!activeSprints.isEmpty()) {
            return activeSprints;
        }
        sprintCondition.setStatus(null);
        return sprintService.selectSprintList(sprintCondition);
    }
}
