package com.devtrace.manager.dashboard.controller;

import com.devtrace.manager.artifact.dto.ArtifactHistoryResponse;
import com.devtrace.manager.artifact.dto.ArtifactSearchCondition;
import com.devtrace.manager.artifact.service.ArtifactService;
import com.devtrace.manager.issue.dto.IssueResponse;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import com.devtrace.manager.issue.service.IssueService;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.dto.ProjectStatus;
import com.devtrace.manager.project.service.ProjectService;
import com.devtrace.manager.vcs.dto.VcsChangeLogResponse;
import com.devtrace.manager.vcs.dto.VcsLogSearchCondition;
import com.devtrace.manager.vcs.service.VcsLogService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ProjectService projectService;
    private final IssueService issueService;
    private final VcsLogService vcsLogService;
    private final ArtifactService artifactService;

    public DashboardController(
            ProjectService projectService,
            IssueService issueService,
            VcsLogService vcsLogService,
            ArtifactService artifactService
    ) {
        this.projectService = projectService;
        this.issueService = issueService;
        this.vcsLogService = vcsLogService;
        this.artifactService = artifactService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<ProjectResponse> projects = projectService.getProjectList(new ProjectSearchCondition());
        List<IssueResponse> issues = issueService.selectIssueList(new IssueSearchCondition());
        List<VcsChangeLogResponse> vcsLogs = vcsLogService.selectChangeLogList(new VcsLogSearchCondition());
        List<ArtifactHistoryResponse> artifacts = artifactService.selectArtifactHistoryList(new ArtifactSearchCondition());
        LocalDate today = LocalDate.now();
        int estimatedMinutes = issues.stream().mapToInt(issue -> safeMinutes(issue.getEstimatedMinutes())).sum();
        int spentMinutes = issues.stream().mapToInt(issue -> safeMinutes(issue.getSpentMinutes())).sum();

        model.addAttribute("totalProjectCount", projects.size());
        model.addAttribute("activeProjectCount", projects.stream().filter(this::isActiveProject).count());
        model.addAttribute("totalIssueCount", issues.size());
        model.addAttribute("completedIssueCount", issues.stream().filter(this::isCompletedIssue).count());
        model.addAttribute("activeIssueCount", issues.stream().filter(this::isActiveIssue).count());
        model.addAttribute("delayedIssueCount", issues.stream().filter(issue -> isDelayedIssue(issue, today)).count());
        model.addAttribute("estimatedHoursText", formatHours(estimatedMinutes));
        model.addAttribute("spentHoursText", formatHours(spentMinutes));
        model.addAttribute("effortDiffText", formatHours(spentMinutes - estimatedMinutes));
        model.addAttribute("recentIssues", selectRecentIssueList(issues));
        model.addAttribute("recentVcsLogs", selectRecentVcsLogList(vcsLogs));
        model.addAttribute("recentArtifacts", selectRecentArtifactList(artifacts));
        return "dashboard/index";
    }

    private boolean isActiveProject(ProjectResponse project) {
        ProjectStatus status = project.getStatus();
        return status == ProjectStatus.ANALYSIS
                || status == ProjectStatus.DESIGN
                || status == ProjectStatus.DEVELOPMENT
                || status == ProjectStatus.TEST
                || status == ProjectStatus.INSPECTION
                || status == ProjectStatus.OPERATION;
    }

    private boolean isCompletedIssue(IssueResponse issue) {
        return issue.getStatus() != null && issue.getStatus().isCompleted();
    }

    private boolean isActiveIssue(IssueResponse issue) {
        return issue.getStatus() != null && issue.getStatus().isActive();
    }

    private boolean isDelayedIssue(IssueResponse issue, LocalDate today) {
        return issue.getDueDate() != null && issue.getDueDate().isBefore(today) && !isCompletedIssue(issue);
    }

    private List<IssueResponse> selectRecentIssueList(List<IssueResponse> issues) {
        return issues.stream()
                .sorted(Comparator.comparing(this::issueUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();
    }

    private List<VcsChangeLogResponse> selectRecentVcsLogList(List<VcsChangeLogResponse> vcsLogs) {
        return vcsLogs.stream()
                .sorted(Comparator.comparing(VcsChangeLogResponse::getChangedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();
    }

    private List<ArtifactHistoryResponse> selectRecentArtifactList(List<ArtifactHistoryResponse> artifacts) {
        return artifacts.stream()
                .sorted(Comparator.comparing(ArtifactHistoryResponse::getGeneratedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();
    }

    private LocalDateTime issueUpdatedAt(IssueResponse issue) {
        return issue.getUpdatedAt() == null ? issue.getCreatedAt() : issue.getUpdatedAt();
    }

    private int safeMinutes(Integer minutes) {
        return minutes == null ? 0 : minutes;
    }

    private String formatHours(int minutes) {
        return String.format(Locale.KOREA, "%.1fh", minutes / 60.0);
    }
}
