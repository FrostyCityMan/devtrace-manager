package com.devtrace.manager.testevidence.dto;

import java.nio.file.Path;

public class TestEvidenceScreenshotResponse {

    private final String fileName;
    private final Path filePath;
    private final String contentType;

    public TestEvidenceScreenshotResponse(String fileName, Path filePath, String contentType) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getContentType() {
        return contentType;
    }
}
