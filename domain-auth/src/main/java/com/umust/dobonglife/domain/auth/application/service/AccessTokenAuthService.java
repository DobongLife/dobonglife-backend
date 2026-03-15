package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.auth.application.dto.AuthenticatedUser;
import com.umust.dobonglife.domain.auth.exception.CustomJwtException;
import com.umust.dobonglife.domain.auth.infrastructure.JwtTokenProvider;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import com.umust.dobonglife.domain.auth.exception.AuthErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccessTokenAuthService implements AuthenticateAccessTokenUseCase {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;

    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return jwtTokenProvider.extractAccessToken(request);
    }

    @Override
    public AuthenticatedUser authenticate(String accessToken) {
        jwtTokenProvider.validateToken(accessToken);

        if (!"access".equals(jwtTokenProvider.getTokenType(accessToken))) {
            throw new CustomJwtException(AuthErrorCode.INVALID_TOKEN_TYPE);
        }

        jwtService.checkLogout(accessToken);

        return new AuthenticatedUser(
                jwtTokenProvider.getUserId(accessToken),
                jwtTokenProvider.getName(accessToken),
                Role.fromRole(jwtTokenProvider.getRole(accessToken)),
                Provider.fromProvider(jwtTokenProvider.getProvider(accessToken))
        );
    }
}
