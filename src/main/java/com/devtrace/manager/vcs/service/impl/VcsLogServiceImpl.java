package com.devtrace.manager.vcs.service.impl;

import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.common.util.DateTimeUtil;
import com.devtrace.manager.project.dao.ProjectDao;
import com.devtrace.manager.vcs.dao.VcsIssueKeyRow;
import com.devtrace.manager.vcs.dao.VcsLogDao;
import com.devtrace.manager.vcs.dto.VcsChangeFileEntity;
import com.devtrace.manager.vcs.dto.VcsChangeLogEntity;
import com.devtrace.manager.vcs.dto.VcsChangeLogResponse;
import com.devtrace.manager.vcs.dto.VcsLogRequest;
import com.devtrace.manager.vcs.dto.VcsLogSearchCondition;
import com.devtrace.manager.vcs.dto.VcsType;
import com.devtrace.manager.vcs.excel.VcsChangeLogExcelGenerator;
import com.devtrace.manager.vcs.parser.GitLogParser;
import com.devtrace.manager.vcs.parser.SvnLogParser;
import com.devtrace.manager.vcs.parser.VcsLogParser;
import com.devtrace.manager.vcs.service.VcsLogService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 형상관리 로그 분석 업무 규칙을 구현합니다.
 *
 * <p>프로젝트 검증, Git/SVN 파서 선택, 이슈 키 자동 매칭, Excel 생성 흐름을 조율합니다.</p>
 */
@Service
@Transactional(readOnly = true)
public class VcsLogServiceImpl implements VcsLogService {

    private final VcsLogDao vcsLogDao;
    private final ProjectDao projectDao;
    private final GitLogParser gitLogParser;
    private final SvnLogParser svnLogParser;
    private final VcsChangeLogExcelGenerator excelGenerator;

    /**
     * VCS 변경이력 서비스 구현체를 생성한다.
     *
     * @param vcsLogDao 변경이력 SQL 호출 DAO
     * @param projectDao 프로젝트 검증 DAO
     * @param gitLogParser Git 로그 파서
     * @param svnLogParser SVN 로그 파서
     * @param excelGenerator 변경이력 Excel 생성기
     */
    public VcsLogServiceImpl(
            VcsLogDao vcsLogDao,
            ProjectDao projectDao,
            GitLogParser gitLogParser,
            SvnLogParser svnLogParser,
            VcsChangeLogExcelGenerator excelGenerator
    ) {
        this.vcsLogDao = vcsLogDao;
        this.projectDao = projectDao;
        this.gitLogParser = gitLogParser;
        this.svnLogParser = svnLogParser;
        this.excelGenerator = excelGenerator;
    }

    /**
     * VCS 로그를 파싱해 변경이력 미리보기를 생성하고 저장한다.
     *
     * <p>프로젝트별 최신 붙여넣기 결과가 기준 데이터가 되도록 기존 로그/파일/이슈 매핑을 삭제한 뒤 재저장한다.</p>
     *
     * @param request VCS 로그 분석 요청
     * @return 변경이력 응답 목록
     */
    @Override
    @Transactional
    public List<VcsChangeLogResponse> selectChangeLogPreviewList(VcsLogRequest request) {
        validateProject(request.getProjectId());
        List<VcsChangeLogEntity> logs = parseAndPrepare(request);
        vcsLogDao.deleteIssueChangeLogMapsByProjectId(request.getProjectId());
        vcsLogDao.deleteChangeFilesByProjectId(request.getProjectId());
        vcsLogDao.deleteChangeLogsByProjectId(request.getProjectId());
        vcsLogDao.insertChangeLogs(logs);

        List<VcsChangeFileEntity> files = logs.stream()
                .flatMap(log -> log.getChangedFiles().stream())
                .toList();
        if (!files.isEmpty()) {
            vcsLogDao.insertChangeFiles(files);
        }
        for (VcsChangeLogEntity log : logs) {
            if (log.getIssueKeys() != null && !log.getIssueKeys().isEmpty()) {
                vcsLogDao.insertIssueChangeLogMaps(log.getChangeLogId(), log.getIssueKeys());
            }
        }
        return toResponses(logs);
    }

