package com.devtrace.manager.worklog.service;

import com.devtrace.manager.worklog.dto.WorkLogRequest;
import com.devtrace.manager.worklog.dto.WorkLogResponse;
import com.devtrace.manager.worklog.dto.WorkLogSearchCondition;
import java.util.List;
import java.util.UUID;

/**
 * 작업 공수 관리 업무를 담당하는 서비스 계약입니다.
 *
 * <p>공수는 분 단위로 저장되며, 등록/수정/삭제 후 이슈의 실제 공수 합계를 갱신합니다.</p>
 */
public interface WorkLogService {

    /**
     * 작업 공수를 등록한다.
     *
     * @param request 작업 공수 등록 요청
     * @return 등록된 작업 공수 응답
     */
    WorkLogResponse insertWorkLog(WorkLogRequest request);

    /**
     * 작업 공수를 수정한다.
     *
     * @param workLogId 수정 대상 작업 공수 ID
     * @param request 작업 공수 수정 요청
     * @return 수정된 작업 공수 응답
     */
    WorkLogResponse updateWorkLog(UUID workLogId, WorkLogRequest request);

    /**
     * 작업 공수를 삭제한다.
     *
     * @param workLogId 삭제 대상 작업 공수 ID
     */
    void deleteWorkLog(UUID workLogId);

    /**
     * 작업 공수 상세를 조회한다.
     *
     * @param workLogId 조회 대상 작업 공수 ID
     * @return 작업 공수 상세 응답
     */
    WorkLogResponse selectWorkLogDetails(UUID workLogId);

    /**
     * 검색 조건에 맞는 작업 공수 목록을 조회한다.
     *
     * @param condition 이슈/사용자 검색 조건
     * @return 작업 공수 목록
     */
    List<WorkLogResponse> selectWorkLogList(WorkLogSearchCondition condition);
}
