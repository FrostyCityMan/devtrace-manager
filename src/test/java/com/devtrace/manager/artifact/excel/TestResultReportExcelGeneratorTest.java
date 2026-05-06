package com.devtrace.manager.artifact.excel;

import static org.assertj.core.api.Assertions.assertThat;

import com.devtrace.manager.artifact.dto.TestResultEvidenceRow;
import com.devtrace.manager.artifact.dto.TestResultReportData;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.testevidence.dto.TestEvidenceResult;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class TestResultReportExcelGeneratorTest {

    private final TestResultReportExcelGenerator generator = new TestResultReportExcelGenerator();

    @Test
    void generateTestResultReportWorkbook() throws Exception {
        TestResultReportData data = new TestResultReportData();
        data.setProject(createProject());
        data.setStartDate(LocalDate.of(2026, 5, 1));
        data.setEndDate(LocalDate.of(2026, 5, 6));
        data.setEvidences(List.of(createEvidence()));

        byte[] excel = generator.generate(data);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(4);
            assertThat(workbook.getSheet("테스트 요약").getRow(0).getCell(0).getStringCellValue()).isEqualTo("항목");
            assertThat(workbook.getSheet("테스트 결과 목록").getRow(0).getCell(3).getStringCellValue()).isEqualTo("테스트명");
            assertThat(workbook.getSheet("테스트 결과 목록").getRow(1).getCell(2).getStringCellValue()).isEqualTo("DTR-101");
            assertThat(workbook.getSheet("테스트 결과 목록").getRow(1).getCell(8).getStringCellValue()).isEqualTo("실패");
            assertThat(workbook.getSheet("실패 차단 항목").getRow(1).getCell(3).getStringCellValue()).isEqualTo("로그인 실패");
            assertThat(workbook.getSheet("증적 파일 목록").getRow(1).getCell(4).getStringCellValue()).isEqualTo("fail.png");
        }
    }

    private ProjectResponse createProject() {
        ProjectResponse project = new ProjectResponse();
        project.setProjectId(UUID.randomUUID());
        project.setProjectCode("DTR");
        project.setProjectName("DevTrace Manager");
        return project;
    }

    private TestResultEvidenceRow createEvidence() {
        TestResultEvidenceRow evidence = new TestResultEvidenceRow();
        evidence.setTestEvidenceId(UUID.randomUUID());
        evidence.setProjectCode("DTR");
        evidence.setProjectName("DevTrace Manager");
        evidence.setIssueId(UUID.randomUUID());
        evidence.setIssueKey("DTR-101");
        evidence.setTestName("로그인 실패");
        evidence.setTestTarget("/login");
        evidence.setTestProcedure("로그인 버튼 클릭");
        evidence.setExpectedResult("대시보드 이동");
        evidence.setActualResult("오류 발생");
        evidence.setResultStatus(TestEvidenceResult.FAIL);
        evidence.setTesterName("관리자");
        evidence.setTestedAt(LocalDateTime.of(2026, 5, 6, 10, 0));
        evidence.setScreenshotFileName("fail.png");
        evidence.setScreenshotFilePath("uploads/test-evidences/fail.png");
        return evidence;
    }
}
