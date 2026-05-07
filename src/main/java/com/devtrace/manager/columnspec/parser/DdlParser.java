package com.devtrace.manager.columnspec.parser;

import com.devtrace.manager.columnspec.dto.ColumnSpecEntity;
import com.devtrace.manager.columnspec.dto.DatabaseType;
import java.util.List;
import java.util.UUID;

/**
 * DDL 문자열을 컬럼명세 엔티티 목록으로 변환하는 파서 계약입니다.
 *
 * <p>지원 DB 유형별 문법 차이는 구현체에서 흡수하고, 서비스 계층은 동일한 계약으로
 * 컬럼명세 미리보기와 Excel 생성을 수행합니다.</p>
 */
public interface DdlParser {

    /**
     * DDL 문자열을 해석하여 컬럼명세 엔티티 목록을 생성합니다.
     *
     * @param projectId 컬럼명세가 속할 프로젝트 ID
     * @param databaseType DDL 작성 기준 DB 유형
     * @param ddl 사용자가 입력한 DDL 문자열
     * @return 파싱된 컬럼명세 엔티티 목록
     */
    List<ColumnSpecEntity> parse(UUID projectId, DatabaseType databaseType, String ddl);
}
