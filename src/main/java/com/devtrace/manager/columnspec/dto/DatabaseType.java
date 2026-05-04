package com.devtrace.manager.columnspec.dto;

public enum DatabaseType {
    POSTGRESQL("PostgreSQL"),
    ORACLE("Oracle"),
    MYSQL("MySQL");

    private final String label;

    DatabaseType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
