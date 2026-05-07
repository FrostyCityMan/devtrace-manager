package com.devtrace.manager.sprint.service;

import com.devtrace.manager.sprint.dto.SprintBacklogSearchCondition;
import com.devtrace.manager.sprint.dto.SprintIssueOrderRequest;
import com.devtrace.manager.sprint.dto.SprintIssueRequest;
import com.devtrace.manager.sprint.dto.SprintIssueResponse;
import com.devtrace.manager.sprint.dto.SprintBurndownPointResponse;
import com.devtrace.manager.sprint.dto.SprintRequest;
import com.devtrace.manager.sprint.dto.SprintResponse;
import com.devtrace.manager.sprint.dto.SprintReportResponse;
import com.devtrace.manager.sprint.dto.SprintSearchCondition;
import com.devtrace.manager.sprint.dto.SprintSummaryResponse;
import java.util.List;
import java.util.UUID;

public interface SprintService {

    SprintResponse insertSprint(SprintRequest request);

    SprintResponse updateSprint(UUID sprintId, SprintRequest request);

    SprintResponse updateSprintStart(UUID sprintId);

    SprintResponse updateSprintClose(UUID sprintId);

    void deleteSprint(UUID sprintId);

    SprintResponse selectSprintDetails(UUID sprintId);

    List<SprintResponse> selectSprintList(SprintSearchCondition condition);

    List<SprintIssueResponse> selectBacklogIssueList(SprintBacklogSearchCondition condition);

    List<SprintIssueResponse> selectSprintIssueList(UUID sprintId);

    SprintSummaryResponse selectSprintSummaryDetails(UUID sprintId);

    SprintReportResponse selectSprintReportDetails(UUID sprintId);

    List<SprintBurndownPointResponse> selectSprintBurndownList(UUID sprintId);

    SprintIssueResponse insertSprintIssue(UUID sprintId, SprintIssueRequest request);

    void updateSprintIssueDisplayOrder(UUID sprintId, UUID issueId, SprintIssueOrderRequest request);

    void deleteSprintIssue(UUID sprintId, UUID issueId);
}
