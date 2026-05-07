package com.devtrace.manager.issue.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class IssueListSummary {

    private final int totalCount;
    private final int activeCount;
    private final int doneCount;
    private final int urgentCount;
    private final int delayedCount;
    private final int estimatedMinutes;
    private final int spentMinutes;

    private IssueListSummary(
            int totalCount,
            int activeCount,
            int doneCount,
            int urgentCount,
            int delayedCount,
            int estimatedMinutes,
            int spentMinutes
    ) {
        this.totalCount = totalCount;
        this.activeCount = activeCount;
        this.doneCount = doneCount;
        this.urgentCount = urgentCount;
        this.delayedCount = delayedCount;
        this.estimatedMinutes = estimatedMinutes;
        this.spentMinutes = spentMinutes;
    }

    /**
     * 이슈 목록에서 상태별 요약 수치를 집계합니다.
     *
     * @param issues 이슈 목록
     * @return 이슈 목록 요약
     */
    public static IssueListSummary from(List<IssueResponse> issues) {
        LocalDate today = LocalDate.now();
        int totalCount = issues.size();
        int activeCount = 0;
        int doneCount = 0;
        int urgentCount = 0;
        int delayedCount = 0;
        int estimatedMinutes = 0;
        int spentMinutes = 0;

        for (IssueResponse issue : issues) {
            if (issue.getStatus() != null && issue.getStatus().isActive()) {
                activeCount++;
            }
            if (issue.getStatus() != null && issue.getStatus().isCompleted()) {
                doneCount++;
            }
            if (issue.getPriority() == IssuePriority.URGENT) {
                urgentCount++;
            }
            if (isDelayed(issue, today)) {
                delayedCount++;
            }
            estimatedMinutes += valueOrZero(issue.getEstimatedMinutes());
            spentMinutes += valueOrZero(issue.getSpentMinutes());
        }

        return new IssueListSummary(
                totalCount,
                activeCount,
                doneCount,
                urgentCount,
                delayedCount,
                estimatedMinutes,
                spentMinutes
        );
    }

    private static boolean isDelayed(IssueResponse issue, LocalDate today) {
        return issue.getDueDate() != null
                && issue.getDueDate().isBefore(today)
                && issue.getStatus() != null
                && !issue.getStatus().isCompleted();
    }

    private static int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    public String getEstimatedHoursLabel() {
        return formatHours(estimatedMinutes);
    }

    public String getSpentHoursLabel() {
        return formatHours(spentMinutes);
    }

    private String formatHours(int minutes) {
        return String.format(Locale.ROOT, "%.1fh", minutes / 60.0);
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public int getDoneCount() {
        return doneCount;
    }

    public int getUrgentCount() {
        return urgentCount;
    }

    public int getDelayedCount() {
        return delayedCount;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public int getSpentMinutes() {
        return spentMinutes;
    }
}