    /**
     * VCS 로그 파싱 결과로 변경이력 Excel 파일을 생성한다.
     *
     * @param request VCS 로그 분석 요청
     * @return xlsx 파일 바이트 배열
     */
    @Override
    public byte[] selectChangeLogExcel(VcsLogRequest request) {
        validateProject(request.getProjectId());
        return excelGenerator.generate(toResponses(parseAndPrepare(request)));
    }

    /**
     * 저장된 변경이력 목록을 조회한다.
     *
     * <p>변경 파일과 매칭된 이슈 키를 함께 붙여 응답 DTO를 완성한다.</p>
     *
     * @param condition 검색 조건
     * @return 변경이력 응답 목록
     */
    @Override
    public List<VcsChangeLogResponse> selectChangeLogList(VcsLogSearchCondition condition) {
        List<VcsChangeLogEntity> logs = vcsLogDao.selectChangeLogList(condition);
        attachChildren(logs);
        return toResponses(logs);
    }

    /**
     * 요청 로그 텍스트를 파싱하고 저장 가능한 엔티티로 준비한다.
     *
     * <p>변경 로그와 변경 파일에 UUID를 부여하고 생성일시를 설정한다.</p>
     *
     * @param request VCS 로그 분석 요청
     * @return 변경 로그 엔티티 목록
     */
    private List<VcsChangeLogEntity> parseAndPrepare(VcsLogRequest request) {
        VcsLogParser parser = selectParser(request.getVcsType());
        LocalDateTime now = DateTimeUtil.now();
        List<VcsChangeLogEntity> logs = parser.parse(request.getProjectId(), request.getLogText());
        for (VcsChangeLogEntity log : logs) {
            log.setChangeLogId(UUID.randomUUID());
            log.setCreatedAt(now);
            for (VcsChangeFileEntity file : log.getChangedFiles()) {
                file.setChangeFileId(UUID.randomUUID());
                file.setChangeLogId(log.getChangeLogId());
            }
        }
        return logs;
    }

    /**
     * VCS 유형에 맞는 파서를 선택한다.
     *
     * @param vcsType VCS 유형
     * @return VCS 로그 파서
     */
    private VcsLogParser selectParser(VcsType vcsType) {
        if (vcsType == VcsType.GIT) {
            return gitLogParser;
        }
        if (vcsType == VcsType.SVN) {
            return svnLogParser;
        }
        throw new BusinessException("지원하지 않는 VCS 유형입니다.", "VCS_TYPE_NOT_SUPPORTED");
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
     * 변경 로그 목록에 변경 파일과 이슈 키 목록을 연결한다.
     *
     * @param logs 자식 데이터가 연결될 변경 로그 목록
     */
    private void attachChildren(List<VcsChangeLogEntity> logs) {
        if (logs.isEmpty()) {
            return;
        }
        List<UUID> changeLogIds = logs.stream().map(VcsChangeLogEntity::getChangeLogId).toList();
        Map<UUID, List<VcsChangeFileEntity>> filesByLogId = vcsLogDao.selectChangeFilesByChangeLogIds(changeLogIds).stream()
                .collect(Collectors.groupingBy(VcsChangeFileEntity::getChangeLogId));
        Map<UUID, List<String>> issueKeysByLogId = vcsLogDao.selectIssueKeysByChangeLogIds(changeLogIds).stream()
                .collect(Collectors.groupingBy(VcsIssueKeyRow::getChangeLogId, Collectors.mapping(VcsIssueKeyRow::getIssueKey, Collectors.toList())));

        for (VcsChangeLogEntity log : logs) {
            log.setChangedFiles(filesByLogId.getOrDefault(log.getChangeLogId(), new ArrayList<>()));
            log.setIssueKeys(issueKeysByLogId.getOrDefault(log.getChangeLogId(), new ArrayList<>()));
        }
    }

    /**
     * 변경 로그 엔티티 목록을 응답 목록으로 변환한다.
     *
     * @param logs 변경 로그 엔티티 목록
     * @return 변경 로그 응답 목록
     */
    private List<VcsChangeLogResponse> toResponses(List<VcsChangeLogEntity> logs) {
        return logs.stream()
                .map(VcsChangeLogEntity::toResponse)
                .toList();
    }
}
