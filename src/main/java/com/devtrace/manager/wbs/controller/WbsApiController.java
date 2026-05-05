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

@RestController
@RequestMapping("/api/v1/wbs")
public class WbsApiController {

    private final WbsService wbsService;

    public WbsApiController(WbsService wbsService) {
        this.wbsService = wbsService;
    }

    @GetMapping("/tasks")
    public ApiResponse<List<WbsTaskResponse>> taskList(WbsTaskSearchCondition condition) {
        return ApiResponse.success(wbsService.selectWbsTaskList(condition));
    }

    @GetMapping("/tasks/{wbsTaskId}")
    public ApiResponse<WbsTaskResponse> taskDetails(@PathVariable UUID wbsTaskId) {
        return ApiResponse.success(wbsService.selectWbsTaskDetails(wbsTaskId));
    }

    @PostMapping("/tasks")
    public ApiResponse<WbsTaskResponse> createTask(@Valid @RequestBody WbsTaskRequest request) {
        return ApiResponse.success("WBS 작업이 등록되었습니다.", wbsService.insertWbsTask(request));
    }

    @PutMapping("/tasks/{wbsTaskId}")
    public ApiResponse<WbsTaskResponse> updateTask(@PathVariable UUID wbsTaskId, @Valid @RequestBody WbsTaskRequest request) {
        return ApiResponse.success("WBS 작업이 수정되었습니다.", wbsService.updateWbsTask(wbsTaskId, request));
    }

    @DeleteMapping("/tasks/{wbsTaskId}")
    public ApiResponse<Void> deleteTask(@PathVariable UUID wbsTaskId) {
        wbsService.deleteWbsTask(wbsTaskId);
        return ApiResponse.success("WBS 작업이 삭제되었습니다.", null);
    }

    @GetMapping("/dependencies")
    public ApiResponse<List<WbsTaskDependencyResponse>> dependencyList(WbsTaskDependencySearchCondition condition) {
        return ApiResponse.success(wbsService.selectWbsTaskDependencyList(condition));
    }

    @PostMapping("/dependencies")
    public ApiResponse<WbsTaskDependencyResponse> createDependency(@Valid @RequestBody WbsTaskDependencyRequest request) {
        return ApiResponse.success("WBS 선후행 작업이 등록되었습니다.", wbsService.insertWbsTaskDependency(request));
    }

    @DeleteMapping("/dependencies/{dependencyId}")
    public ApiResponse<Void> deleteDependency(@PathVariable UUID dependencyId) {
        wbsService.deleteWbsTaskDependency(dependencyId);
        return ApiResponse.success("WBS 선후행 작업이 삭제되었습니다.", null);
    }

    @GetMapping("/gantt")
    public ApiResponse<WbsGanttResponse> gantt(@RequestParam UUID projectId) {
        return ApiResponse.success(wbsService.selectWbsGanttDetails(projectId));
    }
}
