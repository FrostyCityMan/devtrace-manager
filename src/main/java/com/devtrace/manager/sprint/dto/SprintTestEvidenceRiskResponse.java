package com.devtrace.manager.sprint.dto;

import com.devtrace.manager.testevidence.dto.TestEvidenceResult;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 스프린트 이슈와 연결된 실패/차단 테스트 증적 응답 DTO다.
 *
 * <p>스프린트 분석 화면에서 테스트 품질 위험을 함께 판단하기 위해 사용한다.</p>
 */
public class SprintTestEvidenceRiskResponse {

    private UUID testEvidenceId;
    private UUID issueId;
    private String issueKey;
    private String testName;
    private String testTarget;
    private TestEvidenceResult resultStatus;
    private String testerName;
    private LocalDateTime testedAt;

    /**
     * 테스트 판정 표시명을 반환한다.
     *
     * @return 판정 표시명
     */
    public String getResultLabel() {
        return resultStatus == null ? "-" : resultStatus.getLabel();
    }

    /**
     * 테스트 판정 CSS 클래스를 반환한다.
     *
     * @return CSS 클래스명
     */
    public String getResultCssClass() {
        return resultStatus == null ? "planned" : resultStatus.getCssClass();
    }

    /**
     * 테스트 증적 ID를 반환한다.
     *
     * @return 테스트 증적 ID
     */
    public UUID getTestEvidenceId() {
        return testEvidenceId;
    }

    /**
     * 테스트 증적 ID를 설정한다.
     *
     * @param testEvidenceId 테스트 증적 ID
     */
    public void setTestEvidenceId(UUID testEvidenceId) {
        this.testEvidenceId = testEvidenceId;
    }

    /**
     * 연결된 이슈 ID를 반환한다.
     *
     * @return 이슈 ID
     */
    public UUID getIssueId() {
        return issueId;
    }

    /**
     * 연결된 이슈 ID를 설정한다.
     *
     * @param issueId 이슈 ID
     */
    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    /**
     * 연결된 이슈 키를 반환한다.
     *
     * @return 이슈 키
     */
    public String getIssueKey() {
        return issueKey;
    }

    /**
     * 연결된 이슈 키를 설정한다.
     *
     * @param issueKey 이슈 키
     */
    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    /**
     * 테스트명을 반환한다.
     *
     * @return 테스트명
     */
    public String getTestName() {
        return testName;
    }

    /**
     * 테스트명을 설정한다.
     *
     * @param testName 테스트명
     */
    public void setTestName(String testName) {
        this.testName = testName;
    }

    /**
     * 테스트 대상 URL 또는 화면명을 반환한다.
     *
     * @return 테스트 대상
     */
    public String getTestTarget() {
        return testTarget;
    }

    /**
     * 테스트 대상 URL 또는 화면명을 설정한다.
     *
     * @param testTarget 테스트 대상
     */
    public void setTestTarget(String testTarget) {
        this.testTarget = testTarget;
    }

    /**
     * 테스트 판정을 반환한다.
     *
     * @return 테스트 판정
     */
    public TestEvidenceResult getResultStatus() {
        return resultStatus;
    }

    /**
     * 테스트 판정을 설정한다.
     *
     * @param resultStatus 테스트 판정
     */
    public void setResultStatus(TestEvidenceResult resultStatus) {
        this.resultStatus = resultStatus;
    }

    /**
     * 테스트 수행자명을 반환한다.
     *
     * @return 테스트 수행자명
     */
    public String getTesterName() {
        return testerName;
    }

    /**
     * 테스트 수행자명을 설정한다.
     *
     * @param testerName 테스트 수행자명
     */
    public void setTesterName(String testerName) {
        this.testerName = testerName;
    }

    /**
     * 테스트 수행일시를 반환한다.
     *
     * @return 테스트 수행일시
     */
    public LocalDateTime getTestedAt() {
        return testedAt;
    }

    /**
     * 테스트 수행일시를 설정한다.
     *
     * @param testedAt 테스트 수행일시
     */
    public void setTestedAt(LocalDateTime testedAt) {
        this.testedAt = testedAt;
    }
}
