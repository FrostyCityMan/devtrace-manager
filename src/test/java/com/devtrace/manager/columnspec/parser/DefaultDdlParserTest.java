package com.devtrace.manager.columnspec.parser;

import static org.assertj.core.api.Assertions.assertThat;

import com.devtrace.manager.columnspec.dto.ColumnSpecEntity;
import com.devtrace.manager.columnspec.dto.DatabaseType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DefaultDdlParserTest {

    private final DefaultDdlParser parser = new DefaultDdlParser();

    @Test
    void parsePostgreSqlCreateTableWithComments() {
        UUID projectId = UUID.randomUUID();
        String ddl = """
                CREATE TABLE PROJECT (
                    PROJECT_ID UUID PRIMARY KEY,
                    PROJECT_CODE VARCHAR(50) NOT NULL UNIQUE,
                    PROJECT_NAME VARCHAR(200) NOT NULL,
                    CLIENT_NAME VARCHAR(200),
                    STATUS VARCHAR(30) NOT NULL DEFAULT 'READY',
                    CREATED_AT TIMESTAMP NOT NULL
                );

                COMMENT ON TABLE PROJECT IS '프로젝트';
                COMMENT ON COLUMN PROJECT.PROJECT_ID IS '프로젝트 ID';
                COMMENT ON COLUMN PROJECT.PROJECT_CODE IS '프로젝트 코드';
                """;

        List<ColumnSpecEntity> specs = parser.parse(projectId, DatabaseType.POSTGRESQL, ddl);

        assertThat(specs).hasSize(6);
        assertThat(specs.get(0).getTableName()).isEqualTo("PROJECT");
        assertThat(specs.get(0).getTableComment()).isEqualTo("프로젝트");
        assertThat(specs.get(0).getColumnName()).isEqualTo("PROJECT_ID");
        assertThat(specs.get(0).getColumnComment()).isEqualTo("프로젝트 ID");
        assertThat(specs.get(0).getDataType()).isEqualTo("UUID");
        assertThat(specs.get(0).getIsPk()).isEqualTo("Y");
        assertThat(specs.get(0).getIsNullable()).isEqualTo("N");
        assertThat(specs.get(1).getDataLength()).isEqualTo("50");
        assertThat(specs.get(4).getDefaultValue()).isEqualTo("'READY'");
    }

    @Test
    void parseMysqlCreateTableWithInlineCommentAndForeignKey() {
        UUID projectId = UUID.randomUUID();
        String ddl = """
                CREATE TABLE `ISSUE` (
                    `ISSUE_ID` CHAR(36) NOT NULL,
                    `PROJECT_ID` CHAR(36) NOT NULL COMMENT '프로젝트 ID',
                    `TITLE` VARCHAR(500) NOT NULL COMMENT '제목',
                    PRIMARY KEY (`ISSUE_ID`),
                    CONSTRAINT FK_ISSUE_PROJECT FOREIGN KEY (`PROJECT_ID`) REFERENCES PROJECT (`PROJECT_ID`)
                ) COMMENT='이슈';
                """;

        List<ColumnSpecEntity> specs = parser.parse(projectId, DatabaseType.MYSQL, ddl);

        assertThat(specs).hasSize(3);
        assertThat(specs.get(0).getTableComment()).isEqualTo("이슈");
        assertThat(specs.get(0).getIsPk()).isEqualTo("Y");
        assertThat(specs.get(1).getColumnComment()).isEqualTo("프로젝트 ID");
        assertThat(specs.get(1).getIsFk()).isEqualTo("Y");
        assertThat(specs.get(2).getDataType()).isEqualTo("VARCHAR");
        assertThat(specs.get(2).getDataLength()).isEqualTo("500");
    }

    @Test
    void parseOracleCreateTableWithNumberAndComments() {
        UUID projectId = UUID.randomUUID();
        String ddl = """
                CREATE TABLE APP_USER (
                    USER_ID RAW(16) NOT NULL,
                    USERNAME VARCHAR2(50) NOT NULL,
                    DISPLAY_NAME VARCHAR2(100),
                    LOGIN_COUNT NUMBER(10,0) DEFAULT 0,
                    CONSTRAINT PK_APP_USER PRIMARY KEY (USER_ID)
                );

                COMMENT ON TABLE APP_USER IS '사용자';
                COMMENT ON COLUMN APP_USER.USERNAME IS '사용자 계정';
                """;

        List<ColumnSpecEntity> specs = parser.parse(projectId, DatabaseType.ORACLE, ddl);

        assertThat(specs).hasSize(4);
        assertThat(specs.get(0).getIsPk()).isEqualTo("Y");
        assertThat(specs.get(1).getDataType()).isEqualTo("VARCHAR2");
        assertThat(specs.get(1).getDataLength()).isEqualTo("50");
        assertThat(specs.get(1).getColumnComment()).isEqualTo("사용자 계정");
        assertThat(specs.get(3).getDataType()).isEqualTo("NUMBER");
        assertThat(specs.get(3).getDataLength()).isEqualTo("10,0");
        assertThat(specs.get(3).getDefaultValue()).isEqualTo("0");
    }
}
