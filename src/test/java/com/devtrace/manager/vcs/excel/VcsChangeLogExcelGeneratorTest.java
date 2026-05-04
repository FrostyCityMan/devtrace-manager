package com.devtrace.manager.vcs.excel;

import static org.assertj.core.api.Assertions.assertThat;

import com.devtrace.manager.vcs.dto.VcsChangeFileResponse;
import com.devtrace.manager.vcs.dto.VcsChangeLogResponse;
import com.devtrace.manager.vcs.dto.VcsType;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class VcsChangeLogExcelGeneratorTest {

    private final VcsChangeLogExcelGenerator generator = new VcsChangeLogExcelGenerator();

    @Test
    void generateVcsChangeLogWorkbook() throws Exception {
        VcsChangeLogResponse log = new VcsChangeLogResponse();
        log.setChangeLogId(UUID.randomUUID());
        log.setProjectId(UUID.randomUUID());
        log.setVcsType(VcsType.GIT);
        log.setRevisionNo("abc123");
        log.setAuthor("kim");
        log.setChangedAt(LocalDateTime.of(2026, 5, 4, 9, 0));
        log.setIssueKeys(List.of("ISO-101"));
        log.setMessage("ISO-101 사용자 목록 조회 오류 수정");
        VcsChangeFileResponse file = new VcsChangeFileResponse();
        file.setChangeType("M");
        file.setFilePath("src/main/java/App.java");
        log.setChangedFiles(List.of(file));

        byte[] excel = generator.generate(List.of(log));

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
            assertThat(workbook.getSheet("변경이력").getRow(0).getCell(2).getStringCellValue()).isEqualTo("리비전");
            assertThat(workbook.getSheet("변경이력").getRow(1).getCell(1).getStringCellValue()).isEqualTo("GIT");
            assertThat(workbook.getSheet("변경이력").getRow(1).getCell(2).getStringCellValue()).isEqualTo("abc123");
            assertThat(workbook.getSheet("변경이력").getRow(1).getCell(5).getStringCellValue()).isEqualTo("ISO-101");
            assertThat(workbook.getSheet("변경이력").getRow(1).getCell(7).getStringCellValue()).contains("src/main/java/App.java");
        }
    }
}
