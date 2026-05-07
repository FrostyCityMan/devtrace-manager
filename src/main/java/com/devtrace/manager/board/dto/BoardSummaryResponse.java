package com.devtrace.manager.board.dto;

import java.util.List;

public class BoardSummaryResponse {

    private final int totalCount;
    private final int activeCount;
    private final int doneCount;
    private final int delayedCount;
    private final int unassignedCount;

    private BoardSummaryResponse(int totalCount, int activeCount, int doneCount, int delayedCount, int unassignedCount) {
        this.totalCount = totalCount;
        this.activeCount = activeCount;
        this.doneCount = doneCount;
        this.delayedCount = delayedCount;
        this.unassignedCount = unassignedCount;
    }

    /**
     * 보드 컬럼 목록을 기준으로 카드 수, 지연 수, 공수 합계를 집계합니다.
     *
     * @param columns 보드 컬럼 목록
     * @return 보드 요약 응답
     */
    public static BoardSummaryResponse from(List<BoardColumnResponse> columns) {
        List<BoardColumnResponse> safeColumns = columns == null ? List.of() : columns;
        int totalCount = 0;
        int activeCount = 0;
        int doneCount = 0;
        int delayedCount = 0;
        int unassignedCount = 0;

        for (BoardColumnResponse column : safeColumns) {
            List<BoardIssueCardResponse> issues = column.getIssues() == null ? List.of() : column.getIssues();
            totalCount += issues.size();
            for (BoardIssueCardResponse issue : issues) {
                if (issue.getStatus() != null && issue.getStatus().isActive()) {
                    activeCount++;
                }
                if (issue.getStatus() != null && issue.getStatus().isCompleted()) {
                    doneCount++;
                }
                if (issue.isDelayed()) {
                    delayedCount++;
                }
                if (issue.getAssigneeId() == null) {
                    unassignedCount++;
                }
            }
        }

        return new BoardSummaryResponse(totalCount, activeCount, doneCount, delayedCount, unassignedCount);
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

    public int getDelayedCount() {
        return delayedCount;
    }

    public int getUnassignedCount() {
        return unassignedCount;
    }
}
