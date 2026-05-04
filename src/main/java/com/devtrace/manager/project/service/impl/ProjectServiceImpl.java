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

@Service
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    private final ProjectDao projectDao;

    public ProjectServiceImpl(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

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

    @Override
    @Transactional
    public ProjectResponse updateProject(UUID projectId, ProjectRequest request) {
        ProjectEntity project = findProject(projectId);
        applyRequest(project, request);
        project.setUpdatedAt(DateTimeUtil.now());

        projectDao.updateProject(project);
        return project.toResponse();
    }

    @Override
    @Transactional
    public void deleteProject(UUID projectId) {
        ProjectEntity project = findProject(projectId);
        performDelete(project);
    }

    @Override
    public ProjectResponse getProject(UUID projectId) {
        return findProject(projectId).toResponse();
    }

    @Override
    public List<ProjectResponse> getProjectList(ProjectSearchCondition condition) {
        return projectDao.selectProjectList(condition).stream()
                .map(ProjectEntity::toResponse)
                .toList();
    }

    private ProjectEntity findProject(UUID projectId) {
        return projectDao.selectProjectById(projectId)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다.", "PROJECT_NOT_FOUND"));
    }

    private void applyRequest(ProjectEntity project, ProjectRequest request) {
        project.setProjectCode(request.getProjectCode());
        project.setProjectName(request.getProjectName());
        project.setClientName(request.getClientName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setStatus(request.getStatus());
    }

    private void performDelete(ProjectEntity project) {
        projectDao.deleteProject(project.getProjectId());
    }
}
