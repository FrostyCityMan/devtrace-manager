package com.devtrace.manager.worklog.dao;

import com.devtrace.manager.worklog.dto.WorkLogEntity;
import com.devtrace.manager.worklog.dto.WorkLogSearchCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkLogDao {

    void insertWorkLog(WorkLogEntity workLog);

    void updateWorkLog(WorkLogEntity workLog);

    void deleteWorkLog(UUID workLogId);

    Optional<WorkLogEntity> selectWorkLogByIdDetails(UUID workLogId);

    List<WorkLogEntity> selectWorkLogList(WorkLogSearchCondition condition);

    int sumSpentMinutesByIssueId(UUID issueId);
}
