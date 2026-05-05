package com.devtrace.manager.wbs.service.impl;

import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.common.util.DateTimeUtil;
import com.devtrace.manager.issue.dao.IssueDao;
import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.project.dao.ProjectDao;
import com.devtrace.manager.wbs.dao.WbsDao;
import com.devtrace.manager.wbs.dto.WbsDependencyType;
import com.devtrace.manager.wbs.dto.WbsGanttResponse;
import com.devtrace.manager.wbs.dto.WbsGanttTaskResponse;
import com.devtrace.manager.wbs.dto.WbsTaskDependencyEntity;
import com.devtrace.manager.wbs.dto.WbsTaskDependencyRequest;
import com.devtrace.manager.wbs.dto.WbsTaskDependencyResponse;
import com.devtrace.manager.wbs.dto.WbsTaskDependencySearchCondition;
import com.devtrace.manager.wbs.dto.WbsTaskEntity;
import com.devtrace.manager.wbs.dto.WbsTaskRequest;
import com.devtrace.manager.wbs.dto.WbsTaskResponse;
import com.devtrace.manager.wbs.dto.WbsTaskSearchCondition;
import com.devtrace.manager.wbs.dto.WbsTaskStatus;
import com.devtrace.manager.wbs.service.WbsService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WbsServiceImpl implements WbsService {

    private final WbsDao wbsDao;
    private final ProjectDao projectDao;
    private final IssueDao issueDao;

    public WbsServiceImpl(WbsDao wbsDao, ProjectDao projectDao, IssueDao issueDao) {
        this.wbsDao = wbsDao;
        this.projectDao = projectDao;
        this.issueDao = issueDao;
    }

    @Override
    @Transactional
    public WbsTaskResponse insertWbsTask(WbsTaskRequest request) {
        validateTaskRequest(request);
        WbsTaskEntity parentTask = selectParentTask(request);
        IssueEntity issue = selectIssue(request);
        int displayOrder = wbsDao.selectWbsTaskChildCount(request.getProjectId(), request.getParentTaskId()) + 1;

        WbsTaskEntity task = new WbsTaskEntity();
        task.setWbsTaskId(UUID.randomUUID());
        task.setParentTaskId(request.getParentTaskId());
        task.setWbsCode(createWbsCode(parentTask, displayOrder));
        task.setDisplayOrder(displayOrder);
        task.setCreatedAt(DateTimeUtil.now());
        applyRequest(task, request, issue);

        wbsDao.insertWbsTask(task);
        return task.toResponse();
    }

    @Override
    @Transactional
    public WbsTaskResponse updateWbsTask(UUID wbsTaskId, WbsTaskRequest request) {
        validateTaskRequest(request);
        WbsTaskEntity task = selectWbsTaskEntity(wbsTaskId);
        if (!task.getProjectId().equals(request.getProjectId())) {
            throw new BusinessException("WBS 작업의 프로젝트는 변경할 수 없습니다.", "WBS_PROJECT_CHANGE_NOT_SUPPORTED");
        }
        IssueEntity issue = selectIssue(request);
        applyRequest(task, request, issue);
        task.setUpdatedAt(DateTimeUtil.now());

        wbsDao.updateWbsTask(task);
        return task.toResponse();
    }

    @Override
    @Transactional
    public void deleteWbsTask(UUID wbsTaskId) {
        WbsTaskEntity task = selectWbsTaskEntity(wbsTaskId);
        if (wbsDao.selectWbsTaskDirectChildCount(wbsTaskId) > 0) {
            throw new BusinessException("하위 WBS 작업이 있어 삭제할 수 없습니다.", "WBS_TASK_HAS_CHILDREN");
        }
        wbsDao.deleteWbsTaskDependencyByTaskId(task.getWbsTaskId());
        wbsDao.deleteWbsTask(task.getWbsTaskId());
    }

    @Override
    public WbsTaskResponse selectWbsTaskDetails(UUID wbsTaskId) {
        return selectWbsTaskEntity(wbsTaskId).toResponse();
    }

    @Override
    public List<WbsTaskResponse> selectWbsTaskList(WbsTaskSearchCondition condition) {
        WbsTaskSearchCondition safeCondition = condition == null ? new WbsTaskSearchCondition() : condition;
        return wbsDao.selectWbsTaskList(safeCondition).stream()
                .map(WbsTaskEntity::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public WbsTaskDependencyResponse insertWbsTaskDependency(WbsTaskDependencyRequest request) {
        validateDependencyRequest(request);
        WbsTaskEntity predecessor = selectWbsTaskEntity(request.getPredecessorTaskId());
        WbsTaskEntity successor = selectWbsTaskEntity(request.getSuccessorTaskId());
        validateDependencyTaskProject(request.getProjectId(), predecessor, successor);

        WbsTaskDependencyEntity dependency = new WbsTaskDependencyEntity();
        dependency.setDependencyId(UUID.randomUUID());
        dependency.setProjectId(request.getProjectId());
        dependency.setPredecessorTaskId(request.getPredecessorTaskId());
        dependency.setSuccessorTaskId(request.getSuccessorTaskId());
        dependency.setDependencyType(WbsDependencyType.FINISH_TO_START);
        dependency.setLagDays(request.getLagDays() == null ? 0 : request.getLagDays());
        dependency.setCreatedAt(DateTimeUtil.now());
        wbsDao.insertWbsTaskDependency(dependency);
        return dependency.toResponse();
    }

    @Override
    @Transactional
    public void deleteWbsTaskDependency(UUID dependencyId) {
        wbsDao.deleteWbsTaskDependency(dependencyId);
    }

    @Override
    public List<WbsTaskDependencyResponse> selectWbsTaskDependencyList(WbsTaskDependencySearchCondition condition) {
        WbsTaskDependencySearchCondition safeCondition = condition == null ? new WbsTaskDependencySearchCondition() : condition;
        return wbsDao.selectWbsTaskDependencyList(safeCondition).stream()
                .map(WbsTaskDependencyEntity::toResponse)
                .toList();
    }

    @Override
    public WbsGanttResponse selectWbsGanttDetails(UUID projectId) {
        validateProject(projectId);
        WbsTaskSearchCondition taskCondition = new WbsTaskSearchCondition();
        taskCondition.setProjectId(projectId);
        List<WbsGanttTaskResponse> tasks = selectWbsTaskList(taskCondition).stream()
                .map(WbsGanttTaskResponse::from)
                .collect(Collectors.toList());

        WbsTaskDependencySearchCondition dependencyCondition = new WbsTaskDependencySearchCondition();
        dependencyCondition.setProjectId(projectId);
        List<WbsTaskDependencyResponse> dependencies = selectWbsTaskDependencyList(dependencyCondition);
        applyParentAggregate(tasks);
        applyGanttRisk(tasks, dependencies);
        applyGanttTimeline(projectId, tasks);
        return createGanttResponse(projectId, tasks);
    }

    private void validateTaskRequest(WbsTaskRequest request) {
        if (request == null || request.getProjectId() == null) {
            throw new BusinessException("WBS 작업 요청이 올바르지 않습니다.", "WBS_TASK_REQUEST_INVALID");
        }
        validateProject(request.getProjectId());
        if (request.getPlanStartDate() != null && request.getPlanEndDate() != null && request.getPlanStartDate().isAfter(request.getPlanEndDate())) {
            throw new BusinessException("계획 시작일은 종료일보다 늦을 수 없습니다.", "WBS_PLAN_DATE_INVALID");
        }
    }

    private void validateProject(UUID projectId) {
        projectDao.selectProjectById(projectId)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다.", "PROJECT_NOT_FOUND"));
    }

    private WbsTaskEntity selectParentTask(WbsTaskRequest request) {
        if (request.getParentTaskId() == null) {
            return null;
        }
        WbsTaskEntity parentTask = selectWbsTaskEntity(request.getParentTaskId());
        if (!request.getProjectId().equals(parentTask.getProjectId())) {
            throw new BusinessException("상위 WBS 작업이 선택한 프로젝트에 속하지 않습니다.", "WBS_PARENT_PROJECT_MISMATCH");
        }
        return parentTask;
    }

    private IssueEntity selectIssue(WbsTaskRequest request) {
        if (request.getIssueId() == null) {
            return null;
        }
        IssueEntity issue = issueDao.selectIssueByIdDetails(request.getIssueId())
                .orElseThrow(() -> new BusinessException("이슈를 찾을 수 없습니다.", "ISSUE_NOT_FOUND"));
        if (!request.getProjectId().equals(issue.getProjectId())) {
            throw new BusinessException("이슈가 선택한 프로젝트에 속하지 않습니다.", "WBS_ISSUE_PROJECT_MISMATCH");
        }
        return issue;
    }

    private WbsTaskEntity selectWbsTaskEntity(UUID wbsTaskId) {
        return wbsDao.selectWbsTaskDetails(wbsTaskId)
                .orElseThrow(() -> new BusinessException("WBS 작업을 찾을 수 없습니다.", "WBS_TASK_NOT_FOUND"));
    }

    private String createWbsCode(WbsTaskEntity parentTask, int displayOrder) {
        if (parentTask == null) {
            return String.valueOf(displayOrder);
        }
        return parentTask.getWbsCode() + "." + displayOrder;
    }

    private void applyRequest(WbsTaskEntity task, WbsTaskRequest request, IssueEntity issue) {
        task.setProjectId(request.getProjectId());
        task.setIssueId(request.getIssueId());
        task.setTaskName(request.getTaskName());
        task.setTaskDescription(request.getTaskDescription());
        task.setTaskType(request.getTaskType());
        task.setStatus(request.getStatus());
        task.setAssigneeId(request.getAssigneeId());
        task.setPlanStartDate(request.getPlanStartDate());
        task.setPlanEndDate(request.getPlanEndDate());
        task.setActualStartDate(request.getActualStartDate());
        task.setActualEndDate(request.getActualEndDate());
        task.setEstimatedMinutes(valueOrZero(request.getEstimatedMinutes()));
        task.setSpentMinutes(issue == null ? valueOrZero(request.getSpentMinutes()) : valueOrZero(issue.getSpentMinutes()));
        task.setProgressRate(valueOrZero(request.getProgressRate()));
    }

    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private void validateDependencyRequest(WbsTaskDependencyRequest request) {
        if (request == null || request.getProjectId() == null || request.getPredecessorTaskId() == null || request.getSuccessorTaskId() == null) {
            throw new BusinessException("WBS 의존성 요청이 올바르지 않습니다.", "WBS_DEPENDENCY_REQUEST_INVALID");
        }
        if (request.getPredecessorTaskId().equals(request.getSuccessorTaskId())) {
            throw new BusinessException("동일한 작업은 선후행 관계로 등록할 수 없습니다.", "WBS_DEPENDENCY_SELF_NOT_ALLOWED");
        }
        if (request.getDependencyType() != null && request.getDependencyType() != WbsDependencyType.FINISH_TO_START) {
            throw new BusinessException("지원하지 않는 WBS 의존성 유형입니다.", "WBS_DEPENDENCY_TYPE_NOT_SUPPORTED");
        }
        validateProject(request.getProjectId());
    }

    private void validateDependencyTaskProject(UUID projectId, WbsTaskEntity predecessor, WbsTaskEntity successor) {
        if (!projectId.equals(predecessor.getProjectId()) || !projectId.equals(successor.getProjectId())) {
            throw new BusinessException("선후행 작업은 동일 프로젝트에 속해야 합니다.", "WBS_DEPENDENCY_PROJECT_MISMATCH");
        }
    }

    private void applyParentAggregate(List<WbsGanttTaskResponse> tasks) {
        Map<UUID, List<WbsGanttTaskResponse>> childrenByParentId = tasks.stream()
                .filter(task -> task.getParentTaskId() != null)
                .collect(Collectors.groupingBy(WbsGanttTaskResponse::getParentTaskId));
        Map<UUID, WbsGanttTaskResponse> taskById = tasks.stream()
                .collect(Collectors.toMap(WbsGanttTaskResponse::getWbsTaskId, Function.identity()));

        tasks.stream()
                .sorted(Comparator.comparingInt(WbsGanttTaskResponse::getDepth).reversed())
                .forEach(task -> {
                    List<WbsGanttTaskResponse> children = childrenByParentId.get(task.getWbsTaskId());
                    if (children == null || children.isEmpty()) {
                        return;
                    }
                    task.setPlanStartDate(children.stream().map(WbsGanttTaskResponse::getPlanStartDate).filter(Objects::nonNull).min(LocalDate::compareTo).orElse(task.getPlanStartDate()));
                    task.setPlanEndDate(children.stream().map(WbsGanttTaskResponse::getPlanEndDate).filter(Objects::nonNull).max(LocalDate::compareTo).orElse(task.getPlanEndDate()));
                    task.setEstimatedMinutes(children.stream().mapToInt(child -> valueOrZero(child.getEstimatedMinutes())).sum());
                    task.setSpentMinutes(children.stream().mapToInt(child -> valueOrZero(child.getSpentMinutes())).sum());
                    task.setProgressRate(selectWeightedProgress(children));
                    if (task.getParentTaskId() != null && taskById.containsKey(task.getParentTaskId())) {
                        taskById.get(task.getParentTaskId()).setEstimatedMinutes(valueOrZero(taskById.get(task.getParentTaskId()).getEstimatedMinutes()));
                    }
                });
    }

    private int selectWeightedProgress(List<WbsGanttTaskResponse> children) {
        int totalEstimated = children.stream().mapToInt(child -> valueOrZero(child.getEstimatedMinutes())).sum();
        if (totalEstimated > 0) {
            int weighted = children.stream()
                    .mapToInt(child -> valueOrZero(child.getProgressRate()) * valueOrZero(child.getEstimatedMinutes()))
                    .sum();
            return Math.round((float) weighted / totalEstimated);
        }
        return Math.round((float) children.stream().mapToInt(child -> valueOrZero(child.getProgressRate())).sum() / children.size());
    }

    private void applyGanttRisk(List<WbsGanttTaskResponse> tasks, List<WbsTaskDependencyResponse> dependencies) {
        LocalDate today = LocalDate.now();
        Map<UUID, WbsTaskStatus> predecessorStatusBySuccessorId = dependencies.stream()
                .filter(dependency -> dependency.getSuccessorTaskId() != null)
                .collect(Collectors.toMap(
                        WbsTaskDependencyResponse::getSuccessorTaskId,
                        WbsTaskDependencyResponse::getPredecessorStatus,
                        (left, right) -> left != null && !left.isCompleted() ? left : right
                ));

        for (WbsGanttTaskResponse task : tasks) {
            boolean completed = task.getStatus() != null && task.getStatus().isCompleted();
            task.setDelayed(!completed && task.getPlanEndDate() != null && task.getPlanEndDate().isBefore(today));
            task.setOverEffort(valueOrZero(task.getEstimatedMinutes()) > 0 && valueOrZero(task.getSpentMinutes()) > valueOrZero(task.getEstimatedMinutes()));
            WbsTaskStatus predecessorStatus = predecessorStatusBySuccessorId.get(task.getWbsTaskId());
            task.setPredecessorBlocked(predecessorStatus != null && !predecessorStatus.isCompleted());
        }
    }

    private void applyGanttTimeline(UUID projectId, List<WbsGanttTaskResponse> tasks) {
        WbsGanttResponse response = createGanttResponse(projectId, tasks);
        LocalDate startDate = response.getTimelineStartDate();
        LocalDate endDate = response.getTimelineEndDate();
        long totalDays = Math.max(1, ChronoUnit.DAYS.between(startDate, endDate) + 1);

        for (WbsGanttTaskResponse task : tasks) {
            long leftDays = Math.max(0, ChronoUnit.DAYS.between(startDate, task.getPlanStartDate()));
            long widthDays = Math.max(1, ChronoUnit.DAYS.between(task.getPlanStartDate(), task.getPlanEndDate()) + 1);
            task.setLeftPercent(leftDays * 100.0 / totalDays);
            task.setWidthPercent(Math.max(1.0, widthDays * 100.0 / totalDays));
        }
    }

    private WbsGanttResponse createGanttResponse(UUID projectId, List<WbsGanttTaskResponse> tasks) {
        LocalDate timelineStart = tasks.stream()
                .map(WbsGanttTaskResponse::getPlanStartDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
        LocalDate timelineEnd = tasks.stream()
                .map(WbsGanttTaskResponse::getPlanEndDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(timelineStart.plusDays(30));

        WbsGanttResponse response = new WbsGanttResponse();
        response.setProjectId(projectId);
        response.setTimelineStartDate(timelineStart);
        response.setTimelineEndDate(timelineEnd);
        response.setTasks(tasks);
        return response;
    }
}
