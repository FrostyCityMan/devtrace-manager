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

/**
 * 프로젝트 관리 기능을 제공하는 REST 컨트롤러입니다.
 *
 * <p>프로젝트 CRUD 기능을 공통 API 응답 형식으로 노출합니다.</p>
 */
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectApiController {

    private final ProjectService projectService;

    /**
     * 프로젝트 REST API 컨트롤러를 생성한다.
     *
     * @param projectService 프로젝트 업무 서비스
     */
    public ProjectApiController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * 프로젝트 목록을 조회한다.
     *
     * @param condition 프로젝트 검색 조건
     * @return 공통 API 응답으로 감싼 프로젝트 목록
     */
    @GetMapping
    public ApiResponse<List<ProjectResponse>> list(ProjectSearchCondition condition) {
        return ApiResponse.success(projectService.getProjectList(condition));
    }

    /**
     * 프로젝트 상세 정보를 조회한다.
     *
     * @param projectId 조회 대상 프로젝트 ID
     * @return 공통 API 응답으로 감싼 프로젝트 상세
     */
    @GetMapping("/{projectId}")
    public ApiResponse<ProjectResponse> detail(@PathVariable UUID projectId) {
        return ApiResponse.success(projectService.getProject(projectId));
    }

    /**
     * 프로젝트를 등록한다.
     *
     * @param request 프로젝트 등록 요청
     * @return 등록된 프로젝트 응답
     */
    @PostMapping
    public ApiResponse<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        return ApiResponse.success("프로젝트가 등록되었습니다.", projectService.createProject(request));
    }

    /**
     * 프로젝트를 수정한다.
     *
     * @param projectId 수정 대상 프로젝트 ID
     * @param request 프로젝트 수정 요청
     * @return 수정된 프로젝트 응답
     */
    @PutMapping("/{projectId}")
    public ApiResponse<ProjectResponse> update(
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectRequest request
    ) {
        return ApiResponse.success("프로젝트가 수정되었습니다.", projectService.updateProject(projectId, request));
    }

    /**
     * 프로젝트를 삭제한다.
     *
     * @param projectId 삭제 대상 프로젝트 ID
     * @return 처리 결과 응답
     */
    @DeleteMapping("/{projectId}")
    public ApiResponse<Void> delete(@PathVariable UUID projectId) {
        projectService.deleteProject(projectId);
        return ApiResponse.success("프로젝트가 삭제되었습니다.", null);
    }
}
