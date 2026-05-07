package com.devtrace.manager.vcs.service;

import com.devtrace.manager.vcs.dto.VcsChangeLogResponse;
import com.devtrace.manager.vcs.dto.VcsLogRequest;
import com.devtrace.manager.vcs.dto.VcsLogSearchCondition;
import java.util.List;

/**
 * 형상관리 로그 분석과 변경이력 조회 업무를 담당하는 서비스 계약입니다.
 *
 * <p>외부 저장소 직접 연동 없이 사용자가 입력한 Git/SVN 로그 문자열을 분석합니다.</p>
 */
public interface VcsLogService {

    /**
     * Git/SVN 로그 텍스트를 파싱해 변경이력 미리보기 목록을 생성한다.
     *
     * <p>미리보기 생성 시 프로젝트 기준 기존 변경이력을 대체 저장한다.</p>
     *
     * @param request VCS 로그 분석 요청
     * @return 변경이력 미리보기 목록
     */
    List<VcsChangeLogResponse> selectChangeLogPreviewList(VcsLogRequest request);

    /**
     * Git/SVN 로그 텍스트를 파싱해 변경이력 Excel 파일을 생성한다.
     *
     * @param request VCS 로그 분석 요청
     * @return xlsx 파일 바이트 배열
     */
    byte[] selectChangeLogExcel(VcsLogRequest request);

    /**
     * 저장된 변경이력 목록을 조회한다.
     *
     * @param condition 프로젝트와 VCS 유형 검색 조건
     * @return 변경이력 목록
     */
    List<VcsChangeLogResponse> selectChangeLogList(VcsLogSearchCondition condition);
}
