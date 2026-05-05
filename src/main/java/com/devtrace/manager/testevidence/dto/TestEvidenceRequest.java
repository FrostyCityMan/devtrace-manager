package com.devtrace.manager.testevidence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

public class TestEvidenceRequest {

    public static final UUID DEFAULT_ADMIN_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @NotNull(message = "프로젝트는 필수입니다.")
    private UUID projectId;

    @NotNull(message = "이슈는 필수입니다.")
    private UUID issueId;

    @NotBlank(message = "테스트명은 필수입니다.")
    @Size(max = 300, message = "테스트명은 300자 이하로 입력하십시오.")
    private String testName;

    @NotBlank(message = "테스트 대상은 필수입니다.")
    @Size(max = 500, message = "테스트 대상은 500자 이하로 입력하십시오.")
    private String testTarget;

    @NotBlank(message = "테스트 절차는 필수입니다.")
    private String testProcedure;

    @NotBlank(message = "기대 결과는 필수입니다.")
    private String expectedResult;

    @NotBlank(message = "실제 결과는 필수입니다.")
    private String actualResult;

    @NotNull(message = "판정은 필수입니다.")
    private TestEvidenceResult resultStatus = TestEvidenceResult.SUCCESS;

    private UUID testerId = DEFAULT_ADMIN_USER_ID;

    @NotNull(message = "테스트 수행일시는 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime testedAt;

    private MultipartFile screenshotFile;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestTarget() {
        return testTarget;
    }

    public void setTestTarget(String testTarget) {
        this.testTarget = testTarget;
    }

    public String getTestProcedure() {
        return testProcedure;
    }

    public void setTestProcedure(String testProcedure) {
        this.testProcedure = testProcedure;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getActualResult() {
        return actualResult;
    }

    public void setActualResult(String actualResult) {
        this.actualResult = actualResult;
    }

    public TestEvidenceResult getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(TestEvidenceResult resultStatus) {
        this.resultStatus = resultStatus;
    }

    public UUID getTesterId() {
        return testerId;
    }

    public void setTesterId(UUID testerId) {
        this.testerId = testerId;
    }

    public LocalDateTime getTestedAt() {
        return testedAt;
    }

    public void setTestedAt(LocalDateTime testedAt) {
        this.testedAt = testedAt;
    }

    public MultipartFile getScreenshotFile() {
        return screenshotFile;
    }

    public void setScreenshotFile(MultipartFile screenshotFile) {
        this.screenshotFile = screenshotFile;
    }
}
