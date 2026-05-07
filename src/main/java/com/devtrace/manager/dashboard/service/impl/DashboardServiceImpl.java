package com.devtrace.manager.dashboard.service.impl;

import com.devtrace.manager.dashboard.dao.DashboardDao;
import com.devtrace.manager.dashboard.dto.DashboardBoardSummaryResponse;
import com.devtrace.manager.dashboard.dto.DashboardResponse;
import com.devtrace.manager.dashboard.dto.DashboardSummaryResponse;
import com.devtrace.manager.dashboard.service.DashboardService;
import com.devtrace.manager.issue.dto.IssueStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 통합 운영 대시보드 조회 업무를 구현합니다.
 *
 * <p>DAO의 개별 조회 결과를 화면이 바로 사용할 수 있는 단일 응답으로 조립합니다.
 * 칸반 상태 요약은 고정 상태 순서를 유지하여 데이터가 없는 컬럼도 화면에서 흔들리지 않게 합니다.</p>
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private static final List<IssueStatus> BOARD_STATUSES = List.of(
            IssueStatus.REGISTERED,
            IssueStatus.ANALYZING,
            IssueStatus.IN_PROGRESS,
            IssueStatus.DEV_DONE,
            IssueStatus.TESTING,
            IssueStatus.DONE
    );

    private final DashboardDao dashboardDao;

    /**
     * 대시보드 서비스 구현체를 생성합니다.
     *
     * @param dashboardDao 대시보드 전용 조회 DAO
     */
    public DashboardServiceImpl(DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DashboardResponse selectDashboardDetails() {
        LocalDate today = LocalDate.now();
        DashboardResponse dashboard = new DashboardResponse();
        dashboard.setSummary(summaryOrDefault(dashboardDao.selectDashboardSummaryDetails(today)));
        dashboard.setTodayDueIssues(listOrEmpty(dashboardDao.selectTodayDueIssueList(today)));
        dashboard.setDelayedIssues(listOrEmpty(dashboardDao.selectDelayedIssueList(today)));
        dashboard.setDelayedWbsTasks(listOrEmpty(dashboardDao.selectDelayedWbsTaskList(today)));
        dashboard.setFailedTestEvidences(listOrEmpty(dashboardDao.selectFailedTestEvidenceList()));
        dashboard.setRecentChangeLogs(listOrEmpty(dashboardDao.selectRecentChangeLogList()));
        dashboard.setProjectHealthList(listOrEmpty(dashboardDao.selectProjectHealthList(today)));
        dashboard.setBoardSummaryList(fixedBoardSummaryList(dashboardDao.selectBoardSummaryList()));
        dashboard.setRecentDoneIssues(listOrEmpty(dashboardDao.selectRecentDoneIssueList()));
        dashboard.setRecentTestEvidences(listOrEmpty(dashboardDao.selectRecentTestEvidenceList()));
        dashboard.setRecentArtifacts(listOrEmpty(dashboardDao.selectRecentArtifactList()));
        return dashboard;
    }

    /**
     * 요약 조회 결과가 없을 때 빈 응답 객체로 보정합니다.
     *
     * @param summary DAO 조회 결과
     * @return null이 아닌 요약 응답
     */
    private DashboardSummaryResponse summaryOrDefault(DashboardSummaryResponse summary) {
        return summary == null ? new DashboardSummaryResponse() : summary;
    }

    /**
     * 칸반 컬럼 순서에 맞춰 상태별 이슈 수 목록을 고정 길이로 보정합니다.
     *
     * @param source DAO 조회 결과
     * @return 고정 상태 순서의 칸반 요약 목록
     */
    private List<DashboardBoardSummaryResponse> fixedBoardSummaryList(List<DashboardBoardSummaryResponse> source) {
        Map<IssueStatus, DashboardBoardSummaryResponse> countMap = listOrEmpty(source).stream()
                .filter(item -> item.getStatus() != null)
                .collect(Collectors.toMap(DashboardBoardSummaryResponse::getStatus, Function.identity(), (left, right) -> left));
        return BOARD_STATUSES.stream()
                .map(status -> {
                    DashboardBoardSummaryResponse item = countMap.get(status);
                    return item == null ? new DashboardBoardSummaryResponse(status, 0) : item;
                })
                .toList();
    }

    /**
     * 목록 조회 결과에서 null 목록과 null 항목을 제거합니다.
     *
     * @param source 원본 목록
     * @param <T> 목록 항목 타입
     * @return null이 아닌 항목만 포함한 목록
     */
    private <T> List<T> listOrEmpty(List<T> source) {
        if (source == null) {
            return List.of();
        }
        return source.stream()
                .filter(Objects::nonNull)
                .toList();
    }
}
