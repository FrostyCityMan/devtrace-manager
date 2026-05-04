package com.devtrace.manager.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devtrace.manager.project.dao.ProjectDao;
import com.devtrace.manager.project.dto.ProjectEntity;
import com.devtrace.manager.project.dto.ProjectRequest;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.dto.ProjectStatus;
import com.devtrace.manager.project.service.impl.ProjectServiceImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectDao projectDao;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectServiceImpl(projectDao);
    }

    @Test
    void createProject() {
        ProjectRequest request = createRequest("DTR-001", "DevTrace Manager");

        ProjectResponse response = projectService.createProject(request);

        ArgumentCaptor<ProjectEntity> captor = ArgumentCaptor.forClass(ProjectEntity.class);
        verify(projectDao).insertProject(captor.capture());
        ProjectEntity saved = captor.getValue();

        assertThat(saved.getProjectId()).isNotNull();
        assertThat(saved.getProjectCode()).isEqualTo("DTR-001");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(response.getProjectCode()).isEqualTo("DTR-001");
    }

    @Test
    void updateProject() {
        UUID projectId = UUID.randomUUID();
        ProjectEntity project = createEntity(projectId);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(project));

        ProjectRequest request = createRequest("DTR-002", "Updated Project");
        ProjectResponse response = projectService.updateProject(projectId, request);

        verify(projectDao).updateProject(project);
        assertThat(response.getProjectCode()).isEqualTo("DTR-002");
        assertThat(response.getProjectName()).isEqualTo("Updated Project");
        assertThat(response.getUpdatedAt()).isNotNull();
    }

    @Test
    void deleteProject() {
        UUID projectId = UUID.randomUUID();
        ProjectEntity project = createEntity(projectId);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(project));

        projectService.deleteProject(projectId);

        verify(projectDao).deleteProject(projectId);
    }

    @Test
    void getProject() {
        UUID projectId = UUID.randomUUID();
        ProjectEntity project = createEntity(projectId);
        when(projectDao.selectProjectById(projectId)).thenReturn(Optional.of(project));

        ProjectResponse response = projectService.getProject(projectId);

        assertThat(response.getProjectId()).isEqualTo(projectId);
        assertThat(response.getProjectCode()).isEqualTo("DTR-001");
    }

    @Test
    void getProjectList() {
        ProjectEntity project = createEntity(UUID.randomUUID());
        when(projectDao.selectProjectList(any(ProjectSearchCondition.class))).thenReturn(List.of(project));

        List<ProjectResponse> projects = projectService.getProjectList(new ProjectSearchCondition());

        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getProjectCode()).isEqualTo("DTR-001");
    }

    private ProjectRequest createRequest(String projectCode, String projectName) {
        ProjectRequest request = new ProjectRequest();
        request.setProjectCode(projectCode);
        request.setProjectName(projectName);
        request.setClientName("고객사");
        request.setDescription("설명");
        request.setStartDate(LocalDate.of(2026, 5, 1));
        request.setEndDate(LocalDate.of(2026, 12, 31));
        request.setStatus(ProjectStatus.READY);
        return request;
    }

    private ProjectEntity createEntity(UUID projectId) {
        ProjectEntity project = new ProjectEntity();
        project.setProjectId(projectId);
        project.setProjectCode("DTR-001");
        project.setProjectName("DevTrace Manager");
        project.setClientName("고객사");
        project.setDescription("설명");
        project.setStartDate(LocalDate.of(2026, 5, 1));
        project.setEndDate(LocalDate.of(2026, 12, 31));
        project.setStatus(ProjectStatus.READY);
        project.setCreatedAt(LocalDateTime.of(2026, 5, 3, 12, 0));
        return project;
    }
}
