package com.umust.dobonglife.domain.auth.infrastructure.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.domain.auth.dto.response.TokenResponse;
import com.umust.dobonglife.domain.auth.application.service.JwtService;
import com.umust.dobonglife.domain.auth.infrastructure.security.AuthenticationUtil;
import com.umust.dobonglife.domain.auth.infrastructure.jwt.JwtTokenProvider;
import com.umust.dobonglife.domain.auth.application.port.out.AuthUserPort;
import com.umust.dobonglife.global.common.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationUtil authenticationUtil;
    private final JwtTokenProvider jwtUtil;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final AuthUserPort authUserPort;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        String provider = authenticationUtil.getProvider();
        String role = authenticationUtil.getRole();
        Long userId = authenticationUtil.getUserId();
        String userName = authenticationUtil.getUserName();
        log.info("[CustomAuthenticationSuccessHandler] provider={}, role={}, userId={}", provider, role, userId);

        // 폼 로그인만 적용 됨
        Object details = authentication.getDetails();
        if (details instanceof String fcmToken && !fcmToken.isBlank()) {
            authUserPort.updateFcmToken(userId, fcmToken);
        }

        String accessToken = jwtUtil.createAccessToken(userId, provider, role, userName);
        String refreshToken = jwtUtil.createRefreshToken(userId, provider, role, userName);

        jwtService.storeRefreshToken(refreshToken, userId);
        log.info("[CustomAuthenticationSuccessHandler], refreshToken={}", refreshToken);

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(role)
                .build();
        writeResponse(response, BaseResponse.ok(tokenResponse));
    }

    private void writeResponse(HttpServletResponse response, Object value) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String body = objectMapper.writeValueAsString(value);
        response.getWriter().write(body);
    }
}
