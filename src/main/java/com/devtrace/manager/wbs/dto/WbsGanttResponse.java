package com.devtrace.manager.wbs.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class WbsGanttResponse {

    private UUID projectId;
    private LocalDate timelineStartDate;
    private LocalDate timelineEndDate;
    private List<WbsGanttTaskResponse> tasks;

    public int getTaskCount() {
        return safeTasks().size();
    }

    public int getDoneCount() {
        return (int) safeTasks().stream()
                .filter(task -> task.getStatus() != null && task.getStatus().isCompleted())
                .count();
    }

    public int getRiskCount() {
        return (int) safeTasks().stream()
                .filter(task -> task.isDelayed() || task.isOverEffort())
                .count();
    }

    public int getBlockedCount() {
        return (int) safeTasks().stream()
                .filter(WbsGanttTaskResponse::isPredecessorBlocked)
                .count();
    }

    public long getTimelineDays() {
        if (timelineStartDate == null || timelineEndDate == null) {
            return 0;
        }
        return Math.max(1, ChronoUnit.DAYS.between(timelineStartDate, timelineEndDate) + 1);
    }

    private List<WbsGanttTaskResponse> safeTasks() {
        return tasks == null ? List.of() : tasks;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public LocalDate getTimelineStartDate() {
        return timelineStartDate;
    }

    public void setTimelineStartDate(LocalDate timelineStartDate) {
        this.timelineStartDate = timelineStartDate;
    }

    public LocalDate getTimelineEndDate() {
        return timelineEndDate;
    }

    public void setTimelineEndDate(LocalDate timelineEndDate) {
        this.timelineEndDate = timelineEndDate;
    }

    public List<WbsGanttTaskResponse> getTasks() {
        return tasks;
    }

    public void setTasks(List<WbsGanttTaskResponse> tasks) {
        this.tasks = tasks;
    }
}
