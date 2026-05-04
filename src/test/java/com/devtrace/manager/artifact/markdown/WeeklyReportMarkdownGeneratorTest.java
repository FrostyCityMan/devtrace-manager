package com.devtrace.manager.artifact.markdown;

import static org.assertj.core.api.Assertions.assertThat;

import com.devtrace.manager.artifact.dto.WeeklyReportData;
import com.devtrace.manager.artifact.dto.WeeklyReportIssueRow;
import com.devtrace.manager.artifact.dto.WeeklyReportVcsRow;
import com.devtrace.manager.artifact.dto.WeeklyReportWorkLogRow;
import com.devtrace.manager.issue.dto.IssuePriority;
import com.devtrace.manager.issue.dto.IssueStatus;
import com.devtrace.manager.issue.dto.IssueType;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.vcs.dto.VcsType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class WeeklyReportMarkdownGeneratorTest {

    private final WeeklyReportMarkdownGenerator generator = new WeeklyReportMarkdownGenerator();

    @Test
    void generateWeeklyReportMarkdown() {
        WeeklyReportData data = new WeeklyReportData();
        data.setProject(createProject());
        data.setStartDate(LocalDate.of(2026, 4, 27));
        data.setEndDate(LocalDate.of(2026, 5, 3));
        data.setIssues(List.of(
                createIssue("DTR-101", "주간 업무보고 생성", IssueStatus.IN_PROGRESS, LocalDate.of(2026, 5, 1), 480),
                createIssue("DTR-102", "작업 공수 집계", IssueStatus.DONE, LocalDate.of(2026, 5, 2), 120)
        ));
        data.setWorkLogs(List.of(createWorkLog("DTR-101", "Markdown 생성기 구현", 120)));
        data.setVcsLogs(List.of(createVcsLog()));

        String markdown = generator.generate(data);

        assertThat(markdown)
                .contains("## 2026-04-27 ~ 2026-05-03")
                .contains("- 프로젝트: DTR - DevTrace Manager")
                .contains("## 금주 배정 업무")
                .contains("- DTR-101 - 주간 업무보고 생성 (진행 중)")
                .contains("## 금주 수행 업무")
                .contains("- 완료: DTR-102 - 작업 공수 집계")
                .contains("- DTR-101 - Markdown 생성기 구현 (2.0h)")
                .contains("## 이슈 처리 현황")
                .contains("- 전체: 2")
                .contains("- 완료: 1")
                .contains("- 진행 중: 1")
                .contains("- 지연: 1")
                .contains("## 공수 현황")
                .contains("- 예상 공수: 10.0h")
                .contains("- 실제 공수: 2.0h")
                .contains("- 차이: -8.0h")
                .contains("## 변경 이력")
                .contains("- GIT abc123 - [DTR-101] DTR-101 산출물 생성 기능 추가")
                .contains("## 차주 업무계획")
                .contains("- DTR-101 - 주간 업무보고 생성")
                .contains("## 예상 문제점")
                .contains("- DTR-101 - 주간 업무보고 생성 (진행 중)");
    }

    private ProjectResponse createProject() {
        ProjectResponse project = new ProjectResponse();
        project.setProjectId(UUID.randomUUID());
        project.setProjectCode("DTR");
        project.setProjectName("DevTrace Manager");
        return project;
    }

    private WeeklyReportIssueRow createIssue(String issueKey, String title, IssueStatus status, LocalDate dueDate, int estimatedMinutes) {
        WeeklyReportIssueRow issue = new WeeklyReportIssueRow();
        issue.setIssueKey(issueKey);
        issue.setIssueType(IssueType.FEATURE);
        issue.setTitle(title);
        issue.setStatus(status);
        issue.setPriority(IssuePriority.NORMAL);
        issue.setDueDate(dueDate);
        issue.setEstimatedMinutes(estimatedMinutes);
        return issue;
    }

    private WeeklyReportWorkLogRow createWorkLog(String issueKey, String workContent, int spentMinutes) {
        WeeklyReportWorkLogRow workLog = new WeeklyReportWorkLogRow();
        workLog.setIssueKey(issueKey);
        workLog.setIssueTitle("주간 업무보고 생성");
        workLog.setWorkDate(LocalDate.of(2026, 4, 29));
        workLog.setWorkContent(workContent);
        workLog.setSpentMinutes(spentMinutes);
        return workLog;
    }

    private WeeklyReportVcsRow createVcsLog() {
        WeeklyReportVcsRow log = new WeeklyReportVcsRow();
        log.setVcsType(VcsType.GIT);
        log.setRevisionNo("abc123");
        log.setAuthor("admin");
        log.setChangedAt(LocalDateTime.of(2026, 5, 1, 10, 0));
        log.setIssueKeyText("DTR-101");
        log.setMessage("DTR-101 산출물 생성 기능 추가");
        return log;
    }
}
