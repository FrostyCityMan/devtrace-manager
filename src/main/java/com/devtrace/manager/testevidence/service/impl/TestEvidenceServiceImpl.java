package com.devtrace.manager.testevidence.service.impl;

import com.devtrace.manager.common.exception.BusinessException;
import com.devtrace.manager.common.util.DateTimeUtil;
import com.devtrace.manager.issue.dao.IssueDao;
import com.devtrace.manager.issue.dto.IssueEntity;
import com.devtrace.manager.project.dao.ProjectDao;
import com.devtrace.manager.testevidence.dao.TestEvidenceDao;
import com.devtrace.manager.testevidence.dto.TestEvidenceEntity;
import com.devtrace.manager.testevidence.dto.TestEvidenceRequest;
import com.devtrace.manager.testevidence.dto.TestEvidenceResponse;
import com.devtrace.manager.testevidence.dto.TestEvidenceScreenshotResponse;
import com.devtrace.manager.testevidence.dto.TestEvidenceSearchCondition;
import com.devtrace.manager.testevidence.service.TestEvidenceService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 기능 테스트 증적 업무 규칙을 구현합니다.
 *
 * <p>프로젝트와 이슈의 정합성을 검증하고, 허용된 이미지 파일만 스크린샷으로 저장합니다.
 * 파일 경로는 업로드 루트 하위로 제한하여 화면 조회와 삭제 시 동일한 보안 기준을 적용합니다.</p>
 */
@Service
@Transactional(readOnly = true)
public class TestEvidenceServiceImpl implements TestEvidenceService {

    private static final Set<String> ALLOWED_SCREENSHOT_EXTENSIONS = Set.of(".png", ".jpg", ".jpeg", ".gif", ".webp");
    private static final String DEFAULT_SCREENSHOT_CONTENT_TYPE = "application/octet-stream";

    private final TestEvidenceDao testEvidenceDao;
    private final ProjectDao projectDao;
    private final IssueDao issueDao;
    private final Path uploadRoot;

