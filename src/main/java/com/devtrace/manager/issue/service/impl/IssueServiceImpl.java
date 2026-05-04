package com.devtrace.manager.issue.service.impl;

import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.common.util.DateTimeUtil;
import com.devtrace.manager.issue.dao.IssueDao;
import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.issue.dto.IssueRequest;
import com.devtrace.manager.issue.dto.IssueResponse;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import com.devtrace.manager.issue.service.IssueService;
import com.devtrace.manager.project.dao.ProjectDao;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class IssueServiceImpl implements IssueService {

    private final IssueDao issueDao;
    private final ProjectDao projectDao;

    public IssueServiceImpl(IssueDao issueDao, ProjectDao projectDao) {
        this.issueDao = issueDao;
        this.projectDao = projectDao;
    }

    @Override
    @Transactional
    public IssueResponse createIssue(IssueRequest request) {
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

    @Override
    @Transactional
    public IssueResponse updateIssue(UUID issueId, IssueRequest request) {
        validateProject(request.getProjectId());

        IssueEntity issue = findIssue(issueId);
        applyRequest(issue, request);
        issue.setUpdatedAt(DateTimeUtil.now());

        issueDao.updateIssue(issue);
        return issue.toResponse();
    }

    @Override
    @Transactional
    public void deleteIssue(UUID issueId) {
        IssueEntity issue = findIssue(issueId);
        issueDao.deleteIssue(issue.getIssueId());
    }

    @Override
    public IssueResponse getIssue(UUID issueId) {
        return findIssue(issueId).toResponse();
    }

    @Override
    public List<IssueResponse> getIssueList(IssueSearchCondition condition) {
        return issueDao.selectIssueList(condition).stream()
                .map(IssueEntity::toResponse)
                .toList();
    }

    private IssueEntity findIssue(UUID issueId) {
        return issueDao.selectIssueById(issueId)
                .orElseThrow(() -> new BusinessException("이슈를 찾을 수 없습니다.", "ISSUE_NOT_FOUND"));
    }

    private void validateProject(UUID projectId) {
        projectDao.selectProjectById(projectId)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다.", "PROJECT_NOT_FOUND"));
    }

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
