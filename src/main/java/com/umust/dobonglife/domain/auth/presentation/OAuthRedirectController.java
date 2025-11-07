package com.umust.dobonglife.domain.auth.presentation;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@RequestMapping("/api/login")
@Controller
public class OAuthRedirectController {

    @GetMapping("/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/kakao");
    }

    @GetMapping("/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }
}