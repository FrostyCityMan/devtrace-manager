package com.devtrace.manager.common.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 인증 화면을 제공하는 컨트롤러입니다.
 *
 * <p>Spring Security의 Form Login 설정과 연결되는 로그인 페이지 템플릿을 반환합니다.</p>
 */
@Controller
public class AuthController {

    /**
     * 로그인 화면을 표시합니다.
     *
     * @return 로그인 템플릿
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
