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

/**
 * WBS 작업과 Gantt 조회 업무 규칙을 구현합니다.
 *
 * <p>작업 등록 시 WBS 코드를 계층 기준으로 생성하고, 이슈 연결 시 실제 공수는
 * 이슈의 누적 공수를 기준으로 반영합니다. Gantt 조회에서는 부모 작업 집계,
 * 지연/공수초과/선행작업 미완료 위험, 타임라인 표시 비율을 계산합니다.</p>
 */
@Service
@Transactional(readOnly = true)
public class WbsServiceImpl implements WbsService {

    private final WbsDao wbsDao;
    private final ProjectDao projectDao;
    private final IssueDao issueDao;

    /**
     * WBS 서비스 구현체를 생성합니다.
     *
     * @param wbsDao WBS DAO
     * @param projectDao 프로젝트 검증 DAO
     * @param issueDao 이슈 검증 DAO
     */
    public WbsServiceImpl(WbsDao wbsDao, ProjectDao projectDao, IssueDao issueDao) {
        this.wbsDao = wbsDao;
        this.projectDao = projectDao;
        this.issueDao = issueDao;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public WbsTaskResponse selectWbsTaskDetails(UUID wbsTaskId) {
        return selectWbsTaskEntity(wbsTaskId).toResponse();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WbsTaskResponse> selectWbsTaskList(WbsTaskSearchCondition condition) {
        WbsTaskSearchCondition safeCondition = condition == null ? new WbsTaskSearchCondition() : condition;
        return wbsDao.selectWbsTaskList(safeCondition).stream()
                .map(WbsTaskEntity::toResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteWbsTaskDependency(UUID dependencyId) {
        wbsDao.deleteWbsTaskDependency(dependencyId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WbsTaskDependencyResponse> selectWbsTaskDependencyList(WbsTaskDependencySearchCondition condition) {
        WbsTaskDependencySearchCondition safeCondition = condition == null ? new WbsTaskDependencySearchCondition() : condition;
        return wbsDao.selectWbsTaskDependencyList(safeCondition).stream()
                .map(WbsTaskDependencyEntity::toResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * WBS 작업 등록/수정 요청의 필수 조건과 계획 기간을 검증합니다.
     *
     * @param request 검증 대상 요청
     */
    private void validateTaskRequest(WbsTaskRequest request) {
        if (request == null || request.getProjectId() == null) {
            throw new BusinessException("WBS 작업 요청이 올바르지 않습니다.", "WBS_TASK_REQUEST_INVALID");
        }
        validateProject(request.getProjectId());
        if (request.getPlanStartDate() != null && request.getPlanEndDate() != null && request.getPlanStartDate().isAfter(request.getPlanEndDate())) {
            throw new BusinessException("계획 시작일은 종료일보다 늦을 수 없습니다.", "WBS_PLAN_DATE_INVALID");
        }
    }

    /**
     * 프로젝트 존재 여부를 검증합니다.
     *
     * @param projectId 프로젝트 ID
     */
    private void validateProject(UUID projectId) {
        projectDao.selectProjectById(projectId)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다.", "PROJECT_NOT_FOUND"));
    }

    /**
     * 상위 WBS 작업을 조회하고 동일 프로젝트 소속인지 검증합니다.
     *
     * @param request WBS 작업 요청
     * @return 상위 작업, 루트 작업이면 {@code null}
     */
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

    /**
     * 연결 이슈를 조회하고 동일 프로젝트 소속인지 검증합니다.
     *
     * @param request WBS 작업 요청
     * @return 연결 이슈, 미연결 작업이면 {@code null}
     */
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

    /**
     * WBS 작업 엔티티를 조회하고 존재하지 않으면 업무 예외를 발생시킵니다.
     *
     * @param wbsTaskId WBS 작업 ID
     * @return WBS 작업 엔티티
     */
    private WbsTaskEntity selectWbsTaskEntity(UUID wbsTaskId) {
        return wbsDao.selectWbsTaskDetails(wbsTaskId)
                .orElseThrow(() -> new BusinessException("WBS 작업을 찾을 수 없습니다.", "WBS_TASK_NOT_FOUND"));
    }

    /**
     * 부모 WBS 코드와 표시 순서를 기준으로 신규 WBS 코드를 생성합니다.
     *
     * @param parentTask 상위 작업
     * @param displayOrder 동일 계층 내 표시 순서
     * @return 자동 생성된 WBS 코드
     */
    private String createWbsCode(WbsTaskEntity parentTask, int displayOrder) {
        if (parentTask == null) {
            return String.valueOf(displayOrder);
        }
        return parentTask.getWbsCode() + "." + displayOrder;
    }

    /**
     * 요청 값을 WBS 작업 엔티티에 반영합니다.
     *
     * @param task 저장 또는 수정 대상 작업
     * @param request 입력 요청
     * @param issue 연결 이슈
     */
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

    /**
     * 정수 값이 {@code null}이면 0으로 보정합니다.
     *
     * @param value 보정 대상 값
     * @return 보정된 정수 값
     */
    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    /**
     * WBS 의존성 요청의 필수 조건과 지원 유형을 검증합니다.
     *
     * @param request 검증 대상 요청
     */
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

    /**
     * 선행 작업과 후행 작업이 같은 프로젝트에 속하는지 검증합니다.
     *
     * @param projectId 프로젝트 ID
     * @param predecessor 선행 작업
     * @param successor 후행 작업
     */
    private void validateDependencyTaskProject(UUID projectId, WbsTaskEntity predecessor, WbsTaskEntity successor) {
        if (!projectId.equals(predecessor.getProjectId()) || !projectId.equals(successor.getProjectId())) {
            throw new BusinessException("선후행 작업은 동일 프로젝트에 속해야 합니다.", "WBS_DEPENDENCY_PROJECT_MISMATCH");
        }
    }

    /**
     * 하위 작업을 기준으로 부모 작업의 기간, 공수, 진행률을 집계합니다.
     *
     * @param tasks Gantt 작업 목록
     */
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

    /**
     * 하위 작업의 예상 공수 가중치를 기준으로 부모 진행률을 계산합니다.
     *
     * @param children 하위 작업 목록
     * @return 계산된 진행률
     */
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

    /**
     * Gantt 작업별 지연, 공수 초과, 선행 작업 미완료 위험을 계산합니다.
     *
     * @param tasks Gantt 작업 목록
     * @param dependencies WBS 의존성 목록
     */
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

    /**
     * Gantt 막대 표시를 위한 좌측 위치와 너비 비율을 계산합니다.
     *
     * @param projectId 프로젝트 ID
     * @param tasks Gantt 작업 목록
     */
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

    /**
     * 작업 기간을 기준으로 Gantt 전체 타임라인 응답을 생성합니다.
     *
     * @param projectId 프로젝트 ID
     * @param tasks Gantt 작업 목록
     * @return Gantt 응답
     */
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
