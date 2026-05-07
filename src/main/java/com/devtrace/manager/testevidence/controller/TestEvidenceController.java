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

/**
 * Thymeleaf 기반 테스트 증적 관리 화면을 제공하는 컨트롤러입니다.
 *
 * <p>목록, 등록, 상세, 수정, 삭제, 스크린샷 조회 화면 흐름을 담당하며,
 * 테스트 결과 보고서 생성을 위한 증적 데이터를 사용자가 관리할 수 있게 합니다.</p>
 */
@Controller
@RequestMapping("/test-evidences")
public class TestEvidenceController {

    private final TestEvidenceService testEvidenceService;
    private final ProjectService projectService;
    private final IssueService issueService;

    /**
     * 테스트 증적 화면 컨트롤러를 생성합니다.
     *
     * @param testEvidenceService 테스트 증적 서비스
     * @param projectService 프로젝트 선택 목록 조회 서비스
     * @param issueService 이슈 선택 목록 조회 서비스
     */
    public TestEvidenceController(TestEvidenceService testEvidenceService, ProjectService projectService, IssueService issueService) {
        this.testEvidenceService = testEvidenceService;
        this.projectService = projectService;
        this.issueService = issueService;
    }

    /**
     * 테스트 증적 화면의 프로젝트 선택 목록을 제공합니다.
     *
     * @return 전체 프로젝트 목록
     */
    @ModelAttribute("projects")
    public List<ProjectResponse> projects() {
        return projectService.getProjectList(new ProjectSearchCondition());
    }

    /**
     * 테스트 증적 화면의 이슈 선택 목록을 제공합니다.
     *
     * @return 전체 이슈 목록
     */
    @ModelAttribute("issues")
    public List<IssueResponse> issues() {
        return issueService.selectIssueList(new IssueSearchCondition());
    }

    /**
     * 테스트 판정 선택값을 제공합니다.
     *
     * @return 테스트 증적 판정 배열
     */
    @ModelAttribute("resultStatuses")
    public TestEvidenceResult[] resultStatuses() {
        return TestEvidenceResult.values();
    }

    /**
     * 테스트 증적 목록과 판정 요약을 표시합니다.
     *
     * @param condition 검색 조건
     * @param model 화면 모델
     * @return 테스트 증적 목록 템플릿
     */
    @GetMapping
    public String list(@ModelAttribute TestEvidenceSearchCondition condition, Model model) {
        List<TestEvidenceResponse> evidences = testEvidenceService.selectTestEvidenceList(condition);
        model.addAttribute("evidences", evidences);
        model.addAttribute("summary", TestEvidenceSummary.from(evidences));
        model.addAttribute("condition", condition);
        return "testevidence/list";
    }

    /**
     * 테스트 증적 등록 화면을 표시합니다.
     *
     * @param model 화면 모델
     * @return 테스트 증적 입력 템플릿
     */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("testEvidence", createDefaultRequest());
        return "testevidence/form";
    }

    /**
     * 테스트 증적을 등록합니다.
     *
     * @param request 등록 요청
     * @param bindingResult 입력 검증 결과
     * @param model 화면 모델
     * @return 성공 시 목록 화면으로 이동, 실패 시 입력 화면
     */
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

    /**
     * 테스트 증적 상세 화면을 표시합니다.
     *
     * @param testEvidenceId 테스트 증적 ID
     * @param model 화면 모델
     * @return 테스트 증적 상세 템플릿
     */
    @GetMapping("/{testEvidenceId}")
    public String detail(@PathVariable UUID testEvidenceId, Model model) {
        model.addAttribute("testEvidence", testEvidenceService.selectTestEvidenceDetails(testEvidenceId));
        return "testevidence/detail";
    }

    /**
     * 테스트 증적 수정 화면을 표시합니다.
     *
     * @param testEvidenceId 테스트 증적 ID
     * @param model 화면 모델
     * @return 테스트 증적 입력 템플릿
     */
    @GetMapping("/{testEvidenceId}/edit")
    public String editForm(@PathVariable UUID testEvidenceId, Model model) {
        TestEvidenceResponse testEvidence = testEvidenceService.selectTestEvidenceDetails(testEvidenceId);
        model.addAttribute("testEvidenceId", testEvidenceId);
        model.addAttribute("testEvidence", testEvidence.toRequest());
        model.addAttribute("currentScreenshotFileName", testEvidence.getScreenshotFileName());
        return "testevidence/form";
    }

    /**
     * 테스트 증적을 수정합니다.
     *
     * @param testEvidenceId 테스트 증적 ID
     * @param request 수정 요청
     * @param bindingResult 입력 검증 결과
     * @param model 화면 모델
     * @return 성공 시 상세 화면으로 이동, 실패 시 입력 화면
     */
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

    /**
     * 테스트 증적을 삭제합니다.
     *
     * @param testEvidenceId 테스트 증적 ID
     * @return 테스트 증적 목록 이동 경로
     */
    @PostMapping("/{testEvidenceId}/delete")
    public String delete(@PathVariable UUID testEvidenceId) {
        testEvidenceService.deleteTestEvidence(testEvidenceId);
        return "redirect:/test-evidences";
    }

    /**
     * 테스트 증적에 첨부된 스크린샷을 브라우저에서 조회합니다.
     *
     * @param testEvidenceId 테스트 증적 ID
     * @return 스크린샷 리소스 응답
     * @throws MalformedURLException 저장 경로를 URL 리소스로 변환할 수 없는 경우
     */
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

    /**
     * 신규 테스트 증적 입력 화면에 사용할 기본값을 생성합니다.
     *
     * @return 기본 테스트 증적 요청
     */
    private TestEvidenceRequest createDefaultRequest() {
        TestEvidenceRequest request = new TestEvidenceRequest();
        request.setResultStatus(TestEvidenceResult.SUCCESS);
        request.setTestedAt(LocalDateTime.now().withSecond(0).withNano(0));
        return request;
    }

    /**
     * 수정 화면에서 기존 스크린샷 파일명을 다시 표시합니다.
     *
     * @param model 화면 모델
     * @param testEvidenceId 테스트 증적 ID
     */
    private void addCurrentScreenshotFileName(Model model, UUID testEvidenceId) {
        TestEvidenceResponse testEvidence = testEvidenceService.selectTestEvidenceDetails(testEvidenceId);
        model.addAttribute("currentScreenshotFileName", testEvidence.getScreenshotFileName());
    }
}
