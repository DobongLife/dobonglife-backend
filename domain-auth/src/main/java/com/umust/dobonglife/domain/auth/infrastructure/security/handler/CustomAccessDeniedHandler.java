package com.umust.dobonglife.domain.auth.infrastructure.security.handler;

import com.umust.dobonglife.global.error.DomainErrorCode;
import com.umust.dobonglife.global.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.umust.dobonglife.domain.auth.infrastructure.security.AuthErrorResponseUtil.setErrorResponse;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException{
        log.info("=== AccessDeniedHandler 진입 ===");

        ErrorCode code = DomainErrorCode.SECURITY_ACCESS_DENIED;
        setErrorResponse(response, code);
    }
}
