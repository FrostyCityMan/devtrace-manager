package com.devtrace.manager.project.service;

import com.devtrace.manager.project.dto.ProjectRequest;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import java.util.List;
import java.util.UUID;

/**
 * 프로젝트 관리 업무를 담당하는 서비스 계약입니다.
 *
 * <p>프로젝트는 DevTrace Manager의 모든 업무 데이터가 귀속되는 최상위 기준입니다.</p>
 */
public interface ProjectService {

    /**
     * 프로젝트를 등록한다.
     *
     * @param request 프로젝트 등록 요청
     * @return 등록된 프로젝트 응답
     */
    ProjectResponse createProject(ProjectRequest request);

    /**
     * 프로젝트를 수정한다.
     *
     * @param projectId 수정 대상 프로젝트 ID
     * @param request 프로젝트 수정 요청
     * @return 수정된 프로젝트 응답
     */
    ProjectResponse updateProject(UUID projectId, ProjectRequest request);

    /**
     * 프로젝트를 삭제한다.
     *
     * @param projectId 삭제 대상 프로젝트 ID
     */
    void deleteProject(UUID projectId);

    /**
     * 프로젝트 상세 정보를 조회한다.
     *
     * @param projectId 조회 대상 프로젝트 ID
     * @return 프로젝트 상세 응답
     */
    ProjectResponse getProject(UUID projectId);

    /**
     * 검색 조건에 맞는 프로젝트 목록을 조회한다.
     *
     * @param condition 검색어와 상태 필터
     * @return 프로젝트 목록
     */
    List<ProjectResponse> getProjectList(ProjectSearchCondition condition);
}
