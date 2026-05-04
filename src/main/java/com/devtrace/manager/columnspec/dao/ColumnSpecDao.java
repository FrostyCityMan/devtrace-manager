package com.devtrace.manager.columnspec.dao;

import com.devtrace.manager.columnspec.dto.ColumnSpecEntity;
import com.devtrace.manager.columnspec.dto.ColumnSpecSearchCondition;
import java.util.List;
import java.util.UUID;
import org.apache.ibatis.annotations.Param;

public interface ColumnSpecDao {

    void insertColumnSpecs(@Param("columnSpecs") List<ColumnSpecEntity> columnSpecs);

    void deleteColumnSpecsByProjectId(UUID projectId);

    List<ColumnSpecEntity> selectColumnSpecList(ColumnSpecSearchCondition condition);
}
