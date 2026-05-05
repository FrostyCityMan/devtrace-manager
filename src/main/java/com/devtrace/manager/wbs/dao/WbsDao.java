package com.devtrace.manager.wbs.dao;

import com.devtrace.manager.wbs.dto.WbsTaskDependencyEntity;
import com.devtrace.manager.wbs.dto.WbsTaskDependencySearchCondition;
import com.devtrace.manager.wbs.dto.WbsTaskEntity;
import com.devtrace.manager.wbs.dto.WbsTaskSearchCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.ibatis.annotations.Param;

public interface WbsDao {

    void insertWbsTask(WbsTaskEntity task);

    void updateWbsTask(WbsTaskEntity task);

    void deleteWbsTask(UUID wbsTaskId);

    Optional<WbsTaskEntity> selectWbsTaskDetails(UUID wbsTaskId);

    List<WbsTaskEntity> selectWbsTaskList(WbsTaskSearchCondition condition);

    int selectWbsTaskChildCount(
            @Param("projectId") UUID projectId,
            @Param("parentTaskId") UUID parentTaskId
    );

    int selectWbsTaskDirectChildCount(UUID wbsTaskId);

    void insertWbsTaskDependency(WbsTaskDependencyEntity dependency);

    void deleteWbsTaskDependency(UUID dependencyId);

    void deleteWbsTaskDependencyByTaskId(UUID wbsTaskId);

    List<WbsTaskDependencyEntity> selectWbsTaskDependencyList(WbsTaskDependencySearchCondition condition);
}
