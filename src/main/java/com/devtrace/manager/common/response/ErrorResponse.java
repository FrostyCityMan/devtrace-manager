package com.devtrace.manager.common.response;

/**
 * REST API 오류 응답의 공통 래퍼입니다.
 *
 * <p>클라이언트가 사용자 메시지와 업무 오류 코드를 일관되게 처리하도록
 * 실패 응답 구조를 고정합니다.</p>
 */
public class ErrorResponse {

    private final boolean success;
    private final String message;
    private final String errorCode;

    /**
     * 오류 응답을 생성합니다.
     *
     * @param message 오류 메시지
     * @param errorCode 오류 코드
     */
    private ErrorResponse(String message, String errorCode) {
        this.success = false;
        this.message = message;
        this.errorCode = errorCode;
    }

    /**
     * 오류 응답 인스턴스를 생성합니다.
     *
     * @param message 오류 메시지
     * @param errorCode 오류 코드
     * @return 오류 응답
     */
    public static ErrorResponse of(String message, String errorCode) {
        return new ErrorResponse(message, errorCode);
    }

    /**
     * 성공 여부를 반환합니다.
     *
     * @return 항상 false
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 오류 메시지를 반환합니다.
     *
     * @return 오류 메시지
     */
    public String getMessage() {
        return message;
    }

    /**
     * 오류 코드를 반환합니다.
     *
     * @return 오류 코드
     */
    public String getErrorCode() {
        return errorCode;
    }
}
