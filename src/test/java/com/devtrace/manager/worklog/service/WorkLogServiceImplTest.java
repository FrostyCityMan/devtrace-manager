package com.devtrace.manager.worklog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devtrace.manager.issue.dao.IssueDao;
import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.issue.dto.IssueStatus;
import com.devtrace.manager.issue.dto.IssueType;
import com.devtrace.manager.sprint.service.SprintSnapshotService;
import com.devtrace.manager.worklog.dao.WorkLogDao;
import com.devtrace.manager.worklog.dto.WorkLogEntity;
import com.devtrace.manager.worklog.dto.WorkLogRequest;
import com.devtrace.manager.worklog.dto.WorkLogResponse;
import com.devtrace.manager.worklog.dto.WorkLogSearchCondition;
import com.devtrace.manager.worklog.service.impl.WorkLogServiceImpl;
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
class WorkLogServiceImplTest {

    @Mock
    private WorkLogDao workLogDao;

    @Mock
    private IssueDao issueDao;

    @Mock
    private SprintSnapshotService sprintSnapshotService;

    private WorkLogService workLogService;

    @BeforeEach
    void setUp() {
        workLogService = new WorkLogServiceImpl(workLogDao, issueDao, sprintSnapshotService);
    }

    @Test
    void insertWorkLog() {
        UUID issueId = UUID.randomUUID();
        when(issueDao.selectIssueByIdDetails(issueId)).thenReturn(Optional.of(createIssue(issueId)));
        when(workLogDao.sumSpentMinutesByIssueId(issueId)).thenReturn(90);
        WorkLogRequest request = createRequest(issueId, 90);

        WorkLogResponse response = workLogService.insertWorkLog(request);

        ArgumentCaptor<WorkLogEntity> captor = ArgumentCaptor.forClass(WorkLogEntity.class);
        verify(workLogDao).insertWorkLog(captor.capture());
        WorkLogEntity saved = captor.getValue();

        assertThat(saved.getWorkLogId()).isNotNull();
        assertThat(saved.getIssueId()).isEqualTo(issueId);
        assertThat(saved.getSpentMinutes()).isEqualTo(90);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(response.getSpentMinutes()).isEqualTo(90);
        verify(issueDao).updateIssueSpentMinutes(eq(issueId), eq(90), any(LocalDateTime.class));
        verify(sprintSnapshotService).saveSprintDailySnapshotByIssueId(issueId);
    }

    @Test
    void updateWorkLog() {
        UUID issueId = UUID.randomUUID();
        UUID workLogId = UUID.randomUUID();
        WorkLogEntity workLog = createWorkLog(workLogId, issueId, 60);
        when(issueDao.selectIssueByIdDetails(issueId)).thenReturn(Optional.of(createIssue(issueId)));
        when(workLogDao.selectWorkLogByIdDetails(workLogId)).thenReturn(Optional.of(workLog));
        when(workLogDao.sumSpentMinutesByIssueId(issueId)).thenReturn(120);

        WorkLogRequest request = createRequest(issueId, 120);
        WorkLogResponse response = workLogService.updateWorkLog(workLogId, request);

        verify(workLogDao).updateWorkLog(workLog);
        assertThat(response.getSpentMinutes()).isEqualTo(120);
        verify(issueDao).updateIssueSpentMinutes(eq(issueId), eq(120), any(LocalDateTime.class));
        verify(sprintSnapshotService).saveSprintDailySnapshotByIssueId(issueId);
    }

    @Test
    void deleteWorkLog() {
        UUID issueId = UUID.randomUUID();
        UUID workLogId = UUID.randomUUID();
        WorkLogEntity workLog = createWorkLog(workLogId, issueId, 60);
        when(workLogDao.selectWorkLogByIdDetails(workLogId)).thenReturn(Optional.of(workLog));
        when(workLogDao.sumSpentMinutesByIssueId(issueId)).thenReturn(0);

        workLogService.deleteWorkLog(workLogId);

        verify(workLogDao).deleteWorkLog(workLogId);
        verify(issueDao).updateIssueSpentMinutes(eq(issueId), eq(0), any(LocalDateTime.class));
        verify(sprintSnapshotService).saveSprintDailySnapshotByIssueId(issueId);
    }

    @Test
    void selectWorkLogList() {
        UUID issueId = UUID.randomUUID();
        WorkLogEntity workLog = createWorkLog(UUID.randomUUID(), issueId, 45);
        when(workLogDao.selectWorkLogList(any(WorkLogSearchCondition.class))).thenReturn(List.of(workLog));

        List<WorkLogResponse> workLogs = workLogService.selectWorkLogList(new WorkLogSearchCondition());

        assertThat(workLogs).hasSize(1);
        assertThat(workLogs.get(0).getIssueId()).isEqualTo(issueId);
        assertThat(workLogs.get(0).getSpentMinutes()).isEqualTo(45);
    }

    private WorkLogRequest createRequest(UUID issueId, int spentMinutes) {
        WorkLogRequest request = new WorkLogRequest();
        request.setIssueId(issueId);
        request.setUserId(WorkLogRequest.DEFAULT_ADMIN_USER_ID);
        request.setWorkDate(LocalDate.of(2026, 5, 4));
        request.setWorkContent("이슈 구현");
        request.setSpentMinutes(spentMinutes);
        return request;
    }

    private WorkLogEntity createWorkLog(UUID workLogId, UUID issueId, int spentMinutes) {
        WorkLogEntity workLog = new WorkLogEntity();
        workLog.setWorkLogId(workLogId);
        workLog.setIssueId(issueId);
        workLog.setUserId(WorkLogRequest.DEFAULT_ADMIN_USER_ID);
        workLog.setWorkDate(LocalDate.of(2026, 5, 4));
        workLog.setWorkContent("이슈 구현");
        workLog.setSpentMinutes(spentMinutes);
        workLog.setCreatedAt(LocalDateTime.of(2026, 5, 4, 9, 0));
        return workLog;
    }

    private IssueEntity createIssue(UUID issueId) {
        IssueEntity issue = new IssueEntity();
        issue.setIssueId(issueId);
        issue.setProjectId(UUID.randomUUID());
        issue.setIssueKey("DTR-101");
        issue.setIssueType(IssueType.FEATURE);
        issue.setTitle("작업 공수 관리");
        issue.setStatus(IssueStatus.IN_PROGRESS);
        issue.setPriority(IssuePriority.NORMAL);
        issue.setEstimatedMinutes(480);
        issue.setSpentMinutes(0);
        issue.setCreatedAt(LocalDateTime.of(2026, 5, 4, 9, 0));
        return issue;
    }
}
