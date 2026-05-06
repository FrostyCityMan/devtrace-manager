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

@Controller
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;
    private final ProjectService projectService;
    private final SprintService sprintService;

    public BoardController(BoardService boardService, ProjectService projectService, SprintService sprintService) {
        this.boardService = boardService;
        this.projectService = projectService;
        this.sprintService = sprintService;
    }

    @ModelAttribute("priorities")
    public IssuePriority[] priorities() {
        return IssuePriority.values();
    }

    @ModelAttribute("projects")
    public List<ProjectResponse> projects() {
        return projectService.getProjectList(new ProjectSearchCondition());
    }

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
