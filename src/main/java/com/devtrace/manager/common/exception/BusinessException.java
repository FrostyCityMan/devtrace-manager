package com.devtrace.manager.common.exception;

/**
 * 업무 규칙 위반을 표현하는 런타임 예외입니다.
 *
 * <p>사용자에게 전달할 메시지와 클라이언트가 분기할 수 있는 오류 코드를 함께 보관합니다.</p>
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;

    /**
     * 업무 예외를 생성합니다.
     *
     * @param message 사용자에게 전달할 오류 메시지
     * @param errorCode 업무 오류 코드
     */
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 업무 오류 코드를 반환합니다.
     *
     * @return 오류 코드
     */
    public String getErrorCode() {
        return errorCode;
    }
}
