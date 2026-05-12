package com.devtrace.manager.sprint.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 스프린트 일자별 진행 스냅샷 엔티티다.
 *
 * <p>{@code SPRINT_DAILY_SNAPSHOT} 테이블과 1:1로 대응하며, 특정 일자에 스프린트의
 * 이슈 수, 완료 수, 잔여 예상 공수, 누적 실제 공수를 고정한다. 이 값은 Burndown Chart의
 * 실제선과 공수선 산정 기준으로 사용한다.</p>
 */
public class SprintDailySnapshotEntity {

    private UUID snapshotId;
    private UUID sprintId;
    private LocalDate snapshotDate;
    private int totalIssueCount;
    private int doneIssueCount;
    private int remainingIssueCount;
    private int totalEstimatedMinutes;
    private int doneEstimatedMinutes;
    private int remainingEstimatedMinutes;
    private int spentMinutes;
    private LocalDateTime createdAt;

    /**
     * 화면/API 응답 DTO로 변환한다.
     *
     * @return 스프린트 일자별 스냅샷 응답
     */
    public SprintDailySnapshotResponse toResponse() {
        SprintDailySnapshotResponse response = new SprintDailySnapshotResponse();
        response.setSnapshotId(snapshotId);
        response.setSprintId(sprintId);
        response.setSnapshotDate(snapshotDate);
        response.setTotalIssueCount(totalIssueCount);
        response.setDoneIssueCount(doneIssueCount);
        response.setRemainingIssueCount(remainingIssueCount);
        response.setTotalEstimatedMinutes(totalEstimatedMinutes);
        response.setDoneEstimatedMinutes(doneEstimatedMinutes);
        response.setRemainingEstimatedMinutes(remainingEstimatedMinutes);
        response.setSpentMinutes(spentMinutes);
        response.setCreatedAt(createdAt);
        return response;
    }

    /**
     * 스냅샷 ID를 반환한다.
     *
     * @return 스냅샷 ID
     */
    public UUID getSnapshotId() {
        return snapshotId;
    }

    /**
     * 스냅샷 ID를 설정한다.
     *
     * @param snapshotId 스냅샷 ID
     */
    public void setSnapshotId(UUID snapshotId) {
        this.snapshotId = snapshotId;
    }

    /**
     * 스프린트 ID를 반환한다.
     *
     * @return 스프린트 ID
     */
    public UUID getSprintId() {
        return sprintId;
    }

    /**
     * 스프린트 ID를 설정한다.
     *
     * @param sprintId 스프린트 ID
     */
    public void setSprintId(UUID sprintId) {
        this.sprintId = sprintId;
    }

    /**
     * 스냅샷 일자를 반환한다.
     *
     * @return 스냅샷 일자
     */
    public LocalDate getSnapshotDate() {
        return snapshotDate;
    }

    /**
     * 스냅샷 일자를 설정한다.
     *
     * @param snapshotDate 스냅샷 일자
     */
    public void setSnapshotDate(LocalDate snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    /**
     * 전체 이슈 수를 반환한다.
     *
     * @return 전체 이슈 수
     */
    public int getTotalIssueCount() {
        return totalIssueCount;
    }

    /**
     * 전체 이슈 수를 설정한다.
     *
     * @param totalIssueCount 전체 이슈 수
     */
    public void setTotalIssueCount(int totalIssueCount) {
        this.totalIssueCount = totalIssueCount;
    }

    /**
     * 완료 이슈 수를 반환한다.
     *
     * @return 완료 이슈 수
     */
    public int getDoneIssueCount() {
        return doneIssueCount;
    }

    /**
     * 완료 이슈 수를 설정한다.
     *
     * @param doneIssueCount 완료 이슈 수
     */
    public void setDoneIssueCount(int doneIssueCount) {
        this.doneIssueCount = doneIssueCount;
    }

    /**
     * 잔여 이슈 수를 반환한다.
     *
     * @return 잔여 이슈 수
     */
    public int getRemainingIssueCount() {
        return remainingIssueCount;
    }

    /**
     * 잔여 이슈 수를 설정한다.
     *
     * @param remainingIssueCount 잔여 이슈 수
     */
    public void setRemainingIssueCount(int remainingIssueCount) {
        this.remainingIssueCount = remainingIssueCount;
    }

    /**
     * 전체 예상 공수를 반환한다.
     *
     * @return 분 단위 전체 예상 공수
     */
    public int getTotalEstimatedMinutes() {
        return totalEstimatedMinutes;
    }

    /**
     * 전체 예상 공수를 설정한다.
     *
     * @param totalEstimatedMinutes 분 단위 전체 예상 공수
     */
    public void setTotalEstimatedMinutes(int totalEstimatedMinutes) {
        this.totalEstimatedMinutes = totalEstimatedMinutes;
    }

    /**
     * 완료 이슈 예상 공수를 반환한다.
     *
     * @return 분 단위 완료 이슈 예상 공수
     */
    public int getDoneEstimatedMinutes() {
        return doneEstimatedMinutes;
    }

    /**
     * 완료 이슈 예상 공수를 설정한다.
     *
     * @param doneEstimatedMinutes 분 단위 완료 이슈 예상 공수
     */
    public void setDoneEstimatedMinutes(int doneEstimatedMinutes) {
        this.doneEstimatedMinutes = doneEstimatedMinutes;
    }

    /**
     * 잔여 예상 공수를 반환한다.
     *
     * @return 분 단위 잔여 예상 공수
     */
    public int getRemainingEstimatedMinutes() {
        return remainingEstimatedMinutes;
    }

    /**
     * 잔여 예상 공수를 설정한다.
     *
     * @param remainingEstimatedMinutes 분 단위 잔여 예상 공수
     */
    public void setRemainingEstimatedMinutes(int remainingEstimatedMinutes) {
        this.remainingEstimatedMinutes = remainingEstimatedMinutes;
    }

    /**
     * 누적 실제 공수를 반환한다.
     *
     * @return 분 단위 누적 실제 공수
     */
    public int getSpentMinutes() {
        return spentMinutes;
    }

    /**
     * 누적 실제 공수를 설정한다.
     *
     * @param spentMinutes 분 단위 누적 실제 공수
     */
    public void setSpentMinutes(int spentMinutes) {
        this.spentMinutes = spentMinutes;
    }

    /**
     * 스냅샷 생성 일시를 반환한다.
     *
     * @return 생성 일시
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 스냅샷 생성 일시를 설정한다.
     *
     * @param createdAt 생성 일시
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
