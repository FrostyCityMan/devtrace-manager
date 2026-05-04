package com.devtrace.manager.project.service;

import com.devtrace.manager.project.dto.ProjectRequest;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import java.util.List;
import java.util.UUID;

public interface ProjectService {

    ProjectResponse createProject(ProjectRequest request);

    ProjectResponse updateProject(UUID projectId, ProjectRequest request);

    void deleteProject(UUID projectId);

    ProjectResponse getProject(UUID projectId);

    List<ProjectResponse> getProjectList(ProjectSearchCondition condition);
}
