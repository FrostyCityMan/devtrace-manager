package com.devtrace.manager.sprint.dto;

import com.devtrace.manager.issue.dto.IssueStatus;

/**
 * 스프린트 이슈의 상태별 분포 응답 DTO다.
 *
 * <p>상태별 건수와 전체 이슈 대비 비율을 함께 제공하여 차트와 진행 현황 표시에서 사용한다.</p>
 */
public class SprintStatusDistributionResponse {

    private IssueStatus status;
    private int issueCount;
    private int issueRate;

    /**
     * 이슈 상태의 화면 표시명을 반환한다.
     *
     * @return 상태 표시명
     */
    public String getStatusLabel() {
        return status == null ? "-" : status.getLabel();
    }

    /**
     * 이슈 상태에 대응하는 화면 CSS 클래스를 반환한다.
     *
     * @return CSS 클래스명
     */
    public String getStatusCssClass() {
        return status == null ? "planned" : status.getCssClass();
    }

    /**
     * 이슈 상태를 반환한다.
     *
     * @return 이슈 상태
     */
    public IssueStatus getStatus() {
        return status;
    }

    /**
     * 이슈 상태를 설정한다.
     *
     * @param status 이슈 상태
     */
    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    /**
     * 해당 상태의 이슈 수를 반환한다.
     *
     * @return 이슈 수
     */
    public int getIssueCount() {
        return issueCount;
    }

    /**
     * 해당 상태의 이슈 수를 설정한다.
     *
     * @param issueCount 이슈 수
     */
    public void setIssueCount(int issueCount) {
        this.issueCount = issueCount;
    }

    /**
     * 전체 이슈 대비 해당 상태의 비율을 반환한다.
     *
     * @return 0~100 사이 비율
     */
    public int getIssueRate() {
        return issueRate;
    }

    /**
     * 전체 이슈 대비 해당 상태의 비율을 설정한다.
     *
     * @param issueRate 0~100 사이 비율
     */
    public void setIssueRate(int issueRate) {
        this.issueRate = issueRate;
    }
}
