package com.devtrace.manager.columnspec.service.impl;

import com.devtrace.manager.columnspec.dao.ColumnSpecDao;
import com.devtrace.manager.columnspec.dto.ColumnSpecEntity;
import com.devtrace.manager.columnspec.dto.ColumnSpecRequest;
import com.devtrace.manager.columnspec.dto.ColumnSpecResponse;
import com.devtrace.manager.columnspec.dto.ColumnSpecSearchCondition;
import com.devtrace.manager.columnspec.excel.ColumnSpecExcelGenerator;
import com.devtrace.manager.columnspec.parser.DdlParser;
import com.devtrace.manager.common.util.DateTimeUtil;
import com.devtrace.manager.project.dao.ProjectDao;
import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.columnspec.service.ColumnSpecService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ColumnSpecServiceImpl implements ColumnSpecService {

    private final ColumnSpecDao columnSpecDao;
    private final ProjectDao projectDao;
    private final DdlParser ddlParser;
    private final ColumnSpecExcelGenerator excelGenerator;

    public ColumnSpecServiceImpl(
            ColumnSpecDao columnSpecDao,
            ProjectDao projectDao,
            DdlParser ddlParser,
            ColumnSpecExcelGenerator excelGenerator
    ) {
        this.columnSpecDao = columnSpecDao;
        this.projectDao = projectDao;
        this.ddlParser = ddlParser;
        this.excelGenerator = excelGenerator;
    }

    @Override
    @Transactional
    public List<ColumnSpecResponse> selectColumnSpecPreviewList(ColumnSpecRequest request) {
        validateProject(request.getProjectId());
        List<ColumnSpecEntity> columnSpecs = parseAndPrepare(request);
        columnSpecDao.deleteColumnSpecsByProjectId(request.getProjectId());
        columnSpecDao.insertColumnSpecs(columnSpecs);
        return toResponses(columnSpecs);
    }

    @Override
    public byte[] selectColumnSpecExcel(ColumnSpecRequest request) {
        validateProject(request.getProjectId());
        return excelGenerator.generate(toResponses(parseAndPrepare(request)));
    }

    @Override
    public List<ColumnSpecResponse> selectColumnSpecList(ColumnSpecSearchCondition condition) {
        return toResponses(columnSpecDao.selectColumnSpecList(condition));
    }

    private List<ColumnSpecEntity> parseAndPrepare(ColumnSpecRequest request) {
        LocalDateTime now = DateTimeUtil.now();
        List<ColumnSpecEntity> columnSpecs = ddlParser.parse(request.getProjectId(), request.getDatabaseType(), request.getDdl());
        for (ColumnSpecEntity columnSpec : columnSpecs) {
            columnSpec.setColumnSpecId(UUID.randomUUID());
            columnSpec.setCreatedAt(now);
        }
        return columnSpecs;
    }

    private void validateProject(UUID projectId) {
        projectDao.selectProjectById(projectId)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다.", "PROJECT_NOT_FOUND"));
    }

    private List<ColumnSpecResponse> toResponses(List<ColumnSpecEntity> columnSpecs) {
        return columnSpecs.stream()
                .map(ColumnSpecEntity::toResponse)
                .toList();
    }
}
