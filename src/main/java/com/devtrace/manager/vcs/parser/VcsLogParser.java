package com.devtrace.manager.vcs.parser;

import com.devtrace.manager.vcs.dto.VcsChangeLogEntity;
import java.util.List;
import java.util.UUID;

/**
 * 형상관리 로그 문자열을 공통 변경이력 모델로 변환하는 파서 계약입니다.
 *
 * <p>Git과 SVN의 입력 형식은 다르지만, 서비스 계층은 이 계약을 통해 동일한
 * 변경이력/변경파일/이슈키 모델을 사용합니다.</p>
 */
public interface VcsLogParser {

    /**
     * 형상관리 로그 문자열을 파싱합니다.
     *
     * @param projectId 변경이력이 속할 프로젝트 ID
     * @param logText 사용자가 입력한 로그 문자열
     * @return 파싱된 변경이력 목록
     */
    List<VcsChangeLogEntity> parse(UUID projectId, String logText);
}
