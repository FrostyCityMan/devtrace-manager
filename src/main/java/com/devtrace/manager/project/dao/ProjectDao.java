package com.devtrace.manager.project.dao;

import com.devtrace.manager.project.dto.ProjectEntity;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 프로젝트 저장소에 접근하는 MyBatis DAO입니다.
 *
 * <p>프로젝트 SQL 호출만 담당하며, 중복 검증과 업무 판단은 서비스 계층에서 수행합니다.</p>
 */
public interface ProjectDao {

    /**
     * 프로젝트를 저장한다.
     *
     * @param project 저장할 프로젝트 엔티티
     */
    void insertProject(ProjectEntity project);

    /**
     * 프로젝트를 수정한다.
     *
     * @param project 수정할 프로젝트 엔티티
     */
    void updateProject(ProjectEntity project);

    /**
     * 프로젝트를 삭제한다.
     *
     * @param projectId 삭제 대상 프로젝트 ID
     */
    void deleteProject(UUID projectId);

    /**
     * 프로젝트 ID로 단건을 조회한다.
     *
     * @param projectId 조회 대상 프로젝트 ID
     * @return 프로젝트 엔티티
     */
    Optional<ProjectEntity> selectProjectById(UUID projectId);

    /**
     * 검색 조건에 맞는 프로젝트 목록을 조회한다.
     *
     * @param condition 검색어와 상태 필터
     * @return 프로젝트 엔티티 목록
     */
    List<ProjectEntity> selectProjectList(ProjectSearchCondition condition);
}
