package com.devtrace.manager.artifact.excel;

import com.devtrace.manager.artifact.dto.TestResultEvidenceRow;
import com.devtrace.manager.artifact.dto.TestResultReportData;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class TestResultReportExcelGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] generate(TestResultReportData data) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle headerStyle = createHeaderStyle(workbook);
            writeSummarySheet(workbook, headerStyle, data);
            writeResultSheet(workbook, headerStyle, data.getEvidences());
            writeFailBlockedSheet(workbook, headerStyle, data.getEvidences());
            writeEvidenceFileSheet(workbook, headerStyle, data.getEvidences());
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("테스트 결과 보고서 Excel 생성에 실패했습니다.", e);
        }
    }

    private void writeSummarySheet(Workbook workbook, CellStyle headerStyle, TestResultReportData data) {
        Sheet sheet = workbook.createSheet("테스트 요약");
        writeHeader(sheet, headerStyle, "항목", "값");
        writeSummaryRow(sheet, 1, "프로젝트", projectName(data));
        writeSummaryRow(sheet, 2, "기간", data.getStartDate() + " ~ " + data.getEndDate());
        writeSummaryRow(sheet, 3, "전체 테스트", data.getTotalCount());
        writeSummaryRow(sheet, 4, "성공", data.getSuccessCount());
        writeSummaryRow(sheet, 5, "실패", data.getFailCount());
        writeSummaryRow(sheet, 6, "차단", data.getBlockedCount());
        writeSummaryRow(sheet, 7, "성공률", String.format(Locale.ROOT, "%.1f%%", data.getSuccessRate()));
        autoSize(sheet, 2);
    }

    private void writeResultSheet(Workbook workbook, CellStyle headerStyle, List<TestResultEvidenceRow> evidences) {
        Sheet sheet = workbook.createSheet("테스트 결과 목록");
        writeEvidenceHeader(sheet, headerStyle);
        int rowIndex = 1;
        int sequence = 1;
        for (TestResultEvidenceRow evidence : evidences) {
            writeEvidenceRow(sheet.createRow(rowIndex++), sequence++, evidence);
        }
        autoSize(sheet, 12);
    }

    private void writeFailBlockedSheet(Workbook workbook, CellStyle headerStyle, List<TestResultEvidenceRow> evidences) {
        Sheet sheet = workbook.createSheet("실패 차단 항목");
        writeEvidenceHeader(sheet, headerStyle);
        int rowIndex = 1;
        int sequence = 1;
        for (TestResultEvidenceRow evidence : evidences.stream().filter(TestResultEvidenceRow::isFailOrBlocked).toList()) {
            writeEvidenceRow(sheet.createRow(rowIndex++), sequence++, evidence);
        }
        autoSize(sheet, 12);
    }

    private void writeEvidenceFileSheet(Workbook workbook, CellStyle headerStyle, List<TestResultEvidenceRow> evidences) {
        Sheet sheet = workbook.createSheet("증적 파일 목록");
        writeHeader(sheet, headerStyle, "순번", "프로젝트", "이슈 키", "테스트명", "스크린샷 파일명", "스크린샷 저장 경로");
        int rowIndex = 1;
        int sequence = 1;
        for (TestResultEvidenceRow evidence : evidences.stream().filter(TestResultEvidenceRow::hasScreenshot).toList()) {
            Row row = sheet.createRow(rowIndex++);
            writeCell(row, 0, sequence++);
            writeCell(row, 1, projectName(evidence));
            writeCell(row, 2, evidence.getIssueKey());
            writeCell(row, 3, evidence.getTestName());
            writeCell(row, 4, evidence.getScreenshotFileName());
            writeCell(row, 5, evidence.getScreenshotFilePath());
        }
        autoSize(sheet, 6);
    }

    private void writeEvidenceHeader(Sheet sheet, CellStyle headerStyle) {
        writeHeader(
                sheet,
                headerStyle,
                "순번",
                "프로젝트",
                "이슈 키",
                "테스트명",
                "테스트 대상 화면",
                "테스트 절차",
                "기대 결과",
                "실제 결과",
                "판정",
                "수행자",
                "수행일시",
                "스크린샷 파일명"
        );
    }

    private void writeEvidenceRow(Row row, int sequence, TestResultEvidenceRow evidence) {
        writeCell(row, 0, sequence);
        writeCell(row, 1, projectName(evidence));
        writeCell(row, 2, evidence.getIssueKey());
        writeCell(row, 3, evidence.getTestName());
        writeCell(row, 4, evidence.getTestTarget());
        writeCell(row, 5, evidence.getTestProcedure());
        writeCell(row, 6, evidence.getExpectedResult());
        writeCell(row, 7, evidence.getActualResult());
        writeCell(row, 8, evidence.getResultStatus() == null ? "" : evidence.getResultStatus().getLabel());
        writeCell(row, 9, evidence.getTesterName());
        writeCell(row, 10, evidence.getTestedAt() == null ? "" : DATE_TIME_FORMATTER.format(evidence.getTestedAt()));
        writeCell(row, 11, evidence.getScreenshotFileName());
    }

    private void writeHeader(Sheet sheet, CellStyle headerStyle, String... headers) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void writeSummaryRow(Sheet sheet, int rowIndex, String item, Object value) {
        Row row = sheet.createRow(rowIndex);
        writeCell(row, 0, item);
        writeCell(row, 1, String.valueOf(value));
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private void writeCell(Row row, int index, String value) {
        row.createCell(index).setCellValue(value == null ? "" : value);
    }

    private void writeCell(Row row, int index, int value) {
        row.createCell(index).setCellValue(value);
    }

    private String projectName(TestResultReportData data) {
        if (data.getProject() == null) {
            return "-";
        }
        return data.getProject().getProjectCode() + " - " + data.getProject().getProjectName();
    }

    private String projectName(TestResultEvidenceRow evidence) {
        if (evidence.getProjectCode() == null && evidence.getProjectName() == null) {
            return "-";
        }
        return (evidence.getProjectCode() == null ? "" : evidence.getProjectCode()) + " - " + (evidence.getProjectName() == null ? "" : evidence.getProjectName());
    }

    private void autoSize(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.min(Math.max(sheet.getColumnWidth(i), 3000), 18000));
        }
    }
}
