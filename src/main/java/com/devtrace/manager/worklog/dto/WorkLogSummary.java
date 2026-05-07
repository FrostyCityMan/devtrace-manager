package com.devtrace.manager.worklog.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class WorkLogSummary {

    private final int totalCount;
    private final int totalMinutes;
    private final LocalDate latestWorkDate;

    private WorkLogSummary(int totalCount, int totalMinutes, LocalDate latestWorkDate) {
        this.totalCount = totalCount;
        this.totalMinutes = totalMinutes;
        this.latestWorkDate = latestWorkDate;
    }

    /**
     * 작업 공수 목록에서 건수, 총 공수, 최근 작업일을 집계합니다.
     *
     * @param workLogs 작업 공수 목록
     * @return 작업 공수 요약
     */
    public static WorkLogSummary from(List<WorkLogResponse> workLogs) {
        int totalMinutes = 0;
        LocalDate latestWorkDate = null;

        for (WorkLogResponse workLog : workLogs) {
            totalMinutes += workLog.getSpentMinutes() == null ? 0 : workLog.getSpentMinutes();
            if (workLog.getWorkDate() != null
                    && (latestWorkDate == null || workLog.getWorkDate().isAfter(latestWorkDate))) {
                latestWorkDate = workLog.getWorkDate();
            }
        }

        return new WorkLogSummary(workLogs.size(), totalMinutes, latestWorkDate);
    }

    public String getTotalHoursLabel() {
        return String.format(Locale.ROOT, "%.1fh", totalMinutes / 60.0);
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public LocalDate getLatestWorkDate() {
        return latestWorkDate;
    }
}
