package com.umust.dobonglife.global.auth.security.handler;

import com.umust.dobonglife.global.error.CommonErrorCode;
import com.umust.dobonglife.global.error.DomainErrorCode;
import com.umust.dobonglife.global.error.ErrorCode;
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

import static com.umust.dobonglife.global.auth.security.AuthErrorResponseUtil.setErrorResponse;

@Slf4j
@Component
public class CustomJsonAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException authenticationException) throws IOException {
        log.warn("=== AuthenticationFailureHandler 진입: type={}, msg={} ===",
                authenticationException.getClass().getSimpleName(), authenticationException.getMessage());

        ErrorCode code = mapToErrorCode(authenticationException);
        setErrorResponse(response, code);
    }

    private ErrorCode mapToErrorCode(AuthenticationException ex) {

        if (ex instanceof UsernameNotFoundException) {
            return DomainErrorCode.SECURITY_UNAUTHORIZED;
        }

        if (ex instanceof BadCredentialsException) {
            return DomainErrorCode.INVALID_EMAIL_OR_PASSWORD;
        }

        if (ex instanceof AuthenticationServiceException) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "";

            if (msg.contains("HTTP 메서드")) {
                return CommonErrorCode.METHOD_NOT_ALLOWED;
            }
            if (msg.contains("Content-Type")) {
                return CommonErrorCode.ILLEGAL_ARGUMENT;
            }
            if (msg.contains("파싱")) {
                return CommonErrorCode.ILLEGAL_ARGUMENT;
            }
            return CommonErrorCode.ILLEGAL_ARGUMENT;
        }

        return CommonErrorCode.ILLEGAL_ARGUMENT;
    }
}
