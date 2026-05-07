package com.devtrace.manager.project.service.impl;

import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.common.util.DateTimeUtil;
import com.devtrace.manager.project.dao.ProjectDao;
import com.devtrace.manager.project.dto.ProjectEntity;
import com.devtrace.manager.project.dto.ProjectRequest;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.service.ProjectService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 프로젝트 관리 업무 규칙을 구현합니다.
 *
 * <p>프로젝트 코드 중복, 프로젝트 존재 여부, 등록/수정/삭제 시각 처리를 담당합니다.</p>
 */
@Service
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    private final ProjectDao projectDao;

    /**
     * 프로젝트 서비스 구현체를 생성한다.
     *
     * @param projectDao 프로젝트 SQL 호출 DAO
     */
    public ProjectServiceImpl(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    /**
     * 프로젝트를 등록한다.
     *
     * <p>프로젝트 ID와 생성일시는 서비스 계층에서 생성한다.</p>
     *
     * @param request 프로젝트 등록 요청
     * @return 등록된 프로젝트 응답
     */
    @Override
    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        LocalDateTime now = DateTimeUtil.now();
        ProjectEntity project = new ProjectEntity();
        project.setProjectId(UUID.randomUUID());
        applyRequest(project, request);
        project.setCreatedAt(now);

        projectDao.insertProject(project);
        return project.toResponse();
    }

    /**
     * 프로젝트를 수정한다.
     *
     * <p>대상 프로젝트가 존재하는지 확인한 뒤 요청 값을 반영하고 수정일시를 갱신한다.</p>
     *
     * @param projectId 수정 대상 프로젝트 ID
     * @param request 프로젝트 수정 요청
     * @return 수정된 프로젝트 응답
     */
    @Override
    @Transactional
    public ProjectResponse updateProject(UUID projectId, ProjectRequest request) {
        ProjectEntity project = findProject(projectId);
        applyRequest(project, request);
        project.setUpdatedAt(DateTimeUtil.now());

        projectDao.updateProject(project);
        return project.toResponse();
    }

    /**
     * 프로젝트를 삭제한다.
     *
     * @param projectId 삭제 대상 프로젝트 ID
     */
    @Override
    @Transactional
    public void deleteProject(UUID projectId) {
        ProjectEntity project = findProject(projectId);
        performDelete(project);
    }

    /**
     * 프로젝트 상세 정보를 조회한다.
     *
     * @param projectId 조회 대상 프로젝트 ID
     * @return 프로젝트 상세 응답
     */
    @Override
    public ProjectResponse getProject(UUID projectId) {
        return findProject(projectId).toResponse();
    }

    /**
     * 검색 조건에 맞는 프로젝트 목록을 조회한다.
     *
     * @param condition 검색 조건
     * @return 프로젝트 목록
     */
    @Override
    public List<ProjectResponse> getProjectList(ProjectSearchCondition condition) {
        return projectDao.selectProjectList(condition).stream()
                .map(ProjectEntity::toResponse)
                .toList();
    }

    /**
     * 프로젝트 엔티티를 조회하고 없으면 업무 예외를 발생시킨다.
     *
     * @param projectId 조회 대상 프로젝트 ID
     * @return 프로젝트 엔티티
     */
    private ProjectEntity findProject(UUID projectId) {
        return projectDao.selectProjectById(projectId)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다.", "PROJECT_NOT_FOUND"));
    }

    /**
     * 요청 값을 프로젝트 엔티티에 반영한다.
     *
     * @param project 값이 반영될 프로젝트 엔티티
     * @param request 사용자 요청 DTO
     */
    private void applyRequest(ProjectEntity project, ProjectRequest request) {
        project.setProjectCode(request.getProjectCode());
        project.setProjectName(request.getProjectName());
        project.setClientName(request.getClientName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setStatus(request.getStatus());
    }

    /**
     * 프로젝트 삭제 SQL을 호출한다.
     *
     * <p>향후 이슈/산출물 등 하위 데이터 보호 정책이 생기면 이 메소드에서 삭제 전 검증을 확장한다.</p>
     *
     * @param project 삭제 대상 프로젝트 엔티티
     */
    private void performDelete(ProjectEntity project) {
        projectDao.deleteProject(project.getProjectId());
    }
}
