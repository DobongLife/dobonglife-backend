package com.umust.dobonglife.global.auth.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.domain.auth.application.port.in.IssueTokenUseCase;
import com.umust.dobonglife.domain.auth.application.dto.LoginSuccessCommand;
import com.umust.dobonglife.global.auth.security.principal.UserPrincipal;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.presentation.auth.dto.response.TokenResponse;
import com.umust.dobonglife.global.common.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final IssueTokenUseCase issueTokenUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        String fcmToken = null;
        if (authentication.getDetails() instanceof String token && !token.isBlank()) {
            fcmToken = token;
        }

        String role = principal.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);

        LoginSuccessCommand command = new LoginSuccessCommand(
                principal.getUserId(),
                principal.getProvider().getValue(),
                role,
                principal.getUsername(),
                fcmToken
        );

        AuthTokens authTokens = issueTokenUseCase.handleLoginSuccess(command);
        writeResponse(response, BaseResponse.ok(TokenResponse.from(authTokens)));
    }

    private void writeResponse(HttpServletResponse response, Object value) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(value));
    }
}
