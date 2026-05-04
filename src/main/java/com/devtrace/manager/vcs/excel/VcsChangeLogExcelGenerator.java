package com.devtrace.manager.vcs.excel;

import com.devtrace.manager.vcs.dto.VcsChangeLogResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
public class VcsChangeLogExcelGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] generate(List<VcsChangeLogResponse> logs) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("변경이력");
            CellStyle headerStyle = createHeaderStyle(workbook);
            writeHeader(sheet, headerStyle, "순번", "VCS", "리비전", "작성자", "변경일시", "이슈 키", "메시지", "변경 파일");

            int rowIndex = 1;
            int sequence = 1;
            for (VcsChangeLogResponse log : logs) {
                Row row = sheet.createRow(rowIndex++);
                writeCell(row, 0, sequence++);
                writeCell(row, 1, log.getVcsType() == null ? "" : log.getVcsType().name());
                writeCell(row, 2, log.getRevisionNo());
                writeCell(row, 3, log.getAuthor());
                writeCell(row, 4, log.getChangedAt() == null ? "" : DATE_TIME_FORMATTER.format(log.getChangedAt()));
                writeCell(row, 5, log.getIssueKeyText());
                writeCell(row, 6, log.getMessage());
                writeCell(row, 7, log.getChangedFileText());
            }
            autoSize(sheet, 8);
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("변경이력 Excel 생성에 실패했습니다.", e);
        }
    }

    private void writeHeader(Sheet sheet, CellStyle headerStyle, String... headers) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
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

    private void autoSize(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.min(Math.max(sheet.getColumnWidth(i), 3000), 16000));
        }
    }
}
