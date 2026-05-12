package com.devtrace.manager.issue.service.impl;

import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.common.util.DateTimeUtil;
import com.devtrace.manager.issue.dao.IssueDao;
import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.issue.dto.IssueRequest;
import com.devtrace.manager.issue.dto.IssueResponse;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import com.devtrace.manager.issue.dto.IssueStatus;
import com.devtrace.manager.issue.service.IssueService;
import com.devtrace.manager.project.dao.ProjectDao;
import com.devtrace.manager.sprint.service.SprintSnapshotService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 이슈 관리 업무 규칙을 구현합니다.
 *
 * <p>이슈 키 생성, 프로젝트 정합성 검증, 상태 변경, 완료일 보정 로직을 담당합니다.</p>
 */
@Service
@Transactional(readOnly = true)
public class IssueServiceImpl implements IssueService {

    private final IssueDao issueDao;
    private final ProjectDao projectDao;
    private final SprintSnapshotService sprintSnapshotService;

    /**
     * 이슈 서비스 구현체를 생성한다.
     *
     * @param issueDao 이슈 SQL 호출 DAO
     * @param projectDao 프로젝트 존재 검증 DAO
     * @param sprintSnapshotService 스프린트 일자별 스냅샷 서비스
     */
    public IssueServiceImpl(IssueDao issueDao, ProjectDao projectDao, SprintSnapshotService sprintSnapshotService) {
        this.issueDao = issueDao;
        this.projectDao = projectDao;
        this.sprintSnapshotService = sprintSnapshotService;
    }

    /**
     * 이슈를 등록한다.
     *
     * <p>프로젝트 존재 여부를 검증하고, 실제 공수는 작업 공수 합산 대상이므로 0분으로 시작한다.</p>
     *
     * @param request 이슈 등록 요청
     * @return 등록된 이슈 응답
     */
    @Override
    @Transactional
    public IssueResponse insertIssue(IssueRequest request) {
        validateProject(request.getProjectId());

        LocalDateTime now = DateTimeUtil.now();
        IssueEntity issue = new IssueEntity();
        issue.setIssueId(UUID.randomUUID());
        applyRequest(issue, request);
        issue.setSpentMinutes(0);
        issue.setCreatedAt(now);

        issueDao.insertIssue(issue);
        return issue.toResponse();
    }

    /**
     * 이슈 기본 정보를 수정한다.
     *
     * <p>프로젝트 정합성을 확인한 뒤 요청 값을 반영한다. 실제 공수는 WorkLog 합계로 관리하므로
     * 이 수정 흐름에서 직접 변경하지 않는다.</p>
     *
     * @param issueId 수정 대상 이슈 ID
     * @param request 이슈 수정 요청
     * @return 수정된 이슈 응답
     */
    @Override
    @Transactional
    public IssueResponse updateIssue(UUID issueId, IssueRequest request) {
        validateProject(request.getProjectId());

        IssueEntity issue = selectIssueEntityDetails(issueId);
        applyRequest(issue, request);
        issue.setUpdatedAt(DateTimeUtil.now());

        issueDao.updateIssue(issue);
        return issue.toResponse();
    }

    /**
     * 이슈 상태를 변경한다.
     *
     * <p>완료 계열 상태로 전환하면서 실제 완료일이 비어 있으면 현재일로 보정한다.</p>
     *
     * @param issueId 상태 변경 대상 이슈 ID
     * @param status 변경할 상태
     * @return 상태 변경 후 이슈 응답
     */
    @Override
    @Transactional
    public IssueResponse updateIssueStatus(UUID issueId, IssueStatus status) {
        if (status == null) {
            throw new BusinessException("이슈 상태는 필수입니다.", "ISSUE_STATUS_REQUIRED");
        }

        IssueEntity issue = selectIssueEntityDetails(issueId);
        LocalDateTime now = DateTimeUtil.now();
        issue.setStatus(status);
        issue.setUpdatedAt(now);
        if (status.isCompleted() && issue.getResolvedDate() == null) {
            issue.setResolvedDate(now.toLocalDate());
        }

        issueDao.updateIssueStatus(issue.getIssueId(), issue.getStatus(), issue.getResolvedDate(), issue.getUpdatedAt());
        sprintSnapshotService.saveSprintDailySnapshotByIssueId(issue.getIssueId());
        return issue.toResponse();
    }

    /**
     * 이슈를 삭제한다.
     *
     * @param issueId 삭제 대상 이슈 ID
     */
    @Override
    @Transactional
    public void deleteIssue(UUID issueId) {
        IssueEntity issue = selectIssueEntityDetails(issueId);
        issueDao.deleteIssue(issue.getIssueId());
    }

    /**
     * 이슈 상세 정보를 조회한다.
     *
     * @param issueId 조회 대상 이슈 ID
     * @return 이슈 상세 응답
     */
    @Override
    public IssueResponse selectIssueDetails(UUID issueId) {
        return selectIssueEntityDetails(issueId).toResponse();
    }

    /**
     * 검색 조건에 맞는 이슈 목록을 조회한다.
     *
     * @param condition 이슈 검색 조건
     * @return 이슈 목록
     */
    @Override
    public List<IssueResponse> selectIssueList(IssueSearchCondition condition) {
        return issueDao.selectIssueList(condition).stream()
                .map(IssueEntity::toResponse)
                .toList();
    }

    /**
     * 이슈 엔티티를 조회하고 없으면 업무 예외를 발생시킨다.
     *
     * @param issueId 조회 대상 이슈 ID
     * @return 이슈 엔티티
     */
    private IssueEntity selectIssueEntityDetails(UUID issueId) {
        return issueDao.selectIssueByIdDetails(issueId)
                .orElseThrow(() -> new BusinessException("이슈를 찾을 수 없습니다.", "ISSUE_NOT_FOUND"));
    }

    /**
     * 이슈가 속할 프로젝트의 존재 여부를 검증한다.
     *
     * @param projectId 프로젝트 ID
     */
    private void validateProject(UUID projectId) {
        projectDao.selectProjectById(projectId)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다.", "PROJECT_NOT_FOUND"));
    }

    /**
     * 요청 DTO의 값을 이슈 엔티티에 반영한다.
     *
     * <p>예상 공수가 비어 있으면 0분으로 보정한다.</p>
     *
     * @param issue 값이 반영될 이슈 엔티티
     * @param request 사용자 요청 DTO
     */
    private void applyRequest(IssueEntity issue, IssueRequest request) {
        issue.setProjectId(request.getProjectId());
        issue.setIssueKey(request.getIssueKey());
        issue.setIssueType(request.getIssueType());
        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setStatus(request.getStatus());
        issue.setPriority(request.getPriority());
        issue.setAssigneeId(request.getAssigneeId());
        issue.setReporterId(request.getReporterId());
        issue.setStartDate(request.getStartDate());
        issue.setDueDate(request.getDueDate());
        issue.setResolvedDate(request.getResolvedDate());
        issue.setEstimatedMinutes(request.getEstimatedMinutes() == null ? 0 : request.getEstimatedMinutes());
    }
}
