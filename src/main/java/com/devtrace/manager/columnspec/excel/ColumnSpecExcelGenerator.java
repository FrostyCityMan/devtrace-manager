package com.devtrace.manager.columnspec.excel;

import com.devtrace.manager.columnspec.dto.ColumnSpecResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
 * 컬럼명세 데이터를 Apache POI 기반 {@code .xlsx} 파일로 생성합니다.
 *
 * <p>Excel 산출물은 테이블 목록, 컬럼 명세, 변경 이력 시트로 구성되며,
 * 헤더 스타일과 컬럼 너비 자동 조정을 공통 규칙으로 적용합니다.</p>
 */
@Component
public class ColumnSpecExcelGenerator {

    /**
     * 컬럼명세 응답 목록을 Excel 바이너리로 생성합니다.
     *
     * @param specs 컬럼명세 목록
     * @return Excel 파일 바이트 배열
     */
    public byte[] generate(List<ColumnSpecResponse> specs) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle headerStyle = createHeaderStyle(workbook);
            writeTableListSheet(workbook, headerStyle, specs);
            writeColumnSpecSheet(workbook, headerStyle, specs);
            writeChangeHistorySheet(workbook, headerStyle);
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("컬럼명세 Excel 생성에 실패했습니다.", e);
        }
    }

    /**
     * 테이블 목록 시트를 작성합니다.
     *
     * @param workbook Excel 워크북
     * @param headerStyle 헤더 셀 스타일
     * @param specs 컬럼명세 목록
     */
    private void writeTableListSheet(Workbook workbook, CellStyle headerStyle, List<ColumnSpecResponse> specs) {
        Sheet sheet = workbook.createSheet("테이블 목록");
        writeHeader(sheet, headerStyle, "순번", "테이블명", "테이블 설명", "비고");

        Map<String, String> tableComments = new LinkedHashMap<>();
        for (ColumnSpecResponse spec : specs) {
            tableComments.putIfAbsent(spec.getTableName(), spec.getTableComment());
        }

        int rowIndex = 1;
        int sequence = 1;
        for (Map.Entry<String, String> entry : tableComments.entrySet()) {
            Row row = sheet.createRow(rowIndex++);
            writeCell(row, 0, sequence++);
            writeCell(row, 1, entry.getKey());
            writeCell(row, 2, entry.getValue());
            writeCell(row, 3, "");
        }
        autoSize(sheet, 4);
    }

    /**
     * 컬럼 명세 시트를 작성합니다.
     *
     * @param workbook Excel 워크북
     * @param headerStyle 헤더 셀 스타일
     * @param specs 컬럼명세 목록
     */
    private void writeColumnSpecSheet(Workbook workbook, CellStyle headerStyle, List<ColumnSpecResponse> specs) {
        Sheet sheet = workbook.createSheet("컬럼 명세");
        writeHeader(sheet, headerStyle, "순번", "테이블명", "테이블 설명", "컬럼명", "컬럼 설명", "데이터 타입", "길이", "Nullable", "PK", "FK", "기본값", "비고");

        int rowIndex = 1;
        int sequence = 1;
        for (ColumnSpecResponse spec : specs) {
            Row row = sheet.createRow(rowIndex++);
            writeCell(row, 0, sequence++);
            writeCell(row, 1, spec.getTableName());
            writeCell(row, 2, spec.getTableComment());
            writeCell(row, 3, spec.getColumnName());
            writeCell(row, 4, spec.getColumnComment());
            writeCell(row, 5, spec.getDataType());
            writeCell(row, 6, spec.getDataLength());
            writeCell(row, 7, spec.getIsNullable());
            writeCell(row, 8, spec.getIsPk());
            writeCell(row, 9, spec.getIsFk());
            writeCell(row, 10, spec.getDefaultValue());
            writeCell(row, 11, spec.getRemark());
        }
        autoSize(sheet, 12);
    }

    /**
     * 컬럼명세서 생성 이력을 표시하는 기본 변경 이력 시트를 작성합니다.
     *
     * @param workbook Excel 워크북
     * @param headerStyle 헤더 셀 스타일
     */
    private void writeChangeHistorySheet(Workbook workbook, CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("변경 이력");
        writeHeader(sheet, headerStyle, "순번", "변경일자", "변경자", "변경 구분", "테이블명", "컬럼명", "변경 내용");

        Row row = sheet.createRow(1);
        writeCell(row, 0, 1);
        writeCell(row, 1, LocalDate.now().toString());
        writeCell(row, 2, "admin");
        writeCell(row, 3, "생성");
        writeCell(row, 4, "");
        writeCell(row, 5, "");
        writeCell(row, 6, "DDL 기반 컬럼명세서 생성");
        autoSize(sheet, 7);
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
     * 지정 컬럼 수만큼 너비를 자동 조정하고 최소/최대 너비를 보정합니다.
     *
     * @param sheet 대상 시트
     * @param columnCount 조정할 컬럼 수
     */
    private void autoSize(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.min(Math.max(sheet.getColumnWidth(i), 3000), 12000));
        }
    }
}
