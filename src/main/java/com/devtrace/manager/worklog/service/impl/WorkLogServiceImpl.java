package com.devtrace.manager.worklog.service.impl;

import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.common.util.DateTimeUtil;
import com.devtrace.manager.issue.dao.IssueDao;
import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.sprint.service.SprintSnapshotService;
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

/**
 * 작업 공수 관리 업무 규칙을 구현합니다.
 *
 * <p>이슈 존재 여부를 검증하고, 공수 변경 후 이슈의 {@code SPENT_MINUTES}를 재계산합니다.</p>
 */
@Service
@Transactional(readOnly = true)
public class WorkLogServiceImpl implements WorkLogService {

    private final WorkLogDao workLogDao;
    private final IssueDao issueDao;
    private final SprintSnapshotService sprintSnapshotService;

    /**
     * 작업 공수 서비스 구현체를 생성한다.
     *
     * @param workLogDao 작업 공수 SQL 호출 DAO
     * @param issueDao 이슈 검증 및 공수 합계 반영 DAO
     * @param sprintSnapshotService 스프린트 일자별 스냅샷 서비스
     */
    public WorkLogServiceImpl(WorkLogDao workLogDao, IssueDao issueDao, SprintSnapshotService sprintSnapshotService) {
        this.workLogDao = workLogDao;
        this.issueDao = issueDao;
        this.sprintSnapshotService = sprintSnapshotService;
    }

    /**
     * 작업 공수를 등록한다.
     *
     * <p>작업 공수 저장 후 이슈의 실제 공수 합계를 다시 계산하여 ISSUE.SPENT_MINUTES에 반영한다.</p>
     *
     * @param request 작업 공수 등록 요청
     * @return 등록된 작업 공수 응답
     */
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
        sprintSnapshotService.saveSprintDailySnapshotByIssueId(workLog.getIssueId());
        return workLog.toResponse();
    }

    /**
     * 작업 공수를 수정한다.
     *
     * <p>작업 공수가 다른 이슈로 이동될 수 있으므로 이전 이슈와 새 이슈의 실제 공수 합계를
     * 모두 갱신한다.</p>
     *
     * @param workLogId 수정 대상 작업 공수 ID
     * @param request 작업 공수 수정 요청
     * @return 수정된 작업 공수 응답
     */
    @Override
    @Transactional
    public WorkLogResponse updateWorkLog(UUID workLogId, WorkLogRequest request) {
        validateIssue(request.getIssueId());

        WorkLogEntity workLog = selectWorkLogEntity(workLogId);
        UUID previousIssueId = workLog.getIssueId();
        applyRequest(workLog, request);

        workLogDao.updateWorkLog(workLog);
        refreshIssueSpentMinutes(previousIssueId);
        sprintSnapshotService.saveSprintDailySnapshotByIssueId(previousIssueId);
        if (!previousIssueId.equals(workLog.getIssueId())) {
            refreshIssueSpentMinutes(workLog.getIssueId());
            sprintSnapshotService.saveSprintDailySnapshotByIssueId(workLog.getIssueId());
        }
        return workLog.toResponse();
    }

    /**
     * 작업 공수를 삭제한다.
     *
     * <p>삭제 후 연결 이슈의 실제 공수 합계를 다시 계산한다.</p>
     *
     * @param workLogId 삭제 대상 작업 공수 ID
     */
    @Override
    @Transactional
    public void deleteWorkLog(UUID workLogId) {
        WorkLogEntity workLog = selectWorkLogEntity(workLogId);
        workLogDao.deleteWorkLog(workLog.getWorkLogId());
        refreshIssueSpentMinutes(workLog.getIssueId());
        sprintSnapshotService.saveSprintDailySnapshotByIssueId(workLog.getIssueId());
    }

    /**
     * 작업 공수 상세를 조회한다.
     *
     * @param workLogId 조회 대상 작업 공수 ID
     * @return 작업 공수 상세 응답
     */
    @Override
    public WorkLogResponse selectWorkLogDetails(UUID workLogId) {
        return selectWorkLogEntity(workLogId).toResponse();
    }

    /**
     * 검색 조건에 맞는 작업 공수 목록을 조회한다.
     *
     * @param condition 검색 조건
     * @return 작업 공수 목록
     */
    @Override
    public List<WorkLogResponse> selectWorkLogList(WorkLogSearchCondition condition) {
        return workLogDao.selectWorkLogList(condition).stream()
                .map(WorkLogEntity::toResponse)
                .toList();
    }

    /**
     * 작업 공수 엔티티를 조회하고 없으면 업무 예외를 발생시킨다.
     *
     * @param workLogId 조회 대상 작업 공수 ID
     * @return 작업 공수 엔티티
     */
    private WorkLogEntity selectWorkLogEntity(UUID workLogId) {
        return workLogDao.selectWorkLogByIdDetails(workLogId)
                .orElseThrow(() -> new BusinessException("작업 공수를 찾을 수 없습니다.", "WORK_LOG_NOT_FOUND"));
    }

    /**
     * 이슈 존재 여부를 검증한다.
     *
     * @param issueId 이슈 ID
     * @return 이슈 엔티티
     */
    private IssueEntity validateIssue(UUID issueId) {
        return issueDao.selectIssueByIdDetails(issueId)
                .orElseThrow(() -> new BusinessException("이슈를 찾을 수 없습니다.", "ISSUE_NOT_FOUND"));
    }

    /**
     * 요청 값을 작업 공수 엔티티에 반영한다.
     *
     * @param workLog 값이 반영될 작업 공수 엔티티
     * @param request 사용자 요청 DTO
     */
    private void applyRequest(WorkLogEntity workLog, WorkLogRequest request) {
        workLog.setIssueId(request.getIssueId());
        workLog.setUserId(request.getUserId());
        workLog.setWorkDate(request.getWorkDate());
        workLog.setWorkContent(request.getWorkContent());
        workLog.setSpentMinutes(request.getSpentMinutes());
    }

    /**
     * 이슈의 실제 공수 합계를 재계산해 반영한다.
     *
     * <p>작업 공수는 분 단위로 저장하며, 이슈의 실제 공수는 작업 공수 합계에서 파생된다.</p>
     *
     * @param issueId 합계를 갱신할 이슈 ID
     */
    private void refreshIssueSpentMinutes(UUID issueId) {
        int spentMinutes = workLogDao.sumSpentMinutesByIssueId(issueId);
        issueDao.updateIssueSpentMinutes(issueId, spentMinutes, DateTimeUtil.now());
    }
}
