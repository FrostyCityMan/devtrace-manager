package com.devtrace.manager.common.response;

public class ErrorResponse {

    private final boolean success;
    private final String message;
    private final String errorCode;

    private ErrorResponse(String message, String errorCode) {
        this.success = false;
        this.message = message;
        this.errorCode = errorCode;
    }

    public static ErrorResponse of(String message, String errorCode) {
        return new ErrorResponse(message, errorCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
