package com.ssafy.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 인증된 사용자만 접근 가능한 컨트롤러
 */
@Controller
@RequestMapping("/secured")
public class SecuredController {
    private static final Logger log = LoggerFactory.getLogger(SecuredController.class);

    @GetMapping("/admin")
    // @AuthenticationPrincipal: 인증 후 세션의 UserDetails를 주입받는다
    public String admin(@AuthenticationPrincipal UserDetails details, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("admin login.................... : " + details);
        log.error("admin login....................");
        if (details != null) {
            log.debug("로그인 정보: {}", details);
        }
        return "secured/admin";
    }

    @GetMapping("/user")
    public String user() {
        return "secured/user";
    }
}
