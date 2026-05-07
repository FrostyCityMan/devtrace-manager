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

/**
 * WBS 작업, 선후행 의존성, Gantt 조회를 담당하는 서비스 계약입니다.
 *
 * <p>WBS는 프로젝트 계획의 기준 데이터이며, Gantt 화면은 WBS 작업과 의존성을
 * 시간 축으로 해석한 조회 결과입니다.</p>
 */
public interface WbsService {

    /**
     * WBS 작업을 등록합니다.
     *
     * @param request WBS 작업 등록 요청
     * @return 등록된 WBS 작업
     */
    WbsTaskResponse insertWbsTask(WbsTaskRequest request);

    /**
     * WBS 작업을 수정합니다.
     *
     * @param wbsTaskId WBS 작업 ID
     * @param request WBS 작업 수정 요청
     * @return 수정된 WBS 작업
     */
    WbsTaskResponse updateWbsTask(UUID wbsTaskId, WbsTaskRequest request);

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
     * @return WBS 작업 상세 정보
     */
    WbsTaskResponse selectWbsTaskDetails(UUID wbsTaskId);

    /**
     * 프로젝트와 상태 조건에 맞는 WBS 작업 목록을 조회합니다.
     *
     * @param condition WBS 작업 검색 조건
     * @return WBS 작업 목록
     */
    List<WbsTaskResponse> selectWbsTaskList(WbsTaskSearchCondition condition);

    /**
     * WBS 작업 간 선후행 의존성을 등록합니다.
     *
     * @param request 의존성 등록 요청
     * @return 등록된 의존성 정보
     */
    WbsTaskDependencyResponse insertWbsTaskDependency(WbsTaskDependencyRequest request);

    /**
     * WBS 작업 의존성을 삭제합니다.
     *
     * @param dependencyId 의존성 ID
     */
    void deleteWbsTaskDependency(UUID dependencyId);

    /**
     * WBS 작업 의존성 목록을 조회합니다.
     *
     * @param condition 의존성 검색 조건
     * @return 의존성 목록
     */
    List<WbsTaskDependencyResponse> selectWbsTaskDependencyList(WbsTaskDependencySearchCondition condition);

    /**
     * 프로젝트별 WBS Gantt 조회 데이터를 생성합니다.
     *
     * @param projectId 프로젝트 ID
     * @return Gantt 화면 표시용 WBS 응답
     */
    WbsGanttResponse selectWbsGanttDetails(UUID projectId);
}
