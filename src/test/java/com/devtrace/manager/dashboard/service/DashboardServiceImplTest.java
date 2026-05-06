package com.devtrace.manager.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.devtrace.manager.dashboard.dao.DashboardDao;
import com.devtrace.manager.dashboard.dto.DashboardBoardSummaryResponse;
import com.devtrace.manager.dashboard.dto.DashboardResponse;
import com.devtrace.manager.dashboard.dto.DashboardSummaryResponse;
import com.devtrace.manager.dashboard.service.impl.DashboardServiceImpl;
import com.devtrace.manager.issue.dto.IssueStatus;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private DashboardDao dashboardDao;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardServiceImpl(dashboardDao);
    }

    @Test
    void selectDashboardDetailsReturnsFixedBoardColumnsAndSummary() {
        DashboardSummaryResponse summary = new DashboardSummaryResponse();
        summary.setTotalProjectCount(3);
        summary.setDelayedIssueCount(2);
        when(dashboardDao.selectDashboardSummaryDetails(ArgumentMatchers.any(LocalDate.class))).thenReturn(summary);
        when(dashboardDao.selectBoardSummaryList()).thenReturn(List.of(
                new DashboardBoardSummaryResponse(IssueStatus.IN_PROGRESS, 4),
                new DashboardBoardSummaryResponse(IssueStatus.DONE, 1)
        ));

        DashboardResponse dashboard = dashboardService.selectDashboardDetails();

        assertThat(dashboard.getSummary().getTotalProjectCount()).isEqualTo(3);
        assertThat(dashboard.getSummary().getDelayedIssueCount()).isEqualTo(2);
        assertThat(dashboard.getBoardSummaryList()).hasSize(6);
        assertThat(dashboard.getBoardSummaryList())
                .extracting(DashboardBoardSummaryResponse::getStatus)
                .containsExactly(
                        IssueStatus.REGISTERED,
                        IssueStatus.ANALYZING,
                        IssueStatus.IN_PROGRESS,
                        IssueStatus.DEV_DONE,
                        IssueStatus.TESTING,
                        IssueStatus.DONE
                );
        assertThat(dashboard.getBoardSummaryList().get(2).getIssueCount()).isEqualTo(4);
        assertThat(dashboard.getBoardSummaryList().get(5).getIssueCount()).isEqualTo(1);
        assertThat(dashboard.getBoardSummaryList().get(0).getIssueCount()).isZero();
    }

    @Test
    void selectDashboardDetailsUsesEmptyListsWhenDaoReturnsNull() {
        when(dashboardDao.selectDashboardSummaryDetails(ArgumentMatchers.any(LocalDate.class))).thenReturn(null);

        DashboardResponse dashboard = dashboardService.selectDashboardDetails();

        assertThat(dashboard.getSummary()).isNotNull();
        assertThat(dashboard.getTodayDueIssues()).isEmpty();
        assertThat(dashboard.getDelayedIssues()).isEmpty();
        assertThat(dashboard.getDelayedWbsTasks()).isEmpty();
        assertThat(dashboard.getFailedTestEvidences()).isEmpty();
        assertThat(dashboard.getRecentChangeLogs()).isEmpty();
        assertThat(dashboard.getProjectHealthList()).isEmpty();
        assertThat(dashboard.getRecentDoneIssues()).isEmpty();
        assertThat(dashboard.getRecentTestEvidences()).isEmpty();
        assertThat(dashboard.getRecentArtifacts()).isEmpty();
        assertThat(dashboard.getBoardSummaryList()).hasSize(6);
    }
}
