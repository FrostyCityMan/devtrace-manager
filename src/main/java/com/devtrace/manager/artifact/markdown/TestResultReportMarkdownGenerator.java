package com.devtrace.manager.artifact.markdown;

import com.devtrace.manager.artifact.dto.TestResultEvidenceRow;
import com.devtrace.manager.artifact.dto.TestResultReportData;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * 테스트 결과 보고서 Markdown 문서를 생성합니다.
 *
 * <p>테스트 요약, 실패/차단 항목, 이슈별 결과, 증적 파일 목록을 Markdown 형식으로
 * 정리하여 SI 제출용 테스트 결과 보고서의 초안을 만듭니다.</p>
 */
@Component
public class TestResultReportMarkdownGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 테스트 결과 보고서 Markdown 본문을 생성합니다.
     *
     * @param data 테스트 결과 보고서 원천 데이터
     * @return Markdown 문자열
     */
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

    /**
     * 테스트 요약 섹션을 작성합니다.
     *
     * @param markdown Markdown 버퍼
     * @param data 테스트 결과 보고서 원천 데이터
     */
    private void appendSummary(StringBuilder markdown, TestResultReportData data) {
        appendLine(markdown, "## 테스트 요약");
        appendLine(markdown, "- 전체 테스트: " + data.getTotalCount());
        appendLine(markdown, "- 성공: " + data.getSuccessCount());
        appendLine(markdown, "- 실패: " + data.getFailCount());
        appendLine(markdown, "- 차단: " + data.getBlockedCount());
        appendLine(markdown, "- 성공률: " + String.format(Locale.ROOT, "%.1f%%", data.getSuccessRate()));
        appendLine(markdown, "");
    }

    /**
     * 실패/차단 테스트 섹션을 작성합니다.
     *
     * @param markdown Markdown 버퍼
     * @param evidences 테스트 증적 목록
     */
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

    /**
     * 이슈별 테스트 결과 섹션을 작성합니다.
     *
     * @param markdown Markdown 버퍼
     * @param evidences 테스트 증적 목록
     */
    private void appendIssueResults(StringBuilder markdown, List<TestResultEvidenceRow> evidences) {
        appendLine(markdown, "## 이슈별 테스트 결과");
        if (evidences.isEmpty()) {
            appendLine(markdown, "- 없음");
        } else {
            evidences.forEach(evidence -> appendLine(markdown, "- " + issueKey(evidence) + " - " + evidence.getTestName() + " / " + resultLabel(evidence) + testedAt(evidence)));
        }
        appendLine(markdown, "");
    }

    /**
     * 증적 파일 목록 섹션을 작성합니다.
     *
     * @param markdown Markdown 버퍼
     * @param evidences 테스트 증적 목록
     */
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

    /**
     * 보고서에 표시할 프로젝트명을 구성합니다.
     *
     * @param data 테스트 결과 보고서 원천 데이터
     * @return 프로젝트 표시명
     */
    private String projectName(TestResultReportData data) {
        if (data.getProject() == null) {
            return "-";
        }
        return data.getProject().getProjectCode() + " - " + data.getProject().getProjectName();
    }

    /**
     * 테스트 증적의 이슈 키 표시값을 반환합니다.
     *
     * @param evidence 테스트 증적 행
     * @return 이슈 키 표시값
     */
    private String issueKey(TestResultEvidenceRow evidence) {
        return evidence.getIssueKey() == null || evidence.getIssueKey().isBlank() ? "-" : evidence.getIssueKey();
    }

    /**
     * 테스트 판정 라벨을 반환합니다.
     *
     * @param evidence 테스트 증적 행
     * @return 판정 라벨
     */
    private String resultLabel(TestResultEvidenceRow evidence) {
        return evidence.getResultStatus() == null ? "-" : evidence.getResultStatus().getLabel();
    }

    /**
     * 테스트 수행일시 표시 문자열을 구성합니다.
     *
     * @param evidence 테스트 증적 행
     * @return 수행일시 표시 문자열
     */
    private String testedAt(TestResultEvidenceRow evidence) {
        return evidence.getTestedAt() == null ? "" : " (" + DATE_TIME_FORMATTER.format(evidence.getTestedAt()) + ")";
    }

    /**
     * Markdown 버퍼에 한 줄을 추가합니다.
     *
     * @param markdown Markdown 버퍼
     * @param line 추가할 줄
     */
    private void appendLine(StringBuilder markdown, String line) {
        markdown.append(line).append(System.lineSeparator());
    }
}
