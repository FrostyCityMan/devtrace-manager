package com.devtrace.manager.sprint.dto;

import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.issue.dto.IssueStatus;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

/**
 * 스프린트 분석 리포트의 위험 이슈 응답 DTO다.
 *
 * <p>지연, 고우선순위 미완료, 공수 초과 여부를 함께 담아 스프린트 하단의
 * 위험 목록에서 우선 조치 대상을 식별한다.</p>
 */
public class SprintRiskIssueResponse {

    private UUID issueId;
    private String issueKey;
    private String title;
    private IssueStatus status;
    private IssuePriority priority;
    private String assigneeName;
    private LocalDate dueDate;
    private Integer estimatedMinutes;
    private Integer spentMinutes;
    private boolean delayed;
    private boolean highPriority;
    private boolean overEffort;

    /**
     * 이슈 상태 표시명을 반환한다.
     *
     * @return 상태 표시명
     */
    public String getStatusLabel() {
        return status == null ? "-" : status.getLabel();
    }

    /**
     * 이슈 상태 CSS 클래스를 반환한다.
     *
     * @return CSS 클래스명
     */
    public String getStatusCssClass() {
        return status == null ? "planned" : status.getCssClass();
    }

    /**
     * 이슈 우선순위 표시명을 반환한다.
     *
     * @return 우선순위 표시명
     */
    public String getPriorityLabel() {
        return priority == null ? "-" : priority.getLabel();
    }

    /**
     * 이슈 우선순위 CSS 클래스를 반환한다.
     *
     * @return CSS 클래스명
     */
    public String getPriorityCssClass() {
        return priority == null ? "planned" : priority.getCssClass();
    }

    /**
     * 담당자 표시명을 반환한다.
     *
     * @return 담당자 표시명, 없으면 {@code 미지정}
     */
    public String getAssigneeDisplayName() {
        if (assigneeName == null || assigneeName.isBlank()) {
            return "미지정";
        }
        return assigneeName;
    }

    /**
     * 대표 위험 라벨을 반환한다.
     *
     * <p>우선순위는 지연, 공수 초과, 고우선순위 순서로 판단한다.</p>
     *
     * @return 위험 라벨
     */
    public String getRiskLabel() {
        if (delayed) {
            return "지연";
        }
        if (overEffort) {
            return "공수 초과";
        }
        if (highPriority) {
            return "고우선순위";
        }
        return "주의";
    }

    /**
     * 실제/예상 공수 라벨을 반환한다.
     *
     * @return {@code 실제 / 예상} 형식의 공수 라벨
     */
    public String getEffortLabel() {
        return toHoursLabel(spentMinutes) + " / " + toHoursLabel(estimatedMinutes);
    }

    /**
     * nullable 분 단위 공수를 시간 단위 라벨로 변환한다.
     *
     * @param minutes 분 단위 공수
     * @return 시간 단위 표시 라벨
     */
    private String toHoursLabel(Integer minutes) {
        int value = minutes == null ? 0 : minutes;
        if (value % 60 == 0) {
            return (value / 60) + "h";
        }
        return String.format(Locale.ROOT, "%.1fh", value / 60.0);
    }

    /**
     * 이슈 ID를 반환한다.
     *
     * @return 이슈 ID
     */
    public UUID getIssueId() {
        return issueId;
    }

    /**
     * 이슈 ID를 설정한다.
     *
     * @param issueId 이슈 ID
     */
    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    /**
     * 이슈 키를 반환한다.
     *
     * @return 이슈 키
     */
    public String getIssueKey() {
        return issueKey;
    }

    /**
     * 이슈 키를 설정한다.
     *
     * @param issueKey 이슈 키
     */
    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    /**
     * 이슈 제목을 반환한다.
     *
     * @return 이슈 제목
     */
    public String getTitle() {
        return title;
    }

    /**
     * 이슈 제목을 설정한다.
     *
     * @param title 이슈 제목
     */
    public void setTitle(String title) {
        this.title = title;
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
     * 이슈 우선순위를 반환한다.
     *
     * @return 이슈 우선순위
     */
    public IssuePriority getPriority() {
        return priority;
    }

    /**
     * 이슈 우선순위를 설정한다.
     *
     * @param priority 이슈 우선순위
     */
    public void setPriority(IssuePriority priority) {
        this.priority = priority;
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
     * 완료 예정일을 반환한다.
     *
     * @return 완료 예정일
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * 완료 예정일을 설정한다.
     *
     * @param dueDate 완료 예정일
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * 예상 공수를 반환한다.
     *
     * @return 분 단위 예상 공수
     */
    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    /**
     * 예상 공수를 설정한다.
     *
     * @param estimatedMinutes 분 단위 예상 공수
     */
    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    /**
     * 실제 공수를 반환한다.
     *
     * @return 분 단위 실제 공수
     */
    public Integer getSpentMinutes() {
        return spentMinutes;
    }

    /**
     * 실제 공수를 설정한다.
     *
     * @param spentMinutes 분 단위 실제 공수
     */
    public void setSpentMinutes(Integer spentMinutes) {
        this.spentMinutes = spentMinutes;
    }

    /**
     * 지연 여부를 반환한다.
     *
     * @return 지연이면 true
     */
    public boolean isDelayed() {
        return delayed;
    }

    /**
     * 지연 여부를 설정한다.
     *
     * @param delayed 지연 여부
     */
    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }

    /**
     * 고우선순위 미완료 여부를 반환한다.
     *
     * @return 고우선순위 미완료이면 true
     */
    public boolean isHighPriority() {
        return highPriority;
    }

    /**
     * 고우선순위 미완료 여부를 설정한다.
     *
     * @param highPriority 고우선순위 미완료 여부
     */
    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    /**
     * 공수 초과 여부를 반환한다.
     *
     * @return 실제 공수가 예상 공수를 초과했으면 true
     */
    public boolean isOverEffort() {
        return overEffort;
    }

    /**
     * 공수 초과 여부를 설정한다.
     *
     * @param overEffort 공수 초과 여부
     */
    public void setOverEffort(boolean overEffort) {
        this.overEffort = overEffort;
    }
}
