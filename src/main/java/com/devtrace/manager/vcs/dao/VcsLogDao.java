package com.devtrace.manager.vcs.dao;

import com.devtrace.manager.vcs.dto.VcsChangeFileEntity;
import com.devtrace.manager.vcs.dto.VcsChangeLogEntity;
import com.devtrace.manager.vcs.dto.VcsLogSearchCondition;
import java.util.List;
import java.util.UUID;
import org.apache.ibatis.annotations.Param;

public interface VcsLogDao {

    void insertChangeLogs(@Param("changeLogs") List<VcsChangeLogEntity> changeLogs);

    void insertChangeFiles(@Param("changeFiles") List<VcsChangeFileEntity> changeFiles);

    void insertIssueChangeLogMaps(@Param("changeLogId") UUID changeLogId, @Param("issueKeys") List<String> issueKeys);

    void deleteIssueChangeLogMapsByProjectId(UUID projectId);

    void deleteChangeFilesByProjectId(UUID projectId);

    void deleteChangeLogsByProjectId(UUID projectId);

    List<VcsChangeLogEntity> selectChangeLogList(VcsLogSearchCondition condition);

    List<VcsChangeFileEntity> selectChangeFilesByChangeLogIds(@Param("changeLogIds") List<UUID> changeLogIds);

    List<VcsIssueKeyRow> selectIssueKeysByChangeLogIds(@Param("changeLogIds") List<UUID> changeLogIds);
}
