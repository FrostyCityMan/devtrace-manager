package com.devtrace.manager.columnspec.service;

import com.devtrace.manager.columnspec.dto.ColumnSpecRequest;
import com.devtrace.manager.columnspec.dto.ColumnSpecResponse;
import com.devtrace.manager.columnspec.dto.ColumnSpecSearchCondition;
import java.util.List;

public interface ColumnSpecService {

    List<ColumnSpecResponse> selectColumnSpecPreviewList(ColumnSpecRequest request);

    byte[] selectColumnSpecExcel(ColumnSpecRequest request);

    List<ColumnSpecResponse> selectColumnSpecList(ColumnSpecSearchCondition condition);
}
