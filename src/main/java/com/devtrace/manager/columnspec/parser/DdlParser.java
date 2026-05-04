package com.devtrace.manager.columnspec.parser;

import com.devtrace.manager.columnspec.dto.ColumnSpecEntity;
import com.devtrace.manager.columnspec.dto.DatabaseType;
import java.util.List;
import java.util.UUID;

public interface DdlParser {

    List<ColumnSpecEntity> parse(UUID projectId, DatabaseType databaseType, String ddl);
}
