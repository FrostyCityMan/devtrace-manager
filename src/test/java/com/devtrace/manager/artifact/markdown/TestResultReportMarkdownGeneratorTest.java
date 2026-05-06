package com.devtrace.manager.artifact.markdown;

import static org.assertj.core.api.Assertions.assertThat;

import com.devtrace.manager.artifact.dto.TestResultEvidenceRow;
import com.devtrace.manager.artifact.dto.TestResultReportData;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.testevidence.dto.TestEvidenceResult;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TestResultReportMarkdownGeneratorTest {

    private final TestResultReportMarkdownGenerator generator = new TestResultReportMarkdownGenerator();

    @Test
    void generateTestResultReportMarkdown() {
        TestResultReportData data = new TestResultReportData();
        data.setProject(createProject());
        data.setStartDate(LocalDate.of(2026, 5, 1));
        data.setEndDate(LocalDate.of(2026, 5, 6));
        data.setEvidences(List.of(
                createEvidence("DTR-101", "로그인 성공", TestEvidenceResult.SUCCESS, null),
                createEvidence("DTR-102", "권한 오류", TestEvidenceResult.FAIL, "fail.png")
        ));

        String markdown = generator.generate(data);

        assertThat(markdown)
                .contains("## 테스트 결과 보고서")
                .contains("- 프로젝트: DTR - DevTrace Manager")
                .contains("- 전체 테스트: 2")
                .contains("- 성공: 1")
                .contains("- 실패: 1")
                .contains("- 성공률: 50.0%")
                .contains("## 실패/차단 테스트")
                .contains("- DTR-102 - 권한 오류 (실패)")
                .contains("## 이슈별 테스트 결과")
                .contains("- DTR-101 - 로그인 성공 / 성공")
                .contains("## 증적 파일 목록")
                .contains("- DTR-102 - fail.png - uploads/test-evidences/fail.png");
    }

    private ProjectResponse createProject() {
        ProjectResponse project = new ProjectResponse();
        project.setProjectId(UUID.randomUUID());
        project.setProjectCode("DTR");
        project.setProjectName("DevTrace Manager");
        return project;
    }

    private TestResultEvidenceRow createEvidence(String issueKey, String testName, TestEvidenceResult result, String screenshotFileName) {
        TestResultEvidenceRow evidence = new TestResultEvidenceRow();
        evidence.setTestEvidenceId(UUID.randomUUID());
        evidence.setProjectId(UUID.randomUUID());
        evidence.setIssueId(UUID.randomUUID());
        evidence.setIssueKey(issueKey);
        evidence.setTestName(testName);
        evidence.setResultStatus(result);
        evidence.setTestedAt(LocalDateTime.of(2026, 5, 6, 10, 0));
        evidence.setScreenshotFileName(screenshotFileName);
        evidence.setScreenshotFilePath(screenshotFileName == null ? null : "uploads/test-evidences/" + screenshotFileName);
        return evidence;
    }
}
