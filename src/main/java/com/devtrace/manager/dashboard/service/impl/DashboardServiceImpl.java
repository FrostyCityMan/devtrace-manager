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

    public DashboardServiceImpl(DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

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

    private DashboardSummaryResponse summaryOrDefault(DashboardSummaryResponse summary) {
        return summary == null ? new DashboardSummaryResponse() : summary;
    }

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

    private <T> List<T> listOrEmpty(List<T> source) {
        if (source == null) {
            return List.of();
        }
        return source.stream()
                .filter(Objects::nonNull)
                .toList();
    }
}
