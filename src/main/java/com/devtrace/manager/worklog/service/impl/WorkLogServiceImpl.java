package com.devtrace.manager.worklog.service.impl;

import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.common.util.DateTimeUtil;
import com.devtrace.manager.issue.dao.IssueDao;
import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.worklog.dao.WorkLogDao;
import com.devtrace.manager.worklog.dto.WorkLogEntity;
import com.devtrace.manager.worklog.dto.WorkLogRequest;
import com.devtrace.manager.worklog.dto.WorkLogResponse;
import com.devtrace.manager.worklog.dto.WorkLogSearchCondition;
import com.devtrace.manager.worklog.service.WorkLogService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WorkLogServiceImpl implements WorkLogService {

    private final WorkLogDao workLogDao;
    private final IssueDao issueDao;

    public WorkLogServiceImpl(WorkLogDao workLogDao, IssueDao issueDao) {
        this.workLogDao = workLogDao;
        this.issueDao = issueDao;
    }

    @Override
    @Transactional
    public WorkLogResponse insertWorkLog(WorkLogRequest request) {
        validateIssue(request.getIssueId());

        WorkLogEntity workLog = new WorkLogEntity();
        workLog.setWorkLogId(UUID.randomUUID());
        applyRequest(workLog, request);
        workLog.setCreatedAt(DateTimeUtil.now());

        workLogDao.insertWorkLog(workLog);
        refreshIssueSpentMinutes(workLog.getIssueId());
        return workLog.toResponse();
    }

    @Override
    @Transactional
    public WorkLogResponse updateWorkLog(UUID workLogId, WorkLogRequest request) {
        validateIssue(request.getIssueId());

        WorkLogEntity workLog = selectWorkLogEntity(workLogId);
        UUID previousIssueId = workLog.getIssueId();
        applyRequest(workLog, request);

        workLogDao.updateWorkLog(workLog);
        refreshIssueSpentMinutes(previousIssueId);
        if (!previousIssueId.equals(workLog.getIssueId())) {
            refreshIssueSpentMinutes(workLog.getIssueId());
        }
        return workLog.toResponse();
    }

    @Override
    @Transactional
    public void deleteWorkLog(UUID workLogId) {
        WorkLogEntity workLog = selectWorkLogEntity(workLogId);
        workLogDao.deleteWorkLog(workLog.getWorkLogId());
        refreshIssueSpentMinutes(workLog.getIssueId());
    }

    @Override
    public WorkLogResponse selectWorkLogDetails(UUID workLogId) {
        return selectWorkLogEntity(workLogId).toResponse();
    }

    @Override
    public List<WorkLogResponse> selectWorkLogList(WorkLogSearchCondition condition) {
        return workLogDao.selectWorkLogList(condition).stream()
                .map(WorkLogEntity::toResponse)
                .toList();
    }

    private WorkLogEntity selectWorkLogEntity(UUID workLogId) {
        return workLogDao.selectWorkLogByIdDetails(workLogId)
                .orElseThrow(() -> new BusinessException("작업 공수를 찾을 수 없습니다.", "WORK_LOG_NOT_FOUND"));
    }

    private IssueEntity validateIssue(UUID issueId) {
        return issueDao.selectIssueByIdDetails(issueId)
                .orElseThrow(() -> new BusinessException("이슈를 찾을 수 없습니다.", "ISSUE_NOT_FOUND"));
    }

    private void applyRequest(WorkLogEntity workLog, WorkLogRequest request) {
        workLog.setIssueId(request.getIssueId());
        workLog.setUserId(request.getUserId());
        workLog.setWorkDate(request.getWorkDate());
        workLog.setWorkContent(request.getWorkContent());
        workLog.setSpentMinutes(request.getSpentMinutes());
    }

    private void refreshIssueSpentMinutes(UUID issueId) {
        int spentMinutes = workLogDao.sumSpentMinutesByIssueId(issueId);
        issueDao.updateIssueSpentMinutes(issueId, spentMinutes, DateTimeUtil.now());
    }
}
