package com.devtrace.manager.artifact.markdown;

import com.devtrace.manager.artifact.dto.TestResultEvidenceRow;
import com.devtrace.manager.artifact.dto.TestResultReportData;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class TestResultReportMarkdownGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String generate(TestResultReportData data) {
        StringBuilder markdown = new StringBuilder();
        appendLine(markdown, "## 테스트 결과 보고서");
        appendLine(markdown, "");
        appendLine(markdown, "- 프로젝트: " + projectName(data));
        appendLine(markdown, "- 기간: " + data.getStartDate() + " ~ " + data.getEndDate());
        appendLine(markdown, "");

        appendSummary(markdown, data);
        appendFailedTests(markdown, data.getEvidences());
        appendIssueResults(markdown, data.getEvidences());
        appendEvidenceFiles(markdown, data.getEvidences());
        return markdown.toString();
    }

    private void appendSummary(StringBuilder markdown, TestResultReportData data) {
        appendLine(markdown, "## 테스트 요약");
        appendLine(markdown, "- 전체 테스트: " + data.getTotalCount());
        appendLine(markdown, "- 성공: " + data.getSuccessCount());
        appendLine(markdown, "- 실패: " + data.getFailCount());
        appendLine(markdown, "- 차단: " + data.getBlockedCount());
        appendLine(markdown, "- 성공률: " + String.format(Locale.ROOT, "%.1f%%", data.getSuccessRate()));
        appendLine(markdown, "");
    }

    private void appendFailedTests(StringBuilder markdown, List<TestResultEvidenceRow> evidences) {
        appendLine(markdown, "## 실패/차단 테스트");
        List<TestResultEvidenceRow> targets = evidences.stream()
                .filter(TestResultEvidenceRow::isFailOrBlocked)
                .toList();
        if (targets.isEmpty()) {
            appendLine(markdown, "- 없음");
        } else {
            targets.forEach(evidence -> appendLine(markdown, "- " + issueKey(evidence) + " - " + evidence.getTestName() + " (" + resultLabel(evidence) + ")"));
        }
        appendLine(markdown, "");
    }

    private void appendIssueResults(StringBuilder markdown, List<TestResultEvidenceRow> evidences) {
        appendLine(markdown, "## 이슈별 테스트 결과");
        if (evidences.isEmpty()) {
            appendLine(markdown, "- 없음");
        } else {
            evidences.forEach(evidence -> appendLine(markdown, "- " + issueKey(evidence) + " - " + evidence.getTestName() + " / " + resultLabel(evidence) + testedAt(evidence)));
        }
        appendLine(markdown, "");
    }

    private void appendEvidenceFiles(StringBuilder markdown, List<TestResultEvidenceRow> evidences) {
        appendLine(markdown, "## 증적 파일 목록");
        List<TestResultEvidenceRow> targets = evidences.stream()
                .filter(TestResultEvidenceRow::hasScreenshot)
                .toList();
        if (targets.isEmpty()) {
            appendLine(markdown, "- 없음");
        } else {
            targets.forEach(evidence -> appendLine(markdown, "- " + issueKey(evidence) + " - " + evidence.getScreenshotFileName() + " - " + evidence.getScreenshotFilePath()));
        }
    }

    private String projectName(TestResultReportData data) {
        if (data.getProject() == null) {
            return "-";
        }
        return data.getProject().getProjectCode() + " - " + data.getProject().getProjectName();
    }

    private String issueKey(TestResultEvidenceRow evidence) {
        return evidence.getIssueKey() == null || evidence.getIssueKey().isBlank() ? "-" : evidence.getIssueKey();
    }

    private String resultLabel(TestResultEvidenceRow evidence) {
        return evidence.getResultStatus() == null ? "-" : evidence.getResultStatus().getLabel();
    }

    private String testedAt(TestResultEvidenceRow evidence) {
        return evidence.getTestedAt() == null ? "" : " (" + DATE_TIME_FORMATTER.format(evidence.getTestedAt()) + ")";
    }

    private void appendLine(StringBuilder markdown, String line) {
        markdown.append(line).append(System.lineSeparator());
    }
}
