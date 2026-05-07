package com.devtrace.manager.common.exception;

import com.devtrace.manager.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * REST API 예외 응답을 공통 형식으로 변환하는 전역 예외 처리기입니다.
 *
 * <p>업무 예외와 검증 예외는 400 응답으로 반환하고, 예상하지 못한 예외는
 * 내부 오류 응답으로 표준화합니다.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 업무 예외를 오류 응답으로 변환합니다.
     *
     * @param ex 업무 예외
     * @return 400 오류 응답
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(ex.getMessage(), ex.getErrorCode()));
    }

    /**
     * Bean Validation 예외를 첫 번째 필드 오류 메시지 중심으로 변환합니다.
     *
     * @param ex 검증 예외
     * @return 400 오류 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("입력값이 올바르지 않습니다.");
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(message, "VALIDATION_ERROR"));
    }

    /**
     * 처리되지 않은 예외를 내부 서버 오류 응답으로 변환합니다.
     *
     * @param ex 처리되지 않은 예외
     * @return 500 오류 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("서버 처리 중 오류가 발생했습니다.", "INTERNAL_SERVER_ERROR"));
    }
}
