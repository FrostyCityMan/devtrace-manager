package com.devtrace.manager.dashboard.controller;

import com.devtrace.manager.dashboard.dto.DashboardResponse;
import com.devtrace.manager.dashboard.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 애플리케이션 첫 화면과 통합 운영 대시보드를 제공하는 컨트롤러입니다.
 *
 * <p>대시보드는 사용자가 오늘 확인해야 할 위험, 지연, 변경, 산출물 현황을
 * 한 화면에서 판단하도록 구성됩니다.</p>
 */
@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 대시보드 컨트롤러를 생성합니다.
     *
     * @param dashboardService 대시보드 조회 서비스
     */
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 루트 요청을 대시보드로 이동시킵니다.
     *
     * @return 대시보드 리다이렉트 경로
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    /**
     * 통합 운영 대시보드 화면을 표시합니다.
     *
     * @param model 화면 모델
     * @return 대시보드 템플릿
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardResponse dashboard = dashboardService.selectDashboardDetails();
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("summary", dashboard.getSummary());
        model.addAttribute("todayDueIssues", dashboard.getTodayDueIssues());
        model.addAttribute("delayedIssues", dashboard.getDelayedIssues());
        model.addAttribute("delayedWbsTasks", dashboard.getDelayedWbsTasks());
        model.addAttribute("failedTestEvidences", dashboard.getFailedTestEvidences());
        model.addAttribute("recentChangeLogs", dashboard.getRecentChangeLogs());
        model.addAttribute("projectHealthList", dashboard.getProjectHealthList());
        model.addAttribute("boardSummaryList", dashboard.getBoardSummaryList());
        model.addAttribute("recentDoneIssues", dashboard.getRecentDoneIssues());
        model.addAttribute("recentTestEvidences", dashboard.getRecentTestEvidences());
        model.addAttribute("recentArtifacts", dashboard.getRecentArtifacts());
        return "dashboard/index";
    }
}
