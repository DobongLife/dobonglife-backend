package com.umust.dobonglife.domain.auth.handler;

import com.umust.dobonglife.domain.auth.service.JwtService;
import com.umust.dobonglife.domain.auth.utils.AuthenticationUtil;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthenticationUtil authenticationUtil;
    private final JwtUtil jwtUtil;
    private final JwtService jwtService;

    // 원래는 프론트에게 Rest API를 redirect 해야됨 추후 수정 예정
    private static final String LOGIN_SUCCESS_URI = "http://localhost:8080/api/login/successPage";

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        String providerId = authenticationUtil.getProvider();
        String role = authenticationUtil.getRole();
        Long userId = authenticationUtil.getUserId();
        String userName = authenticationUtil.getUserName();
        log.info("[CustomAuthenticationSuccessHandler] providerId={}, role={}, userId={}", providerId, role, userId);

        // 토큰 생성
        String accessToken = jwtUtil.createAccessToken(userId, providerId, role, userName);
        String refreshToken = jwtUtil.createRefreshToken(userId, providerId, role);

        // refresh token 저장
        jwtService.storeRefreshToken(refreshToken, userId);
        log.info("[CustomAuthenticationSuccessHandler], refreshToken={}", refreshToken);

        // 리다이렉션
        response.sendRedirect(LOGIN_SUCCESS_URI);

    }
}
