package com.devtrace.manager.sprint.dto;

import java.util.Locale;
import java.util.UUID;

/**
 * 스프린트 담당자별 작업량 응답 DTO다.
 *
 * <p>담당자 기준 이슈 수, 예상 공수, 실제 공수를 제공하며,
 * 실제 공수가 예상 공수를 초과했는지 화면에서 바로 판단할 수 있도록 보조 메소드를 포함한다.</p>
 */
public class SprintAssigneeWorkloadResponse {

    private UUID assigneeId;
    private String assigneeName;
    private int issueCount;
    private int estimatedMinutes;
    private int spentMinutes;

    /**
     * 담당자 표시명을 반환한다.
     *
     * <p>담당자가 없거나 표시명이 비어 있으면 {@code 미지정}으로 표시한다.</p>
     *
     * @return 담당자 표시명
     */
    public String getAssigneeDisplayName() {
        if (assigneeName == null || assigneeName.isBlank()) {
            return "미지정";
        }
        return assigneeName;
    }

    /**
     * 예상 공수를 시간 단위 라벨로 반환한다.
     *
     * @return 예상 공수 라벨
     */
    public String getEstimatedHoursLabel() {
        return toHoursLabel(estimatedMinutes);
    }

    /**
     * 실제 공수를 시간 단위 라벨로 반환한다.
     *
     * @return 실제 공수 라벨
     */
    public String getSpentHoursLabel() {
        return toHoursLabel(spentMinutes);
    }

    /**
     * 담당자별 실제 공수가 예상 공수를 초과했는지 반환한다.
     *
     * @return 예상 공수가 있고 실제 공수가 더 크면 true
     */
    public boolean isOverEffort() {
        return estimatedMinutes > 0 && spentMinutes > estimatedMinutes;
    }

    /**
     * 분 단위 공수를 시간 단위 화면 라벨로 변환한다.
     *
     * @param minutes 분 단위 공수
     * @return 시간 단위 표시 라벨
     */
    private String toHoursLabel(int minutes) {
        if (minutes % 60 == 0) {
            return (minutes / 60) + "h";
        }
        return String.format(Locale.ROOT, "%.1fh", minutes / 60.0);
    }

    /**
     * 담당자 ID를 반환한다.
     *
     * @return 담당자 ID
     */
    public UUID getAssigneeId() {
        return assigneeId;
    }

    /**
     * 담당자 ID를 설정한다.
     *
     * @param assigneeId 담당자 ID
     */
    public void setAssigneeId(UUID assigneeId) {
        this.assigneeId = assigneeId;
    }

    /**
     * 담당자명을 반환한다.
     *
     * @return 담당자명
     */
    public String getAssigneeName() {
        return assigneeName;
    }

    /**
     * 담당자명을 설정한다.
     *
     * @param assigneeName 담당자명
     */
    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    /**
     * 담당자에게 배정된 이슈 수를 반환한다.
     *
     * @return 이슈 수
     */
    public int getIssueCount() {
        return issueCount;
    }

    /**
     * 담당자에게 배정된 이슈 수를 설정한다.
     *
     * @param issueCount 이슈 수
     */
    public void setIssueCount(int issueCount) {
        this.issueCount = issueCount;
    }

    /**
     * 담당자에게 배정된 예상 공수 합계를 반환한다.
     *
     * @return 분 단위 예상 공수
     */
    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    /**
     * 담당자에게 배정된 예상 공수 합계를 설정한다.
     *
     * @param estimatedMinutes 분 단위 예상 공수
     */
    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    /**
     * 담당자의 실제 투입 공수 합계를 반환한다.
     *
     * @return 분 단위 실제 공수
     */
    public int getSpentMinutes() {
        return spentMinutes;
    }

    /**
     * 담당자의 실제 투입 공수 합계를 설정한다.
     *
     * @param spentMinutes 분 단위 실제 공수
     */
    public void setSpentMinutes(int spentMinutes) {
        this.spentMinutes = spentMinutes;
    }
}
