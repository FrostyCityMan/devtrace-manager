package com.devtrace.manager.testevidence.controller;

import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.issue.dto.IssueResponse;
import com.devtrace.manager.issue.dto.IssueSearchCondition;
import com.devtrace.manager.issue.service.IssueService;
import com.devtrace.manager.project.dto.ProjectResponse;
import com.devtrace.manager.project.dto.ProjectSearchCondition;
import com.devtrace.manager.project.service.ProjectService;
import com.devtrace.manager.testevidence.dto.TestEvidenceRequest;
import com.devtrace.manager.testevidence.dto.TestEvidenceResponse;
import com.devtrace.manager.testevidence.dto.TestEvidenceResult;
import com.devtrace.manager.testevidence.dto.TestEvidenceScreenshotResponse;
import com.devtrace.manager.testevidence.dto.TestEvidenceSearchCondition;
import com.devtrace.manager.testevidence.dto.TestEvidenceSummary;
import com.devtrace.manager.testevidence.service.TestEvidenceService;
import jakarta.validation.Valid;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test-evidences")
public class TestEvidenceController {

    private final TestEvidenceService testEvidenceService;
    private final ProjectService projectService;
    private final IssueService issueService;

    public TestEvidenceController(TestEvidenceService testEvidenceService, ProjectService projectService, IssueService issueService) {
        this.testEvidenceService = testEvidenceService;
        this.projectService = projectService;
        this.issueService = issueService;
    }

    @ModelAttribute("projects")
    public List<ProjectResponse> projects() {
        return projectService.getProjectList(new ProjectSearchCondition());
    }

    @ModelAttribute("issues")
    public List<IssueResponse> issues() {
        return issueService.selectIssueList(new IssueSearchCondition());
    }

    @ModelAttribute("resultStatuses")
    public TestEvidenceResult[] resultStatuses() {
        return TestEvidenceResult.values();
    }

    @GetMapping
    public String list(@ModelAttribute TestEvidenceSearchCondition condition, Model model) {
        List<TestEvidenceResponse> evidences = testEvidenceService.selectTestEvidenceList(condition);
        model.addAttribute("evidences", evidences);
        model.addAttribute("summary", TestEvidenceSummary.from(evidences));
        model.addAttribute("condition", condition);
        return "testevidence/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("testEvidence", createDefaultRequest());
        return "testevidence/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("testEvidence") TestEvidenceRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "testevidence/form";
        }
        try {
            testEvidenceService.insertTestEvidence(request);
        } catch (BusinessException ex) {
            model.addAttribute("testEvidenceError", ex.getMessage());
            return "testevidence/form";
        }
        return "redirect:/test-evidences";
    }

    @GetMapping("/{testEvidenceId}")
    public String detail(@PathVariable UUID testEvidenceId, Model model) {
        model.addAttribute("testEvidence", testEvidenceService.selectTestEvidenceDetails(testEvidenceId));
        return "testevidence/detail";
    }

    @GetMapping("/{testEvidenceId}/edit")
    public String editForm(@PathVariable UUID testEvidenceId, Model model) {
        TestEvidenceResponse testEvidence = testEvidenceService.selectTestEvidenceDetails(testEvidenceId);
        model.addAttribute("testEvidenceId", testEvidenceId);
        model.addAttribute("testEvidence", testEvidence.toRequest());
        model.addAttribute("currentScreenshotFileName", testEvidence.getScreenshotFileName());
        return "testevidence/form";
    }

    @PostMapping("/{testEvidenceId}")
    public String update(
            @PathVariable UUID testEvidenceId,
            @Valid @ModelAttribute("testEvidence") TestEvidenceRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("testEvidenceId", testEvidenceId);
            addCurrentScreenshotFileName(model, testEvidenceId);
            return "testevidence/form";
        }
        try {
            testEvidenceService.updateTestEvidence(testEvidenceId, request);
        } catch (BusinessException ex) {
            model.addAttribute("testEvidenceId", testEvidenceId);
            model.addAttribute("testEvidenceError", ex.getMessage());
            addCurrentScreenshotFileName(model, testEvidenceId);
            return "testevidence/form";
        }
        return "redirect:/test-evidences/" + testEvidenceId;
    }

    @PostMapping("/{testEvidenceId}/delete")
    public String delete(@PathVariable UUID testEvidenceId) {
        testEvidenceService.deleteTestEvidence(testEvidenceId);
        return "redirect:/test-evidences";
    }

    @GetMapping("/{testEvidenceId}/screenshot")
    public ResponseEntity<Resource> screenshot(@PathVariable UUID testEvidenceId) throws MalformedURLException {
        TestEvidenceScreenshotResponse screenshot = testEvidenceService.selectTestEvidenceScreenshotDetails(testEvidenceId);
        Resource resource = new UrlResource(screenshot.getFilePath().toUri());
        String fileName = URLEncoder.encode(screenshot.getFileName(), StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(screenshot.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + fileName)
                .body(resource);
    }

    private TestEvidenceRequest createDefaultRequest() {
        TestEvidenceRequest request = new TestEvidenceRequest();
        request.setResultStatus(TestEvidenceResult.SUCCESS);
        request.setTestedAt(LocalDateTime.now().withSecond(0).withNano(0));
        return request;
    }

    private void addCurrentScreenshotFileName(Model model, UUID testEvidenceId) {
        TestEvidenceResponse testEvidence = testEvidenceService.selectTestEvidenceDetails(testEvidenceId);
        model.addAttribute("currentScreenshotFileName", testEvidence.getScreenshotFileName());
    }
}
