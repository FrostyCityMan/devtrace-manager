package com.devtrace.manager.artifact.markdown;

import static org.assertj.core.api.Assertions.assertThat;

import com.devtrace.manager.artifact.dto.DailyReportData;
import com.devtrace.manager.artifact.dto.DailyReportIssueRow;
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

class DailyReportMarkdownGeneratorTest {

    private final DailyReportMarkdownGenerator generator = new DailyReportMarkdownGenerator();

    @Test
    void generateDailyReportMarkdown() {
        DailyReportData data = new DailyReportData();
        data.setProject(createProject());
        data.setBaseDate(LocalDate.of(2026, 5, 6));
        data.setIssues(List.of(
                createIssue("DTR-101", "일일 보고서 생성", IssueStatus.IN_PROGRESS, LocalDate.of(2026, 5, 5)),
                createIssue("DTR-102", "테스트 결과 보고서", IssueStatus.DONE, LocalDate.of(2026, 5, 6))
        ));
        data.setWorkLogs(List.of(createWorkLog()));
        data.setVcsLogs(List.of(createVcsLog()));

        String markdown = generator.generate(data);

        assertThat(markdown)
                .contains("## 2026-05-06")
                .contains("- 프로젝트: DTR - DevTrace Manager")
                .contains("## 금일 업무")
                .contains("- DTR-101 - 일일 보고서 Markdown 생성")
                .contains("## 진행 현황")
                .contains("- 전체 이슈: 2")
                .contains("- 완료 이슈: 1")
                .contains("- 진행 중 이슈: 1")
                .contains("- 지연 이슈: 1")
                .contains("## 변경 이력")
                .contains("- GIT abc123")
                .contains("## 특이사항")
                .contains("- 없음")
                .contains("## 익일 업무")
                .contains("## 예상 문제점")
                .contains("- DTR-101 - 일일 보고서 생성");
    }

    private ProjectResponse createProject() {
        ProjectResponse project = new ProjectResponse();
        project.setProjectId(UUID.randomUUID());
        project.setProjectCode("DTR");
        project.setProjectName("DevTrace Manager");
        return project;
    }

    private DailyReportIssueRow createIssue(String issueKey, String title, IssueStatus status, LocalDate dueDate) {
        DailyReportIssueRow issue = new DailyReportIssueRow();
        issue.setIssueKey(issueKey);
        issue.setIssueType(IssueType.FEATURE);
        issue.setTitle(title);
        issue.setStatus(status);
        issue.setPriority(IssuePriority.NORMAL);
        issue.setStartDate(LocalDate.of(2026, 5, 7));
        issue.setDueDate(dueDate);
        return issue;
    }

    private WeeklyReportWorkLogRow createWorkLog() {
        WeeklyReportWorkLogRow workLog = new WeeklyReportWorkLogRow();
        workLog.setIssueKey("DTR-101");
        workLog.setWorkDate(LocalDate.of(2026, 5, 6));
        workLog.setWorkContent("일일 보고서 Markdown 생성");
        workLog.setSpentMinutes(120);
        return workLog;
    }

    private WeeklyReportVcsRow createVcsLog() {
        WeeklyReportVcsRow log = new WeeklyReportVcsRow();
        log.setVcsType(VcsType.GIT);
        log.setRevisionNo("abc123");
        log.setChangedAt(LocalDateTime.of(2026, 5, 6, 10, 0));
        log.setMessage("DTR-101 일일 보고서 생성");
        return log;
    }
}
