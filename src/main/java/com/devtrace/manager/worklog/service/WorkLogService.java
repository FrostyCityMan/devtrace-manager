package com.devtrace.manager.worklog.service;

import com.devtrace.manager.worklog.dto.WorkLogRequest;
import com.devtrace.manager.worklog.dto.WorkLogResponse;
import com.devtrace.manager.worklog.dto.WorkLogSearchCondition;
import java.util.List;
import java.util.UUID;

public interface WorkLogService {

    WorkLogResponse insertWorkLog(WorkLogRequest request);

    WorkLogResponse updateWorkLog(UUID workLogId, WorkLogRequest request);

    void deleteWorkLog(UUID workLogId);

    WorkLogResponse selectWorkLogDetails(UUID workLogId);

    List<WorkLogResponse> selectWorkLogList(WorkLogSearchCondition condition);
}
