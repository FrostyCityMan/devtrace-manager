package com.devtrace.manager.testevidence.dao;

import com.devtrace.manager.testevidence.dto.TestEvidenceEntity;
import com.devtrace.manager.testevidence.dto.TestEvidenceSearchCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 테스트 증적 저장소에 접근하는 MyBatis DAO입니다.
 *
 * <p>테스트 증적 본문과 스크린샷 메타데이터의 영속화만 담당하며,
 * 파일 저장과 업무 검증은 서비스 계층에서 처리합니다.</p>
 */
public interface TestEvidenceDao {

    /**
     * 테스트 증적을 등록합니다.
     *
     * @param testEvidence 저장할 테스트 증적 엔티티
     */
    void insertTestEvidence(TestEvidenceEntity testEvidence);

    /**
     * 테스트 증적을 수정합니다.
     *
     * @param testEvidence 수정할 테스트 증적 엔티티
     */
    void updateTestEvidence(TestEvidenceEntity testEvidence);

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
     * @return 테스트 증적 엔티티
     */
    Optional<TestEvidenceEntity> selectTestEvidenceDetails(UUID testEvidenceId);

    /**
     * 테스트 증적 목록을 검색합니다.
     *
     * @param condition 프로젝트, 이슈, 판정, 기간 조건
     * @return 테스트 증적 엔티티 목록
     */
    List<TestEvidenceEntity> selectTestEvidenceList(TestEvidenceSearchCondition condition);
}
