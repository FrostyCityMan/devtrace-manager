package com.devtrace.manager.sprint.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

/**
 * 스프린트 일자별 스냅샷 조회 응답 DTO다.
 *
 * <p>저장된 스냅샷을 화면, API, Burndown 계산 로직에 전달하며, 분 단위 공수를
 * 시간 단위 라벨로 표시하는 보조 메소드를 제공한다.</p>
 */
public class SprintDailySnapshotResponse {

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
     * 전체 예상 공수를 시간 라벨로 반환한다.
     *
     * @return 전체 예상 공수 라벨
     */
    public String getTotalEstimatedHoursLabel() {
        return toHoursLabel(totalEstimatedMinutes);
    }

    /**
     * 완료 예상 공수를 시간 라벨로 반환한다.
     *
     * @return 완료 예상 공수 라벨
     */
    public String getDoneEstimatedHoursLabel() {
        return toHoursLabel(doneEstimatedMinutes);
    }

    /**
     * 잔여 예상 공수를 시간 라벨로 반환한다.
     *
     * @return 잔여 예상 공수 라벨
     */
    public String getRemainingEstimatedHoursLabel() {
        return toHoursLabel(remainingEstimatedMinutes);
    }

    /**
     * 누적 실제 공수를 시간 라벨로 반환한다.
     *
     * @return 누적 실제 공수 라벨
     */
    public String getSpentHoursLabel() {
        return toHoursLabel(spentMinutes);
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
     * 생성 일시를 반환한다.
     *
     * @return 생성 일시
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 생성 일시를 설정한다.
     *
     * @param createdAt 생성 일시
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
