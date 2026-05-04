package com.devtrace.manager.issue.dao;

import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import com.devtrace.manager.issue.dto.IssueStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.ibatis.annotations.Param;

public interface IssueDao {

    void insertIssue(IssueEntity issue);

    void updateIssue(IssueEntity issue);

    void updateIssueStatus(
            @Param("issueId") UUID issueId,
            @Param("status") IssueStatus status,
            @Param("resolvedDate") LocalDate resolvedDate,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    void updateIssueSpentMinutes(
            @Param("issueId") UUID issueId,
            @Param("spentMinutes") int spentMinutes,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    void deleteIssue(UUID issueId);

    Optional<IssueEntity> selectIssueByIdDetails(UUID issueId);

    List<IssueEntity> selectIssueList(IssueSearchCondition condition);
}
