package com.devtrace.manager.wbs.service;

import com.devtrace.manager.wbs.dto.WbsGanttResponse;
import com.devtrace.manager.wbs.dto.WbsTaskDependencyRequest;
import com.devtrace.manager.wbs.dto.WbsTaskDependencyResponse;
import com.devtrace.manager.wbs.dto.WbsTaskDependencySearchCondition;
import com.devtrace.manager.wbs.dto.WbsTaskRequest;
import com.devtrace.manager.wbs.dto.WbsTaskResponse;
import com.devtrace.manager.wbs.dto.WbsTaskSearchCondition;
import java.util.List;
import java.util.UUID;

public interface WbsService {

    WbsTaskResponse insertWbsTask(WbsTaskRequest request);

    WbsTaskResponse updateWbsTask(UUID wbsTaskId, WbsTaskRequest request);

    void deleteWbsTask(UUID wbsTaskId);

    WbsTaskResponse selectWbsTaskDetails(UUID wbsTaskId);

    List<WbsTaskResponse> selectWbsTaskList(WbsTaskSearchCondition condition);

    WbsTaskDependencyResponse insertWbsTaskDependency(WbsTaskDependencyRequest request);

    void deleteWbsTaskDependency(UUID dependencyId);

    List<WbsTaskDependencyResponse> selectWbsTaskDependencyList(WbsTaskDependencySearchCondition condition);

    WbsGanttResponse selectWbsGanttDetails(UUID projectId);
}
