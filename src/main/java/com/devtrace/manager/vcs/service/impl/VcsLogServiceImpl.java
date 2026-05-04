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

@Service
@Transactional(readOnly = true)
public class VcsLogServiceImpl implements VcsLogService {

    private final VcsLogDao vcsLogDao;
    private final ProjectDao projectDao;
    private final GitLogParser gitLogParser;
    private final SvnLogParser svnLogParser;
    private final VcsChangeLogExcelGenerator excelGenerator;

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

    @Override
    public byte[] selectChangeLogExcel(VcsLogRequest request) {
        validateProject(request.getProjectId());
        return excelGenerator.generate(toResponses(parseAndPrepare(request)));
    }

    @Override
    public List<VcsChangeLogResponse> selectChangeLogList(VcsLogSearchCondition condition) {
        List<VcsChangeLogEntity> logs = vcsLogDao.selectChangeLogList(condition);
        attachChildren(logs);
        return toResponses(logs);
    }

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

    private VcsLogParser selectParser(VcsType vcsType) {
        if (vcsType == VcsType.GIT) {
            return gitLogParser;
        }
        if (vcsType == VcsType.SVN) {
            return svnLogParser;
        }
        throw new BusinessException("지원하지 않는 VCS 유형입니다.", "VCS_TYPE_NOT_SUPPORTED");
    }

    private void validateProject(UUID projectId) {
        projectDao.selectProjectById(projectId)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다.", "PROJECT_NOT_FOUND"));
    }

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

    private List<VcsChangeLogResponse> toResponses(List<VcsChangeLogEntity> logs) {
        return logs.stream()
                .map(VcsChangeLogEntity::toResponse)
                .toList();
    }
}
