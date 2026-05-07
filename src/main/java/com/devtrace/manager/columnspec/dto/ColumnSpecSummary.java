package com.devtrace.manager.columnspec.dto;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ColumnSpecSummary {

    private final int tableCount;
    private final int columnCount;
    private final int primaryKeyCount;
    private final int foreignKeyCount;

    private ColumnSpecSummary(int tableCount, int columnCount, int primaryKeyCount, int foreignKeyCount) {
        this.tableCount = tableCount;
        this.columnCount = columnCount;
        this.primaryKeyCount = primaryKeyCount;
        this.foreignKeyCount = foreignKeyCount;
    }

    /**
     * 컬럼명세 목록에서 테이블 수, 컬럼 수, PK/FK 수를 집계합니다.
     *
     * @param specs 컬럼명세 목록
     * @return 컬럼명세 요약
     */
    public static ColumnSpecSummary from(List<ColumnSpecResponse> specs) {
        Set<String> tableNames = new LinkedHashSet<>();
        int primaryKeyCount = 0;
        int foreignKeyCount = 0;

        for (ColumnSpecResponse spec : specs) {
            if (spec.getTableName() != null && !spec.getTableName().isBlank()) {
                tableNames.add(spec.getTableName());
            }
            if ("Y".equals(spec.getIsPk())) {
                primaryKeyCount++;
            }
            if ("Y".equals(spec.getIsFk())) {
                foreignKeyCount++;
            }
        }

        return new ColumnSpecSummary(tableNames.size(), specs.size(), primaryKeyCount, foreignKeyCount);
    }

    public int getTableCount() {
        return tableCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getPrimaryKeyCount() {
        return primaryKeyCount;
    }

    public int getForeignKeyCount() {
        return foreignKeyCount;
    }
}
