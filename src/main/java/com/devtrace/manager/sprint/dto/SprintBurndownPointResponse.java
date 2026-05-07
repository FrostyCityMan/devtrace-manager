package com.devtrace.manager.sprint.dto;

import java.time.LocalDate;
import java.util.Locale;

public class SprintBurndownPointResponse {

    private LocalDate snapshotDate;
    private int spentMinutes;
    private int cumulativeSpentMinutes;
    private int idealRemainingMinutes;
    private Integer actualRemainingMinutes;
    private int xPercent;
    private int idealYPercent;
    private Integer actualYPercent;

    public String getIdealRemainingHoursLabel() {
        return toHoursLabel(idealRemainingMinutes);
    }

    public String getActualRemainingHoursLabel() {
        return actualRemainingMinutes == null ? "-" : toHoursLabel(actualRemainingMinutes);
    }

    public String getSpentHoursLabel() {
        return toHoursLabel(spentMinutes);
    }

    private String toHoursLabel(int minutes) {
        if (minutes % 60 == 0) {
            return (minutes / 60) + "h";
        }
        return String.format(Locale.ROOT, "%.1fh", minutes / 60.0);
    }

    public LocalDate getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(LocalDate snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public int getSpentMinutes() {
        return spentMinutes;
    }

    public void setSpentMinutes(int spentMinutes) {
        this.spentMinutes = spentMinutes;
    }

    public int getCumulativeSpentMinutes() {
        return cumulativeSpentMinutes;
    }

    public void setCumulativeSpentMinutes(int cumulativeSpentMinutes) {
        this.cumulativeSpentMinutes = cumulativeSpentMinutes;
    }

    public int getIdealRemainingMinutes() {
        return idealRemainingMinutes;
    }

    public void setIdealRemainingMinutes(int idealRemainingMinutes) {
        this.idealRemainingMinutes = idealRemainingMinutes;
    }

    public Integer getActualRemainingMinutes() {
        return actualRemainingMinutes;
    }

    public void setActualRemainingMinutes(Integer actualRemainingMinutes) {
        this.actualRemainingMinutes = actualRemainingMinutes;
    }

    public int getXPercent() {
        return xPercent;
    }

    public void setXPercent(int xPercent) {
        this.xPercent = xPercent;
    }

    public int getIdealYPercent() {
        return idealYPercent;
    }

    public void setIdealYPercent(int idealYPercent) {
        this.idealYPercent = idealYPercent;
    }

    public Integer getActualYPercent() {
        return actualYPercent;
    }

    public void setActualYPercent(Integer actualYPercent) {
        this.actualYPercent = actualYPercent;
    }
}
