package com.devtrace.manager.wbs.controller;

import com.devtrace.manager.common.response.ApiResponse;
import com.devtrace.manager.wbs.dto.WbsGanttResponse;
import com.devtrace.manager.wbs.dto.WbsTaskDependencyRequest;
import com.devtrace.manager.wbs.dto.WbsTaskDependencyResponse;
import com.devtrace.manager.wbs.dto.WbsTaskDependencySearchCondition;
import com.devtrace.manager.wbs.dto.WbsTaskRequest;
import com.devtrace.manager.wbs.dto.WbsTaskResponse;
import com.devtrace.manager.wbs.dto.WbsTaskSearchCondition;
import com.devtrace.manager.wbs.service.WbsService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * WBS 작업과 Gantt 조회 기능을 제공하는 REST 컨트롤러입니다.
 *
 * <p>프로젝트 계획 데이터를 외부 클라이언트에서 사용할 수 있도록 작업 CRUD,
 * 선후행 의존성 관리, Gantt 조회 API를 제공합니다.</p>
 */
@RestController
@RequestMapping("/api/v1/wbs")
public class WbsApiController {

    private final WbsService wbsService;

    /**
     * WBS API 컨트롤러를 생성합니다.
     *
     * @param wbsService WBS 서비스
     */
    public WbsApiController(WbsService wbsService) {
        this.wbsService = wbsService;
    }

    /**
     * WBS 작업 목록을 조회합니다.
     *
     * @param condition WBS 작업 검색 조건
     * @return WBS 작업 목록 API 응답
     */
    @GetMapping("/tasks")
    public ApiResponse<List<WbsTaskResponse>> taskList(WbsTaskSearchCondition condition) {
        return ApiResponse.success(wbsService.selectWbsTaskList(condition));
    }

    /**
     * WBS 작업 상세 정보를 조회합니다.
     *
     * @param wbsTaskId WBS 작업 ID
     * @return WBS 작업 상세 API 응답
     */
    @GetMapping("/tasks/{wbsTaskId}")
    public ApiResponse<WbsTaskResponse> taskDetails(@PathVariable UUID wbsTaskId) {
        return ApiResponse.success(wbsService.selectWbsTaskDetails(wbsTaskId));
    }

    /**
     * WBS 작업을 등록합니다.
     *
     * @param request 등록 요청
     * @return 등록된 WBS 작업 API 응답
     */
    @PostMapping("/tasks")
    public ApiResponse<WbsTaskResponse> createTask(@Valid @RequestBody WbsTaskRequest request) {
        return ApiResponse.success("WBS 작업이 등록되었습니다.", wbsService.insertWbsTask(request));
    }

    /**
     * WBS 작업을 수정합니다.
     *
     * @param wbsTaskId WBS 작업 ID
     * @param request 수정 요청
     * @return 수정된 WBS 작업 API 응답
     */
    @PutMapping("/tasks/{wbsTaskId}")
    public ApiResponse<WbsTaskResponse> updateTask(@PathVariable UUID wbsTaskId, @Valid @RequestBody WbsTaskRequest request) {
        return ApiResponse.success("WBS 작업이 수정되었습니다.", wbsService.updateWbsTask(wbsTaskId, request));
    }

    /**
     * WBS 작업을 삭제합니다.
     *
     * @param wbsTaskId WBS 작업 ID
     * @return 삭제 결과 API 응답
     */
    @DeleteMapping("/tasks/{wbsTaskId}")
    public ApiResponse<Void> deleteTask(@PathVariable UUID wbsTaskId) {
        wbsService.deleteWbsTask(wbsTaskId);
        return ApiResponse.success("WBS 작업이 삭제되었습니다.", null);
    }

    /**
     * WBS 작업 의존성 목록을 조회합니다.
     *
     * @param condition 의존성 검색 조건
     * @return 의존성 목록 API 응답
     */
    @GetMapping("/dependencies")
    public ApiResponse<List<WbsTaskDependencyResponse>> dependencyList(WbsTaskDependencySearchCondition condition) {
        return ApiResponse.success(wbsService.selectWbsTaskDependencyList(condition));
    }

    /**
     * WBS 작업 의존성을 등록합니다.
     *
     * @param request 의존성 등록 요청
     * @return 등록된 의존성 API 응답
     */
    @PostMapping("/dependencies")
    public ApiResponse<WbsTaskDependencyResponse> createDependency(@Valid @RequestBody WbsTaskDependencyRequest request) {
        return ApiResponse.success("WBS 선후행 작업이 등록되었습니다.", wbsService.insertWbsTaskDependency(request));
    }

    /**
     * WBS 작업 의존성을 삭제합니다.
     *
     * @param dependencyId 의존성 ID
     * @return 삭제 결과 API 응답
     */
    @DeleteMapping("/dependencies/{dependencyId}")
    public ApiResponse<Void> deleteDependency(@PathVariable UUID dependencyId) {
        wbsService.deleteWbsTaskDependency(dependencyId);
        return ApiResponse.success("WBS 선후행 작업이 삭제되었습니다.", null);
    }

    /**
     * 프로젝트별 WBS Gantt 조회 데이터를 반환합니다.
     *
     * @param projectId 프로젝트 ID
     * @return WBS Gantt API 응답
     */
    @GetMapping("/gantt")
    public ApiResponse<WbsGanttResponse> gantt(@RequestParam UUID projectId) {
        return ApiResponse.success(wbsService.selectWbsGanttDetails(projectId));
    }
}
