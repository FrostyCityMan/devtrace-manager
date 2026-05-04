package com.devtrace.manager.vcs.service;

import com.devtrace.manager.vcs.dto.VcsChangeLogResponse;
import com.devtrace.manager.vcs.dto.VcsLogRequest;
import com.devtrace.manager.vcs.dto.VcsLogSearchCondition;
import java.util.List;

public interface VcsLogService {

    List<VcsChangeLogResponse> selectChangeLogPreviewList(VcsLogRequest request);

    byte[] selectChangeLogExcel(VcsLogRequest request);

    List<VcsChangeLogResponse> selectChangeLogList(VcsLogSearchCondition condition);
}
