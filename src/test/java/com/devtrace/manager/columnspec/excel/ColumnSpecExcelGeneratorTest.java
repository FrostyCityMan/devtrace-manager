package com.devtrace.manager.columnspec.excel;

import static org.assertj.core.api.Assertions.assertThat;

import com.devtrace.manager.columnspec.dto.ColumnSpecResponse;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class ColumnSpecExcelGeneratorTest {

    private final ColumnSpecExcelGenerator generator = new ColumnSpecExcelGenerator();

    @Test
    void generateColumnSpecWorkbook() throws Exception {
        byte[] excel = generator.generate(List.of(
                createSpec("PROJECT", "프로젝트", "PROJECT_ID", "프로젝트 ID", "UUID", null, "N", "Y", "N"),
                createSpec("PROJECT", "프로젝트", "PROJECT_NAME", "프로젝트명", "VARCHAR", "200", "N", "N", "N")
        ));

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(3);
            assertThat(workbook.getSheet("테이블 목록").getRow(0).getCell(1).getStringCellValue()).isEqualTo("테이블명");
            assertThat(workbook.getSheet("테이블 목록").getRow(1).getCell(1).getStringCellValue()).isEqualTo("PROJECT");
            assertThat(workbook.getSheet("컬럼 명세").getRow(0).getCell(3).getStringCellValue()).isEqualTo("컬럼명");
            assertThat(workbook.getSheet("컬럼 명세").getRow(1).getCell(3).getStringCellValue()).isEqualTo("PROJECT_ID");
            assertThat(workbook.getSheet("컬럼 명세").getRow(1).getCell(8).getStringCellValue()).isEqualTo("Y");
            assertThat(workbook.getSheet("변경 이력").getRow(0).getCell(6).getStringCellValue()).isEqualTo("변경 내용");
        }
    }

    private ColumnSpecResponse createSpec(
            String tableName,
            String tableComment,
            String columnName,
            String columnComment,
            String dataType,
            String dataLength,
            String nullable,
            String pk,
            String fk
    ) {
        ColumnSpecResponse spec = new ColumnSpecResponse();
        spec.setColumnSpecId(UUID.randomUUID());
        spec.setProjectId(UUID.randomUUID());
        spec.setTableName(tableName);
        spec.setTableComment(tableComment);
        spec.setColumnName(columnName);
        spec.setColumnComment(columnComment);
        spec.setDataType(dataType);
        spec.setDataLength(dataLength);
        spec.setIsNullable(nullable);
        spec.setIsPk(pk);
        spec.setIsFk(fk);
        return spec;
    }
}
