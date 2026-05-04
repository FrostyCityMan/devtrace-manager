package com.devtrace.manager.columnspec.dto;

import java.util.UUID;

public class ColumnSpecSearchCondition {

    private UUID projectId;
    private String tableName;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
