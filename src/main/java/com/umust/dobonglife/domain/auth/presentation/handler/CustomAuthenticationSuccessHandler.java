package com.umust.dobonglife.domain.auth.presentation.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jooeon.mybeauty.domain.auth.application.JwtService;
import me.jooeon.mybeauty.domain.auth.utils.JwtUtil;
import me.jooeon.mybeauty.domain.auth.utils.AuthenticationUtil;
import me.jooeon.mybeauty.domain.auth.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
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
    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;
    //private final RedisService redisService;

    // 원래는 프론트에게 Rest API를 redirect 해야됨 추후 수정 예정
    private static final String LOGIN_SUCCESS_URI = "http://localhost:8080/api/login/successPage";
    private final JwtService jwtService;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        String providerId = authenticationUtil.getProviderId();
        String role = authenticationUtil.getRole();
        Long memberId = authenticationUtil.getMemberId();
        String userName = authenticationUtil.getUsername();
        String email = authenticationUtil.getEmail();
        log.info("[CustomAuthenticationSuccessHandler] providerId={}, role={}, memberId={}, email={}", providerId, role, memberId, email);

        // 토큰 생성
        String accessToken = jwtUtil.createAccessToken(memberId, providerId, role, userName);
        String refreshToken = jwtUtil.createRefreshToken(memberId, providerId, role);

        // refresh token 저장
        jwtService.storeRefreshToken(refreshToken);
        log.info("[CustomAuthenticationSuccessHandler], refreshToken={}", refreshToken);

        // 응답 설정
        response.addCookie(cookieUtil.createCookie("ACCESS_TOKEN", accessToken));
        response.addCookie(cookieUtil.createCookie("REFRESH_TOKEN", refreshToken));

        // 리다이렉션
        response.sendRedirect(LOGIN_SUCCESS_URI);

    }
}
