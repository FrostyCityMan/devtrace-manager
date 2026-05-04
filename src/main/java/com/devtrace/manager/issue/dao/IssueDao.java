package com.devtrace.manager.issue.dao;

import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IssueDao {

    void insertIssue(IssueEntity issue);

    void updateIssue(IssueEntity issue);

    void deleteIssue(UUID issueId);

    Optional<IssueEntity> selectIssueById(UUID issueId);

    List<IssueEntity> selectIssueList(IssueSearchCondition condition);
}
