package com.devtrace.manager.issue.service;

import com.devtrace.manager.issue.dto.IssueRequest;
import com.devtrace.manager.issue.dto.IssueResponse;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import com.devtrace.manager.issue.dto.IssueStatus;
import java.util.List;
import java.util.UUID;

public interface IssueService {

    IssueResponse insertIssue(IssueRequest request);

    IssueResponse updateIssue(UUID issueId, IssueRequest request);

    IssueResponse updateIssueStatus(UUID issueId, IssueStatus status);

    void deleteIssue(UUID issueId);

    IssueResponse selectIssueDetails(UUID issueId);

    List<IssueResponse> selectIssueList(IssueSearchCondition condition);
}
