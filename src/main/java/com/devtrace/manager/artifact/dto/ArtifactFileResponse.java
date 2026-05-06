package com.devtrace.manager.artifact.dto;

public class ArtifactFileResponse {

    private final String fileName;
    private final String contentType;
    private final byte[] content;

    public ArtifactFileResponse(String fileName, String contentType, byte[] content) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }
}
