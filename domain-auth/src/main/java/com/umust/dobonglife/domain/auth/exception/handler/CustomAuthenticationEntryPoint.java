package com.umust.dobonglife.domain.auth.exception.handler;

import com.umust.dobonglife.domain.auth.exception.CustomAuthenticationException;
import com.umust.dobonglife.global.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.umust.dobonglife.domain.auth.utils.AuthErrorResponseUtil.setErrorResponse;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException{
        log.info("=== AuthenticationEntryPoint 진입 ===");

        ErrorCode code = ErrorCode.SECURITY_UNAUTHORIZED;

        if (authException instanceof CustomAuthenticationException e) {
            code = e.getErrorCode();
        }
        setErrorResponse(response, code);
    }
}
