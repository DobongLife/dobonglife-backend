package com.umust.dobonglife.auth.security.handler;

import com.umust.dobonglife.common.security.util.AuthErrorResponseUtil;
import com.umust.dobonglife.domain.auth.exception.AuthErrorCode;
import com.umust.dobonglife.global.common.error.CommonErrorCode;
import com.umust.dobonglife.global.common.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class FormLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException ex) throws IOException {
        log.warn("=== AuthenticationFailureHandler 진입: type={}, msg={} ===",
                ex.getClass().getSimpleName(), ex.getMessage());
        AuthErrorResponseUtil.setErrorResponse(response, mapToErrorCode(ex));
    }

    private ErrorCode mapToErrorCode(AuthenticationException ex) {
        if (ex instanceof UsernameNotFoundException) {
            return AuthErrorCode.SECURITY_UNAUTHORIZED;
        }
        if (ex instanceof BadCredentialsException) {
            return AuthErrorCode.INVALID_EMAIL_OR_PASSWORD;
        }
        if (ex instanceof AuthenticationServiceException) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "";
            if (msg.contains("HTTP 메서드")) {
                return CommonErrorCode.METHOD_NOT_ALLOWED;
            }
            if (msg.contains("Content-Type") || msg.contains("파싱")) {
                return CommonErrorCode.ILLEGAL_ARGUMENT;
            }
            return CommonErrorCode.ILLEGAL_ARGUMENT;
        }
        return CommonErrorCode.ILLEGAL_ARGUMENT;
    }
}
