package com.devtrace.manager.common.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 모든 Thymeleaf 화면에 현재 요청 경로를 제공하는 모델 어드바이스입니다.
 *
 * <p>공통 레이아웃의 사이드바 활성 상태 계산에 사용됩니다.</p>
 */
@ControllerAdvice
public class NavigationModelAdvice {

    /**
     * 컨텍스트 경로를 제거한 현재 요청 경로를 계산합니다.
     *
     * @param request HTTP 요청
     * @return 현재 화면 경로
     */
    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank() && requestUri.startsWith(contextPath)) {
            return requestUri.substring(contextPath.length());
        }
        return requestUri;
    }
}
