package com.devtrace.manager.common.response;

/**
 * REST API 성공 응답의 공통 래퍼입니다.
 *
 * <p>모든 성공 응답은 성공 여부, 메시지, 실제 데이터를 동일한 JSON 구조로 제공합니다.</p>
 *
 * @param <T> 응답 데이터 타입
 */
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;

    /**
     * API 응답을 생성합니다.
     *
     * @param success 성공 여부
     * @param message 응답 메시지
     * @param data 응답 데이터
     */
    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * 메시지를 지정한 성공 응답을 생성합니다.
     *
     * @param message 성공 메시지
     * @param data 응답 데이터
     * @param <T> 응답 데이터 타입
     * @return 성공 API 응답
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * 기본 메시지를 사용하는 성공 응답을 생성합니다.
     *
     * @param data 응답 데이터
     * @param <T> 응답 데이터 타입
     * @return 성공 API 응답
     */
    public static <T> ApiResponse<T> success(T data) {
        return success("요청이 정상 처리되었습니다.", data);
    }

    /**
     * 성공 여부를 반환합니다.
     *
     * @return 성공이면 true
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 응답 메시지를 반환합니다.
     *
     * @return 응답 메시지
     */
    public String getMessage() {
        return message;
    }

    /**
     * 응답 데이터를 반환합니다.
     *
     * @return 응답 데이터
     */
    public T getData() {
        return data;
    }
}
