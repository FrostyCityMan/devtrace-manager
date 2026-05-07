package com.devtrace.manager.columnspec.dao;

import com.devtrace.manager.columnspec.dto.ColumnSpecEntity;
import com.devtrace.manager.columnspec.dto.ColumnSpecSearchCondition;
import java.util.List;
import java.util.UUID;
import org.apache.ibatis.annotations.Param;

/**
 * 컬럼명세 저장소에 접근하는 MyBatis DAO입니다.
 *
 * <p>DDL 파싱 결과 저장과 프로젝트별 컬럼명세 검색 SQL을 제공합니다.</p>
 */
public interface ColumnSpecDao {

    /**
     * 컬럼명세 목록을 일괄 저장한다.
     *
     * @param columnSpecs 저장할 컬럼명세 엔티티 목록
     */
    void insertColumnSpecs(@Param("columnSpecs") List<ColumnSpecEntity> columnSpecs);

    /**
     * 프로젝트 기준 컬럼명세를 삭제한다.
     *
     * @param projectId 프로젝트 ID
     */
    void deleteColumnSpecsByProjectId(UUID projectId);

    /**
     * 검색 조건에 맞는 컬럼명세 목록을 조회한다.
     *
     * @param condition 프로젝트와 테이블명 검색 조건
     * @return 컬럼명세 엔티티 목록
     */
    List<ColumnSpecEntity> selectColumnSpecList(ColumnSpecSearchCondition condition);
}
