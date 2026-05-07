package com.devtrace.manager.artifact.service;

import com.devtrace.manager.artifact.dto.ArtifactFileResponse;
import com.devtrace.manager.artifact.dto.ArtifactHistoryResponse;
import com.devtrace.manager.artifact.dto.ArtifactMarkdownResponse;
import com.devtrace.manager.artifact.dto.ArtifactRequest;
import com.devtrace.manager.artifact.dto.ArtifactSearchCondition;
import java.util.List;

/**
 * 산출물 생성과 생성 이력 조회를 담당하는 서비스 계약입니다.
 *
 * <p>현재 산출물은 주간 업무보고, 일일 업무보고, 테스트 결과 보고서를 중심으로 하며,
 * 입력 데이터는 프로젝트, 이슈, 공수, 형상관리 변경이력, 테스트 증적에서 조합됩니다.</p>
 */
public interface ArtifactService {

    /**
     * 주간 업무보고 Markdown 미리보기 데이터를 생성합니다.
     *
     * @param request 프로젝트와 기간 조건
     * @return 생성된 Markdown 본문과 집계 요약
     */
    ArtifactMarkdownResponse selectWeeklyReportPreviewDetails(ArtifactRequest request);

    /**
     * 주간 업무보고 Markdown을 생성하고 산출물 이력을 저장합니다.
     *
     * @param request 프로젝트와 기간 조건
     * @return 다운로드 가능한 Markdown 응답
     */
    ArtifactMarkdownResponse insertWeeklyReportMarkdown(ArtifactRequest request);

    /**
     * 일일 업무보고 Markdown 미리보기 데이터를 생성합니다.
     *
     * @param request 프로젝트와 기준일 조건
     * @return 생성된 Markdown 본문과 당일 업무 요약
     */
    ArtifactMarkdownResponse selectDailyReportPreviewDetails(ArtifactRequest request);

    /**
     * 일일 업무보고 Markdown을 생성하고 산출물 이력을 저장합니다.
     *
     * @param request 프로젝트와 기준일 조건
     * @return 다운로드 가능한 Markdown 응답
     */
    ArtifactMarkdownResponse insertDailyReportMarkdown(ArtifactRequest request);

    /**
     * 테스트 결과 보고서 Markdown 미리보기 데이터를 생성합니다.
     *
     * @param request 프로젝트, 기간, 이슈, 판정 필터 조건
     * @return 생성된 Markdown 본문과 테스트 결과 집계
     */
    ArtifactMarkdownResponse selectTestResultReportPreviewDetails(ArtifactRequest request);

    /**
     * 테스트 결과 보고서 Markdown을 생성하고 산출물 이력을 저장합니다.
     *
     * @param request 프로젝트, 기간, 이슈, 판정 필터 조건
     * @return 다운로드 가능한 Markdown 응답
     */
    ArtifactMarkdownResponse insertTestResultReportMarkdown(ArtifactRequest request);

    /**
     * 테스트 결과 보고서 Excel 파일을 생성하고 산출물 이력을 저장합니다.
     *
     * @param request 프로젝트, 기간, 이슈, 판정 필터 조건
     * @return 다운로드 파일명, MIME 타입, 바이너리 본문
     */
    ArtifactFileResponse insertTestResultReportExcel(ArtifactRequest request);

    /**
     * 산출물 생성 이력 목록을 조회합니다.
     *
     * @param condition 프로젝트와 산출물 유형 검색 조건
     * @return 산출물 생성 이력 목록
     */
    List<ArtifactHistoryResponse> selectArtifactHistoryList(ArtifactSearchCondition condition);
}
