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

/**
 * 컬럼명세 생성 업무 규칙을 구현합니다.
 *
 * <p>프로젝트 검증, DDL 파싱, 컬럼명세 저장, Excel 생성 흐름을 조율합니다.</p>
 */
@Service
@Transactional(readOnly = true)
public class ColumnSpecServiceImpl implements ColumnSpecService {

    private final ColumnSpecDao columnSpecDao;
    private final ProjectDao projectDao;
    private final DdlParser ddlParser;
    private final ColumnSpecExcelGenerator excelGenerator;

    /**
     * 컬럼명세 서비스 구현체를 생성한다.
     *
     * @param columnSpecDao 컬럼명세 SQL 호출 DAO
     * @param projectDao 프로젝트 검증 DAO
     * @param ddlParser DDL 파서
     * @param excelGenerator 컬럼명세 Excel 생성기
     */
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

    /**
     * DDL을 파싱해 컬럼명세 미리보기 목록을 생성하고 저장한다.
     *
     * <p>프로젝트 기준으로 기존 컬럼명세를 삭제한 뒤 새 파싱 결과를 저장하여 최신 명세를 유지한다.</p>
     *
     * @param request 컬럼명세 생성 요청
     * @return 컬럼명세 응답 목록
     */
    @Override
    @Transactional
    public List<ColumnSpecResponse> selectColumnSpecPreviewList(ColumnSpecRequest request) {
        validateProject(request.getProjectId());
        List<ColumnSpecEntity> columnSpecs = parseAndPrepare(request);
        columnSpecDao.deleteColumnSpecsByProjectId(request.getProjectId());
        columnSpecDao.insertColumnSpecs(columnSpecs);
        return toResponses(columnSpecs);
    }

    /**
     * DDL 파싱 결과로 컬럼명세 Excel 파일을 생성한다.
     *
     * @param request 컬럼명세 생성 요청
     * @return xlsx 파일 바이트 배열
     */
    @Override
    public byte[] selectColumnSpecExcel(ColumnSpecRequest request) {
        validateProject(request.getProjectId());
        return excelGenerator.generate(toResponses(parseAndPrepare(request)));
    }

    /**
     * 저장된 컬럼명세 목록을 조회한다.
     *
     * @param condition 검색 조건
     * @return 컬럼명세 응답 목록
     */
    @Override
    public List<ColumnSpecResponse> selectColumnSpecList(ColumnSpecSearchCondition condition) {
        return toResponses(columnSpecDao.selectColumnSpecList(condition));
    }

    /**
     * DDL을 파싱하고 저장 가능한 컬럼명세 엔티티로 준비한다.
     *
     * <p>각 행에 신규 UUID와 생성일시를 부여한다.</p>
     *
     * @param request 컬럼명세 생성 요청
     * @return 컬럼명세 엔티티 목록
     */
    private List<ColumnSpecEntity> parseAndPrepare(ColumnSpecRequest request) {
        LocalDateTime now = DateTimeUtil.now();
        List<ColumnSpecEntity> columnSpecs = ddlParser.parse(request.getProjectId(), request.getDatabaseType(), request.getDdl());
        for (ColumnSpecEntity columnSpec : columnSpecs) {
            columnSpec.setColumnSpecId(UUID.randomUUID());
            columnSpec.setCreatedAt(now);
        }
        return columnSpecs;
    }

    /**
     * 프로젝트 존재 여부를 검증한다.
     *
     * @param projectId 프로젝트 ID
     */
    private void validateProject(UUID projectId) {
        projectDao.selectProjectById(projectId)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다.", "PROJECT_NOT_FOUND"));
    }

    /**
     * 컬럼명세 엔티티 목록을 응답 목록으로 변환한다.
     *
     * @param columnSpecs 컬럼명세 엔티티 목록
     * @return 컬럼명세 응답 목록
     */
    private List<ColumnSpecResponse> toResponses(List<ColumnSpecEntity> columnSpecs) {
        return columnSpecs.stream()
                .map(ColumnSpecEntity::toResponse)
                .toList();
    }
}