    /**
     * 테스트 증적 서비스 구현체를 생성합니다.
     *
     * @param testEvidenceDao 테스트 증적 DAO
     * @param projectDao 프로젝트 검증 DAO
     * @param issueDao 이슈 검증 DAO
     */
    public TestEvidenceServiceImpl(TestEvidenceDao testEvidenceDao, ProjectDao projectDao, IssueDao issueDao) {
        this.testEvidenceDao = testEvidenceDao;
        this.projectDao = projectDao;
        this.issueDao = issueDao;
        this.uploadRoot = Paths.get("uploads", "test-evidences").toAbsolutePath().normalize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TestEvidenceResponse insertTestEvidence(TestEvidenceRequest request) {
        validateRequest(request);

        TestEvidenceEntity testEvidence = new TestEvidenceEntity();
        testEvidence.setTestEvidenceId(UUID.randomUUID());
        applyRequest(testEvidence, request);
        testEvidence.setCreatedAt(DateTimeUtil.now());
        saveScreenshot(testEvidence, request.getScreenshotFile());

        testEvidenceDao.insertTestEvidence(testEvidence);
        return testEvidence.toResponse();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TestEvidenceResponse updateTestEvidence(UUID testEvidenceId, TestEvidenceRequest request) {
        validateRequest(request);

        TestEvidenceEntity testEvidence = selectTestEvidenceEntity(testEvidenceId);
        applyRequest(testEvidence, request);
        testEvidence.setUpdatedAt(DateTimeUtil.now());
        saveScreenshot(testEvidence, request.getScreenshotFile());

        testEvidenceDao.updateTestEvidence(testEvidence);
        return testEvidence.toResponse();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteTestEvidence(UUID testEvidenceId) {
        TestEvidenceEntity testEvidence = selectTestEvidenceEntity(testEvidenceId);
        testEvidenceDao.deleteTestEvidence(testEvidenceId);
        deleteScreenshotFile(testEvidence.getScreenshotFilePath());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestEvidenceResponse selectTestEvidenceDetails(UUID testEvidenceId) {
        return selectTestEvidenceEntity(testEvidenceId).toResponse();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TestEvidenceResponse> selectTestEvidenceList(TestEvidenceSearchCondition condition) {
        TestEvidenceSearchCondition safeCondition = condition == null ? new TestEvidenceSearchCondition() : condition;
        return testEvidenceDao.selectTestEvidenceList(safeCondition).stream()
                .map(TestEvidenceEntity::toResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestEvidenceScreenshotResponse selectTestEvidenceScreenshotDetails(UUID testEvidenceId) {
        TestEvidenceEntity testEvidence = selectTestEvidenceEntity(testEvidenceId);
        if (!StringUtils.hasText(testEvidence.getScreenshotFilePath())) {
            throw new BusinessException("등록된 스크린샷이 없습니다.", "TEST_EVIDENCE_SCREENSHOT_NOT_FOUND");
        }

        Path screenshotPath = Paths.get(testEvidence.getScreenshotFilePath()).toAbsolutePath().normalize();
        if (!screenshotPath.startsWith(uploadRoot) || !Files.exists(screenshotPath)) {
            throw new BusinessException("스크린샷 파일을 찾을 수 없습니다.", "TEST_EVIDENCE_SCREENSHOT_NOT_FOUND");
        }

        String contentType = probeContentType(screenshotPath);
        return new TestEvidenceScreenshotResponse(testEvidence.getScreenshotFileName(), screenshotPath, contentType);
    }

    /**
     * 테스트 증적 엔티티를 조회하고 존재하지 않으면 업무 예외를 발생시킵니다.
     *
     * @param testEvidenceId 테스트 증적 ID
     * @return 테스트 증적 엔티티
     */
    private TestEvidenceEntity selectTestEvidenceEntity(UUID testEvidenceId) {
        return testEvidenceDao.selectTestEvidenceDetails(testEvidenceId)
                .orElseThrow(() -> new BusinessException("테스트 증적을 찾을 수 없습니다.", "TEST_EVIDENCE_NOT_FOUND"));
    }

    /**
     * 테스트 증적 요청의 프로젝트, 이슈 필수값과 소속 관계를 검증합니다.
     *
     * @param request 검증 대상 요청
     */
    private void validateRequest(TestEvidenceRequest request) {
        if (request == null || request.getProjectId() == null || request.getIssueId() == null) {
            throw new BusinessException("테스트 증적 요청이 올바르지 않습니다.", "TEST_EVIDENCE_REQUEST_INVALID");
        }
        projectDao.selectProjectById(request.getProjectId())
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다.", "PROJECT_NOT_FOUND"));
        IssueEntity issue = issueDao.selectIssueByIdDetails(request.getIssueId())
                .orElseThrow(() -> new BusinessException("이슈를 찾을 수 없습니다.", "ISSUE_NOT_FOUND"));
        if (!request.getProjectId().equals(issue.getProjectId())) {
            throw new BusinessException("이슈가 선택한 프로젝트에 속하지 않습니다.", "TEST_EVIDENCE_ISSUE_PROJECT_MISMATCH");
        }
    }

    /**
     * 요청 값을 테스트 증적 엔티티에 반영합니다.
     *
     * @param testEvidence 저장 또는 수정 대상 엔티티
     * @param request 입력 요청
     */
    private void applyRequest(TestEvidenceEntity testEvidence, TestEvidenceRequest request) {
        testEvidence.setProjectId(request.getProjectId());
        testEvidence.setIssueId(request.getIssueId());
        testEvidence.setTestName(request.getTestName());
        testEvidence.setTestTarget(request.getTestTarget());
        testEvidence.setTestProcedure(request.getTestProcedure());
        testEvidence.setExpectedResult(request.getExpectedResult());
        testEvidence.setActualResult(request.getActualResult());
        testEvidence.setResultStatus(request.getResultStatus());
        testEvidence.setTesterId(request.getTesterId() == null ? TestEvidenceRequest.DEFAULT_ADMIN_USER_ID : request.getTesterId());
        testEvidence.setTestedAt(request.getTestedAt() == null ? LocalDateTime.now() : request.getTestedAt());
    }

    /**
     * 업로드된 스크린샷 파일을 검증하고 저장 경로를 엔티티에 기록합니다.
     *
     * @param testEvidence 저장 대상 테스트 증적 엔티티
     * @param screenshotFile 업로드된 스크린샷 파일
     */
    private void saveScreenshot(TestEvidenceEntity testEvidence, MultipartFile screenshotFile) {
        if (screenshotFile == null || screenshotFile.isEmpty()) {
            return;
        }

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNullElse(screenshotFile.getOriginalFilename(), "screenshot"));
        if (originalFileName.contains("..")) {
            throw new BusinessException("스크린샷 파일명이 올바르지 않습니다.", "TEST_EVIDENCE_SCREENSHOT_INVALID");
        }

        String extension = selectExtension(originalFileName);
        if (!ALLOWED_SCREENSHOT_EXTENSIONS.contains(extension)) {
            throw new BusinessException("스크린샷은 png, jpg, jpeg, gif, webp 형식만 허용합니다.", "TEST_EVIDENCE_SCREENSHOT_EXTENSION_NOT_ALLOWED");
        }

        String storedFileName = UUID.randomUUID() + extension;
        Path directory = uploadRoot.resolve(testEvidence.getTestEvidenceId().toString()).normalize();
        Path destination = directory.resolve(storedFileName).normalize();
        if (!destination.startsWith(directory)) {
            throw new BusinessException("스크린샷 저장 경로가 올바르지 않습니다.", "TEST_EVIDENCE_SCREENSHOT_INVALID");
        }

        try {
            Files.createDirectories(directory);
            screenshotFile.transferTo(destination);
        } catch (IOException ex) {
            throw new BusinessException("스크린샷 저장에 실패했습니다.", "TEST_EVIDENCE_SCREENSHOT_SAVE_FAILED");
        }

        testEvidence.setScreenshotFileName(originalFileName);
        testEvidence.setScreenshotFilePath(uploadRoot.resolve(testEvidence.getTestEvidenceId().toString()).resolve(storedFileName).toString());
    }

    /**
     * 파일명에서 소문자 확장자를 추출합니다.
     *
     * @param fileName 원본 파일명
     * @return 점을 포함한 확장자, 확장자가 없으면 빈 문자열
     */
    private String selectExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return fileName.substring(dotIndex).toLowerCase(Locale.ROOT);
    }

    /**
     * 스크린샷 파일의 MIME 타입을 조회합니다.
     *
     * @param screenshotPath 스크린샷 파일 경로
     * @return 조회된 MIME 타입 또는 기본 바이너리 타입
     */
    private String probeContentType(Path screenshotPath) {
        try {
            String contentType = Files.probeContentType(screenshotPath);
            return StringUtils.hasText(contentType) ? contentType : DEFAULT_SCREENSHOT_CONTENT_TYPE;
        } catch (IOException ex) {
            return DEFAULT_SCREENSHOT_CONTENT_TYPE;
        }
    }

    /**
     * 테스트 증적 삭제 후 물리 스크린샷 파일을 정리합니다.
     *
     * <p>파일 삭제 실패는 데이터 삭제를 되돌릴 정도의 업무 실패로 보지 않으므로 조용히 무시합니다.</p>
     *
     * @param screenshotFilePath 삭제할 스크린샷 경로
     */
    private void deleteScreenshotFile(String screenshotFilePath) {
        if (!StringUtils.hasText(screenshotFilePath)) {
            return;
        }
        Path path = Paths.get(screenshotFilePath).toAbsolutePath().normalize();
        if (!path.startsWith(uploadRoot)) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // 스크린샷 파일 삭제 실패는 DB 삭제를 되돌릴 만큼의 업무 오류로 보지 않는다.
        }
    }
}
