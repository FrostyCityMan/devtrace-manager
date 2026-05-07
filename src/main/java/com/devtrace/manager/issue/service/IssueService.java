package com.devtrace.manager.issue.service;

import com.devtrace.manager.issue.dto.IssueRequest;
import com.devtrace.manager.issue.dto.IssueResponse;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import com.devtrace.manager.issue.dto.IssueStatus;
import java.util.List;
import java.util.UUID;

/**
 * 이슈 관리 업무를 담당하는 서비스 계약입니다.
 *
 * <p>이슈는 요구사항, 버그, 테스트, 배포 등 DevTrace Manager의 작업 단위입니다.</p>
 */
public interface IssueService {

    /**
     * 이슈를 등록한다.
     *
     * @param request 이슈 등록 요청
     * @return 등록된 이슈 응답
     */
    IssueResponse insertIssue(IssueRequest request);

    /**
     * 이슈를 수정한다.
     *
     * @param issueId 수정 대상 이슈 ID
     * @param request 이슈 수정 요청
     * @return 수정된 이슈 응답
     */
    IssueResponse updateIssue(UUID issueId, IssueRequest request);

    /**
     * 이슈 상태를 변경한다.
     *
     * @param issueId 상태 변경 대상 이슈 ID
     * @param status 변경할 상태
     * @return 상태 변경 후 이슈 응답
     */
    IssueResponse updateIssueStatus(UUID issueId, IssueStatus status);

    /**
     * 이슈를 삭제한다.
     *
     * @param issueId 삭제 대상 이슈 ID
     */
    void deleteIssue(UUID issueId);

    /**
     * 이슈 상세 정보를 조회한다.
     *
     * @param issueId 조회 대상 이슈 ID
     * @return 이슈 상세 응답
     */
    IssueResponse selectIssueDetails(UUID issueId);

    /**
     * 검색 조건에 맞는 이슈 목록을 조회한다.
     *
     * @param condition 이슈 검색 조건
     * @return 이슈 목록
     */
    List<IssueResponse> selectIssueList(IssueSearchCondition condition);
}
