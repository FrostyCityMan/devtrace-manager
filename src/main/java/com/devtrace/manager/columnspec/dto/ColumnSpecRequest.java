package com.devtrace.manager.columnspec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ColumnSpecRequest {

    @NotNull(message = "프로젝트는 필수입니다.")
    private UUID projectId;

    @NotNull(message = "DB 종류는 필수입니다.")
    private DatabaseType databaseType = DatabaseType.POSTGRESQL;

    @NotBlank(message = "DDL은 필수입니다.")
    private String ddl;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public String getDdl() {
        return ddl;
    }

    public void setDdl(String ddl) {
        this.ddl = ddl;
    }
}
