package com.devtrace.manager.vcs.dao;

import com.devtrace.manager.vcs.dto.VcsChangeFileEntity;
import com.devtrace.manager.vcs.dto.VcsChangeLogEntity;
import com.devtrace.manager.vcs.dto.VcsLogSearchCondition;
import java.util.List;
import java.util.UUID;
import org.apache.ibatis.annotations.Param;

/**
 * 형상관리 변경이력 저장소에 접근하는 MyBatis DAO입니다.
 *
 * <p>변경이력, 변경 파일, 이슈 매핑 저장과 조회 SQL을 제공합니다.</p>
 */
public interface VcsLogDao {

    /**
     * 변경 로그 목록을 일괄 저장한다.
     *
     * @param changeLogs 저장할 변경 로그 목록
     */
    void insertChangeLogs(@Param("changeLogs") List<VcsChangeLogEntity> changeLogs);

    /**
     * 변경 파일 목록을 일괄 저장한다.
     *
     * @param changeFiles 저장할 변경 파일 목록
     */
    void insertChangeFiles(@Param("changeFiles") List<VcsChangeFileEntity> changeFiles);

    /**
     * 변경 로그와 이슈 키 매핑을 저장한다.
     *
     * @param changeLogId 변경 로그 ID
     * @param issueKeys 매칭된 이슈 키 목록
     */
    void insertIssueChangeLogMaps(@Param("changeLogId") UUID changeLogId, @Param("issueKeys") List<String> issueKeys);

    /**
     * 프로젝트 기준 이슈-변경로그 매핑을 삭제한다.
     *
     * @param projectId 프로젝트 ID
     */
    void deleteIssueChangeLogMapsByProjectId(UUID projectId);

    /**
     * 프로젝트 기준 변경 파일을 삭제한다.
     *
     * @param projectId 프로젝트 ID
     */
    void deleteChangeFilesByProjectId(UUID projectId);

    /**
     * 프로젝트 기준 변경 로그를 삭제한다.
     *
     * @param projectId 프로젝트 ID
     */
    void deleteChangeLogsByProjectId(UUID projectId);

    /**
     * 검색 조건에 맞는 변경 로그 목록을 조회한다.
     *
     * @param condition 변경이력 검색 조건
     * @return 변경 로그 엔티티 목록
     */
    List<VcsChangeLogEntity> selectChangeLogList(VcsLogSearchCondition condition);

    /**
     * 변경 로그 ID 목록에 연결된 변경 파일 목록을 조회한다.
     *
     * @param changeLogIds 변경 로그 ID 목록
     * @return 변경 파일 엔티티 목록
     */
    List<VcsChangeFileEntity> selectChangeFilesByChangeLogIds(@Param("changeLogIds") List<UUID> changeLogIds);

    /**
     * 변경 로그 ID 목록에 연결된 이슈 키 목록을 조회한다.
     *
     * @param changeLogIds 변경 로그 ID 목록
     * @return 변경 로그별 이슈 키 행 목록
     */
    List<VcsIssueKeyRow> selectIssueKeysByChangeLogIds(@Param("changeLogIds") List<UUID> changeLogIds);
}
