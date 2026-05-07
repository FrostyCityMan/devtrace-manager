package com.devtrace.manager.wbs.dao;

import com.devtrace.manager.wbs.dto.WbsTaskDependencyEntity;
import com.devtrace.manager.wbs.dto.WbsTaskDependencySearchCondition;
import com.devtrace.manager.wbs.dto.WbsTaskEntity;
import com.devtrace.manager.wbs.dto.WbsTaskSearchCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.ibatis.annotations.Param;

/**
 * WBS 작업과 선후행 의존성 저장소에 접근하는 MyBatis DAO입니다.
 *
 * <p>계층 코드 생성에 필요한 하위 작업 수 조회와 의존성 정리용 삭제 SQL을 포함합니다.</p>
 */
public interface WbsDao {

    /**
     * WBS 작업을 등록합니다.
     *
     * @param task 저장할 WBS 작업 엔티티
     */
    void insertWbsTask(WbsTaskEntity task);

    /**
     * WBS 작업을 수정합니다.
     *
     * @param task 수정할 WBS 작업 엔티티
     */
    void updateWbsTask(WbsTaskEntity task);

    /**
     * WBS 작업을 삭제합니다.
     *
     * @param wbsTaskId WBS 작업 ID
     */
    void deleteWbsTask(UUID wbsTaskId);

    /**
     * WBS 작업 상세 정보를 조회합니다.
     *
     * @param wbsTaskId WBS 작업 ID
     * @return WBS 작업 엔티티
     */
    Optional<WbsTaskEntity> selectWbsTaskDetails(UUID wbsTaskId);

    /**
     * WBS 작업 목록을 검색합니다.
     *
     * @param condition 프로젝트, 상태, 키워드 조건
     * @return WBS 작업 엔티티 목록
     */
    List<WbsTaskEntity> selectWbsTaskList(WbsTaskSearchCondition condition);

    /**
     * 지정 계층의 하위 작업 수를 조회합니다.
     *
     * @param projectId 프로젝트 ID
     * @param parentTaskId 상위 작업 ID
     * @return 하위 작업 수
     */
    int selectWbsTaskChildCount(
            @Param("projectId") UUID projectId,
            @Param("parentTaskId") UUID parentTaskId
    );

    /**
     * 지정 작업의 직접 하위 작업 수를 조회합니다.
     *
     * @param wbsTaskId WBS 작업 ID
     * @return 직접 하위 작업 수
     */
    int selectWbsTaskDirectChildCount(UUID wbsTaskId);

    /**
     * WBS 작업 의존성을 등록합니다.
     *
     * @param dependency 저장할 의존성 엔티티
     */
    void insertWbsTaskDependency(WbsTaskDependencyEntity dependency);

    /**
     * WBS 작업 의존성을 삭제합니다.
     *
     * @param dependencyId 의존성 ID
     */
    void deleteWbsTaskDependency(UUID dependencyId);

    /**
     * 특정 작업과 연결된 모든 의존성을 삭제합니다.
     *
     * @param wbsTaskId WBS 작업 ID
     */
    void deleteWbsTaskDependencyByTaskId(UUID wbsTaskId);

    /**
     * WBS 작업 의존성 목록을 검색합니다.
     *
     * @param condition 프로젝트와 작업 조건
     * @return 의존성 엔티티 목록
     */
    List<WbsTaskDependencyEntity> selectWbsTaskDependencyList(WbsTaskDependencySearchCondition condition);
}
