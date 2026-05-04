package com.devtrace.manager.project.dao;

import com.devtrace.manager.project.dto.ProjectEntity;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectDao {

    void insertProject(ProjectEntity project);

    void updateProject(ProjectEntity project);

    void deleteProject(UUID projectId);

    Optional<ProjectEntity> selectProjectById(UUID projectId);

    List<ProjectEntity> selectProjectList(ProjectSearchCondition condition);
}
