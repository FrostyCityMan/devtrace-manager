package com.devtrace.manager.issue.service;

import com.devtrace.manager.issue.dto.IssueRequest;
import com.devtrace.manager.issue.dto.IssueResponse;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import java.util.List;
import java.util.UUID;

public interface IssueService {

    IssueResponse createIssue(IssueRequest request);

    IssueResponse updateIssue(UUID issueId, IssueRequest request);

    void deleteIssue(UUID issueId);

    IssueResponse getIssue(UUID issueId);

    List<IssueResponse> getIssueList(IssueSearchCondition condition);
}
