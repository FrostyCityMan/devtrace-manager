package com.devtrace.manager.project.controller;

import com.devtrace.manager.common.response.ApiResponse;
import com.devtrace.manager.project.dto.ProjectRequest;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.service.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectApiController {

    private final ProjectService projectService;

    public ProjectApiController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ApiResponse<List<ProjectResponse>> list(ProjectSearchCondition condition) {
        return ApiResponse.success(projectService.getProjectList(condition));
    }

    @GetMapping("/{projectId}")
    public ApiResponse<ProjectResponse> detail(@PathVariable UUID projectId) {
        return ApiResponse.success(projectService.getProject(projectId));
    }

    @PostMapping
    public ApiResponse<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        return ApiResponse.success("프로젝트가 등록되었습니다.", projectService.createProject(request));
    }

    @PutMapping("/{projectId}")
    public ApiResponse<ProjectResponse> update(
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectRequest request
    ) {
        return ApiResponse.success("프로젝트가 수정되었습니다.", projectService.updateProject(projectId, request));
    }

    @DeleteMapping("/{projectId}")
    public ApiResponse<Void> delete(@PathVariable UUID projectId) {
        projectService.deleteProject(projectId);
        return ApiResponse.success("프로젝트가 삭제되었습니다.", null);
    }
}
