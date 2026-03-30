package com.umust.dobonglife.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.global.port.auth.in.LoginSuccessUseCase;
import com.umust.dobonglife.global.port.auth.dto.LoginSuccessCommand;
import com.umust.dobonglife.security.principal.UserPrincipal;
import com.umust.dobonglife.global.port.auth.dto.AuthTokens;
import com.umust.dobonglife.global.port.user.in.ManageUserUseCase;
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

    private final LoginSuccessUseCase loginSuccessUseCase;
    private final ManageUserUseCase manageUserUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        if (authentication.getDetails() instanceof String fcmToken && !fcmToken.isBlank()) {
            manageUserUseCase.updateFcmToken(principal.getUserId(), fcmToken);
        }

        String role = principal.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);

        LoginSuccessCommand command = new LoginSuccessCommand(
                principal.getUserId(),
                principal.getProvider().getValue(),
                role,
                principal.getUsername()
        );

        AuthTokens authTokens = loginSuccessUseCase.handleLoginSuccess(command);
        writeResponse(response, BaseResponse.ok(TokenResponse.from(authTokens)));
    }

    private void writeResponse(HttpServletResponse response, Object value) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(value));
    }
}
