package com.devtrace.manager.columnspec.service;

import com.devtrace.manager.columnspec.dto.ColumnSpecRequest;
import com.devtrace.manager.columnspec.dto.ColumnSpecResponse;
import com.devtrace.manager.columnspec.dto.ColumnSpecSearchCondition;
import java.util.List;

/**
 * 컬럼명세 생성과 조회 업무를 담당하는 서비스 계약입니다.
 *
 * <p>DDL 파싱 결과를 저장하고, Apache POI 기반 컬럼명세 Excel 산출물로 변환합니다.</p>
 */
public interface ColumnSpecService {

    /**
     * DDL을 파싱해 컬럼명세 미리보기 목록을 생성한다.
     *
     * <p>미리보기 생성 시 프로젝트의 기존 컬럼명세를 대체 저장한다.</p>
     *
     * @param request 컬럼명세 생성 요청
     * @return 컬럼명세 미리보기 목록
     */
    List<ColumnSpecResponse> selectColumnSpecPreviewList(ColumnSpecRequest request);

    /**
     * DDL 기반 컬럼명세 Excel 파일을 생성한다.
     *
     * @param request 컬럼명세 생성 요청
     * @return xlsx 파일 바이트 배열
     */
    byte[] selectColumnSpecExcel(ColumnSpecRequest request);

    /**
     * 저장된 컬럼명세 목록을 조회한다.
     *
     * @param condition 프로젝트와 테이블명 검색 조건
     * @return 컬럼명세 목록
     */
    List<ColumnSpecResponse> selectColumnSpecList(ColumnSpecSearchCondition condition);
}
