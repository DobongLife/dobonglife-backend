package com.umust.dobonglife.domain.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.domain.auth.controller.dto.response.TokenResponse;
import com.umust.dobonglife.domain.auth.utils.AuthenticationUtil;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.global.common.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class FormAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final AuthenticationUtil authenticationUtil;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        String provider = authenticationUtil.getProvider();
        String role = authenticationUtil.getRole();
        Long userId = authenticationUtil.getUserId();
        String userName = authenticationUtil.getUserName();
        log.info("[FormAuthenticationSuccessHandler] provider={}, role={}, userId={}", provider, role, userId);

        String accessToken = jwtUtil.createAccessToken(userId, provider, role, userName);
        String refreshToken = jwtUtil.createRefreshToken(userId, provider, role);

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        writeResponse(response, BaseResponse.ok(tokenResponse));
        log.info("[FormSuccess] userName={}, role={}", userName, role);
    }

    private void writeResponse(HttpServletResponse response, Object value) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String body = objectMapper.writeValueAsString(value);
        response.getWriter().write(body);
    }
}


