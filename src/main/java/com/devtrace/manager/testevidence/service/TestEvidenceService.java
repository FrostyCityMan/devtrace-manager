package com.devtrace.manager.testevidence.service;

import com.devtrace.manager.testevidence.dto.TestEvidenceRequest;
import com.devtrace.manager.testevidence.dto.TestEvidenceResponse;
import com.devtrace.manager.testevidence.dto.TestEvidenceScreenshotResponse;
import com.devtrace.manager.testevidence.dto.TestEvidenceSearchCondition;
import java.util.List;
import java.util.UUID;

/**
 * 기능 테스트 증적의 등록, 조회, 수정, 삭제를 담당하는 서비스 계약입니다.
 *
 * <p>테스트 증적은 테스트 결과 보고서의 원천 데이터이므로 프로젝트, 이슈, 판정,
 * 수행 일시, 스크린샷 메타데이터를 일관된 단위로 관리합니다.</p>
 */
public interface TestEvidenceService {

    /**
     * 테스트 증적을 등록합니다.
     *
     * @param request 테스트 증적 등록 요청
     * @return 등록된 테스트 증적
     */
    TestEvidenceResponse insertTestEvidence(TestEvidenceRequest request);

    /**
     * 테스트 증적을 수정합니다.
     *
     * @param testEvidenceId 테스트 증적 ID
     * @param request 테스트 증적 수정 요청
     * @return 수정된 테스트 증적
     */
    TestEvidenceResponse updateTestEvidence(UUID testEvidenceId, TestEvidenceRequest request);

    /**
     * 테스트 증적을 삭제합니다.
     *
     * @param testEvidenceId 테스트 증적 ID
     */
    void deleteTestEvidence(UUID testEvidenceId);

    /**
     * 테스트 증적 상세 정보를 조회합니다.
     *
     * @param testEvidenceId 테스트 증적 ID
     * @return 테스트 증적 상세 정보
     */
    TestEvidenceResponse selectTestEvidenceDetails(UUID testEvidenceId);

    /**
     * 테스트 증적 목록을 검색합니다.
     *
     * @param condition 프로젝트, 이슈, 판정, 기간 검색 조건
     * @return 테스트 증적 목록
     */
    List<TestEvidenceResponse> selectTestEvidenceList(TestEvidenceSearchCondition condition);

    /**
     * 테스트 증적에 연결된 스크린샷 다운로드 정보를 조회합니다.
     *
     * @param testEvidenceId 테스트 증적 ID
     * @return 스크린샷 파일명, 경로, MIME 타입
     */
    TestEvidenceScreenshotResponse selectTestEvidenceScreenshotDetails(UUID testEvidenceId);
}
