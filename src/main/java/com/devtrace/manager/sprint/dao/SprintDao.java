package com.devtrace.manager.sprint.dao;

import com.devtrace.manager.sprint.dto.SprintBacklogSearchCondition;
import com.devtrace.manager.sprint.dto.SprintAssigneeWorkloadResponse;
import com.devtrace.manager.sprint.dto.SprintBurndownPointResponse;
import com.devtrace.manager.sprint.dto.SprintEntity;
import com.devtrace.manager.sprint.dto.SprintIssueEntity;
import com.devtrace.manager.sprint.dto.SprintIssueResponse;
import com.devtrace.manager.sprint.dto.SprintRiskIssueResponse;
import com.devtrace.manager.sprint.dto.SprintSearchCondition;
import com.devtrace.manager.sprint.dto.SprintStatus;
import com.devtrace.manager.sprint.dto.SprintStatusDistributionResponse;
import com.devtrace.manager.sprint.dto.SprintSummaryResponse;
import com.devtrace.manager.sprint.dto.SprintTestEvidenceRiskResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.ibatis.annotations.Param;

public interface SprintDao {

    void insertSprint(SprintEntity sprint);

    void updateSprint(SprintEntity sprint);

    void updateSprintStatus(
            @Param("sprintId") UUID sprintId,
            @Param("status") SprintStatus status,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    void deleteSprint(UUID sprintId);

    Optional<SprintEntity> selectSprintDetails(UUID sprintId);

    Optional<SprintEntity> selectActiveSprintByProjectIdDetails(UUID projectId);

    List<SprintEntity> selectSprintList(SprintSearchCondition condition);

    List<SprintIssueResponse> selectBacklogIssueList(SprintBacklogSearchCondition condition);

    List<SprintIssueResponse> selectSprintIssueList(UUID sprintId);

    Optional<SprintIssueEntity> selectSprintIssueDetails(
            @Param("sprintId") UUID sprintId,
            @Param("issueId") UUID issueId
    );

    int selectSprintIssueMaxDisplayOrder(UUID sprintId);

    SprintSummaryResponse selectSprintSummaryDetails(
            @Param("sprintId") UUID sprintId,
            @Param("today") LocalDate today
    );

    List<SprintStatusDistributionResponse> selectSprintStatusDistributionList(UUID sprintId);

    List<SprintAssigneeWorkloadResponse> selectSprintAssigneeWorkloadList(UUID sprintId);

    List<SprintRiskIssueResponse> selectSprintRiskIssueList(
            @Param("sprintId") UUID sprintId,
            @Param("today") LocalDate today
    );

    List<SprintTestEvidenceRiskResponse> selectSprintTestEvidenceRiskList(UUID sprintId);

    List<SprintBurndownPointResponse> selectSprintDailySpentList(UUID sprintId);

    void insertSprintIssue(SprintIssueEntity sprintIssue);

    void updateSprintIssueDisplayOrder(
            @Param("sprintId") UUID sprintId,
            @Param("issueId") UUID issueId,
            @Param("displayOrder") int displayOrder
    );

    void deleteSprintIssue(
            @Param("sprintId") UUID sprintId,
            @Param("issueId") UUID issueId
    );
}
