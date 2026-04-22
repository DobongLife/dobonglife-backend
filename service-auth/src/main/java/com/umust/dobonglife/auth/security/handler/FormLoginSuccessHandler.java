package com.umust.dobonglife.auth.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.auth.client.UserServiceClient;
import com.umust.dobonglife.common.security.principal.UserPrincipal;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.application.port.in.LoginSuccessUseCase;
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
public class FormLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginSuccessUseCase loginSuccessUseCase;
    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        Object details = authentication.getDetails();
        if (details instanceof String fcmToken && !fcmToken.isBlank()) {
            userServiceClient.updateFcmToken(userId, fcmToken);
        }

        AuthTokens tokens = loginSuccessUseCase.issueLoginToken(
                userId, principal.getProvider(), principal.getRole(), principal.getUsername()
        );

        TokenResponse body = new TokenResponse(tokens.accessToken(), tokens.refreshToken(), tokens.role());
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(BaseResponse.ok(body)));
    }

    public record TokenResponse(String accessToken, String refreshToken, String role) {}
}
