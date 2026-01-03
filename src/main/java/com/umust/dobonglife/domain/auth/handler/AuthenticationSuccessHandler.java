package com.umust.dobonglife.domain.auth.handler;

import com.umust.dobonglife.domain.auth.service.JwtService;
import com.umust.dobonglife.domain.auth.utils.AuthenticationUtil;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${jwt.access.header}")
    private String ACCESS_HEADER;

    @Value("${jwt.refresh.header}")
    private String REFRESH_HEADER;

    private final AuthenticationUtil authenticationUtil;
    private final JwtUtil jwtUtil;
    private final JwtService jwtService;
    private final String REDIRECT_URI = "dobonglife://auth-callback";

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        String provider = authenticationUtil.getProvider();
        String role = authenticationUtil.getRole();
        Long userId = authenticationUtil.getUserId();
        String userName = authenticationUtil.getUserName();
        log.info("[AuthenticationSuccessHandler] provider={}, role={}, userId={}", provider, role, userId);

        String accessToken = jwtUtil.createAccessToken(userId, provider, role, userName);
        String refreshToken = jwtUtil.createRefreshToken(userId, provider, role);

        jwtService.storeRefreshToken(refreshToken, userId);
        log.info("[AuthenticationSuccessHandler], refreshToken={}", refreshToken);

        response.setHeader(ACCESS_HEADER, accessToken);
        response.setHeader(REFRESH_HEADER, refreshToken);
        response.sendRedirect(REDIRECT_URI);
    }
}
