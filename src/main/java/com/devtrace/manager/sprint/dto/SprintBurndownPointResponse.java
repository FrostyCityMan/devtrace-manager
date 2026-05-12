package com.devtrace.manager.sprint.dto;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Burndown Chart의 일자별 포인트 응답 DTO다.
 *
 * <p>업무 공수 값과 함께 SVG 차트 표시를 위한 좌표 비율을 포함한다.
 * 공수 값은 내부 기준인 분 단위로 저장하고, 화면 표시용 시간 라벨은 별도 메소드에서 제공한다.</p>
 */
public class SprintBurndownPointResponse {

    private LocalDate snapshotDate;
    private int spentMinutes;
    private int cumulativeSpentMinutes;
    private int idealRemainingMinutes;
    private Integer actualRemainingMinutes;
    private int xPercent;
    private int idealYPercent;
    private Integer actualYPercent;
    private Integer spentYPercent;

    /**
     * 이상 잔여 공수를 시간 라벨로 반환한다.
     *
     * @return 예: {@code 8h}, {@code 1.5h}
     */
    public String getIdealRemainingHoursLabel() {
        return toHoursLabel(idealRemainingMinutes);
    }

    /**
     * 실제 잔여 공수를 시간 라벨로 반환한다.
     *
     * <p>미래 일자처럼 실제 값이 없는 경우 {@code -}를 반환한다.</p>
     *
     * @return 실제 잔여 공수 라벨
     */
    public String getActualRemainingHoursLabel() {
        return actualRemainingMinutes == null ? "-" : toHoursLabel(actualRemainingMinutes);
    }

    /**
     * 스냅샷 기준 누적 투입 공수를 시간 라벨로 반환한다.
     *
     * @return 누적 투입 공수 라벨
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
     * 스냅샷 기준 누적 투입 공수를 반환한다.
     *
     * @return 분 단위 누적 투입 공수
     */
    public int getSpentMinutes() {
        return spentMinutes;
    }

    /**
     * 스냅샷 기준 누적 투입 공수를 설정한다.
     *
     * @param spentMinutes 분 단위 누적 투입 공수
     */
    public void setSpentMinutes(int spentMinutes) {
        this.spentMinutes = spentMinutes;
    }

    /**
     * 스프린트 시작일부터 해당 일자까지의 누적 투입 공수를 반환한다.
     *
     * @return 분 단위 누적 투입 공수
     */
    public int getCumulativeSpentMinutes() {
        return cumulativeSpentMinutes;
    }

    /**
     * 스프린트 시작일부터 해당 일자까지의 누적 투입 공수를 설정한다.
     *
     * @param cumulativeSpentMinutes 분 단위 누적 투입 공수
     */
    public void setCumulativeSpentMinutes(int cumulativeSpentMinutes) {
        this.cumulativeSpentMinutes = cumulativeSpentMinutes;
    }

    /**
     * 이상 잔여 공수를 반환한다.
     *
     * @return 분 단위 이상 잔여 공수
     */
    public int getIdealRemainingMinutes() {
        return idealRemainingMinutes;
    }

    /**
     * 이상 잔여 공수를 설정한다.
     *
     * @param idealRemainingMinutes 분 단위 이상 잔여 공수
     */
    public void setIdealRemainingMinutes(int idealRemainingMinutes) {
        this.idealRemainingMinutes = idealRemainingMinutes;
    }

    /**
     * 실제 잔여 공수를 반환한다.
     *
     * @return 분 단위 실제 잔여 공수, 값이 아직 없으면 null
     */
    public Integer getActualRemainingMinutes() {
        return actualRemainingMinutes;
    }

    /**
     * 실제 잔여 공수를 설정한다.
     *
     * @param actualRemainingMinutes 분 단위 실제 잔여 공수
     */
    public void setActualRemainingMinutes(Integer actualRemainingMinutes) {
        this.actualRemainingMinutes = actualRemainingMinutes;
    }

    /**
     * SVG 차트 X축 좌표 비율을 반환한다.
     *
     * @return 0~100 사이 X축 비율
     */
    public int getXPercent() {
        return xPercent;
    }

    /**
     * SVG 차트 X축 좌표 비율을 설정한다.
     *
     * @param xPercent 0~100 사이 X축 비율
     */
    public void setXPercent(int xPercent) {
        this.xPercent = xPercent;
    }

    /**
     * 이상 잔여 공수의 SVG Y축 좌표 비율을 반환한다.
     *
     * @return 0~100 사이 Y축 비율
     */
    public int getIdealYPercent() {
        return idealYPercent;
    }

    /**
     * 이상 잔여 공수의 SVG Y축 좌표 비율을 설정한다.
     *
     * @param idealYPercent 0~100 사이 Y축 비율
     */
    public void setIdealYPercent(int idealYPercent) {
        this.idealYPercent = idealYPercent;
    }

    /**
     * 실제 잔여 공수의 SVG Y축 좌표 비율을 반환한다.
     *
     * @return 0~100 사이 Y축 비율, 실제 값이 없으면 null
     */
    public Integer getActualYPercent() {
        return actualYPercent;
    }

    /**
     * 실제 잔여 공수의 SVG Y축 좌표 비율을 설정한다.
     *
     * @param actualYPercent 0~100 사이 Y축 비율
     */
    public void setActualYPercent(Integer actualYPercent) {
        this.actualYPercent = actualYPercent;
    }

    /**
     * 누적 실제 공수의 SVG Y축 좌표 비율을 반환한다.
     *
     * @return 0~100 사이 Y축 비율, 스냅샷 값이 없으면 null
     */
    public Integer getSpentYPercent() {
        return spentYPercent;
    }

    /**
     * 누적 실제 공수의 SVG Y축 좌표 비율을 설정한다.
     *
     * @param spentYPercent 0~100 사이 Y축 비율
     */
    public void setSpentYPercent(Integer spentYPercent) {
        this.spentYPercent = spentYPercent;
    }
}
