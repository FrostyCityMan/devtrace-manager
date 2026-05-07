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

/**
 * 테스트 결과 보고서 데이터를 Apache POI 기반 Excel 파일로 생성합니다.
 *
 * <p>테스트 요약, 테스트 결과 목록, 실패/차단 항목, 증적 파일 목록 시트를 만들어
 * Markdown 보고서보다 표 중심의 제출물을 제공합니다.</p>
 */
@Component
public class TestResultReportExcelGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 테스트 결과 보고서 데이터를 Excel 바이너리로 생성합니다.
     *
     * @param data 테스트 결과 보고서 원천 데이터
     * @return Excel 파일 바이트 배열
     */
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

    /**
     * 테스트 요약 시트를 작성합니다.
     *
     * @param workbook Excel 워크북
     * @param headerStyle 헤더 셀 스타일
     * @param data 테스트 결과 보고서 원천 데이터
     */
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

    /**
     * 전체 테스트 결과 목록 시트를 작성합니다.
     *
     * @param workbook Excel 워크북
     * @param headerStyle 헤더 셀 스타일
     * @param evidences 테스트 증적 목록
     */
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

    /**
     * 실패/차단 테스트 항목 시트를 작성합니다.
     *
     * @param workbook Excel 워크북
     * @param headerStyle 헤더 셀 스타일
     * @param evidences 테스트 증적 목록
     */
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

    /**
     * 스크린샷 증적 파일 목록 시트를 작성합니다.
     *
     * @param workbook Excel 워크북
     * @param headerStyle 헤더 셀 스타일
     * @param evidences 테스트 증적 목록
     */
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

    /**
     * 테스트 증적 목록 계열 시트의 공통 헤더를 작성합니다.
     *
     * @param sheet 대상 시트
     * @param headerStyle 헤더 셀 스타일
     */
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

    /**
     * 테스트 증적 한 건을 Excel 행으로 작성합니다.
     *
     * @param row 대상 행
     * @param sequence 순번
     * @param evidence 테스트 증적 행
     */
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

    /**
     * 지정 시트의 헤더 행을 작성합니다.
     *
     * @param sheet 대상 시트
     * @param headerStyle 헤더 셀 스타일
     * @param headers 헤더 문자열 목록
     */
    private void writeHeader(Sheet sheet, CellStyle headerStyle, String... headers) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * 테스트 요약 시트의 항목 행을 작성합니다.
     *
     * @param sheet 대상 시트
     * @param rowIndex 행 인덱스
     * @param item 항목명
     * @param value 항목 값
     */
    private void writeSummaryRow(Sheet sheet, int rowIndex, String item, Object value) {
        Row row = sheet.createRow(rowIndex);
        writeCell(row, 0, item);
        writeCell(row, 1, String.valueOf(value));
    }

    /**
     * Excel 헤더 셀 스타일을 생성합니다.
     *
     * @param workbook Excel 워크북
     * @return 헤더 셀 스타일
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    /**
     * 문자열 셀 값을 작성합니다.
     *
     * @param row 대상 행
     * @param index 컬럼 인덱스
     * @param value 셀 값
     */
    private void writeCell(Row row, int index, String value) {
        row.createCell(index).setCellValue(value == null ? "" : value);
    }

    /**
     * 정수 셀 값을 작성합니다.
     *
     * @param row 대상 행
     * @param index 컬럼 인덱스
     * @param value 셀 값
     */
    private void writeCell(Row row, int index, int value) {
        row.createCell(index).setCellValue(value);
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
     * 테스트 증적 행의 프로젝트 표시명을 구성합니다.
     *
     * @param evidence 테스트 증적 행
     * @return 프로젝트 표시명
     */
    private String projectName(TestResultEvidenceRow evidence) {
        if (evidence.getProjectCode() == null && evidence.getProjectName() == null) {
            return "-";
        }
        return (evidence.getProjectCode() == null ? "" : evidence.getProjectCode()) + " - " + (evidence.getProjectName() == null ? "" : evidence.getProjectName());
    }

    /**
     * 지정 컬럼 수만큼 너비를 자동 조정하고 최소/최대 너비를 보정합니다.
     *
     * @param sheet 대상 시트
     * @param columnCount 조정할 컬럼 수
     */
    private void autoSize(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.min(Math.max(sheet.getColumnWidth(i), 3000), 18000));
        }
    }
}
