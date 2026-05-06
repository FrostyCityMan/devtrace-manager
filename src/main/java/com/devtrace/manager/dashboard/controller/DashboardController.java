package com.devtrace.manager.dashboard.controller;

import com.devtrace.manager.dashboard.dto.DashboardResponse;
import com.devtrace.manager.dashboard.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

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
