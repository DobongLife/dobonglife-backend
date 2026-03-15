package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.in.IssueLoginTokenUseCase;
import com.umust.dobonglife.domain.auth.domain.AuthTokens;
import com.umust.dobonglife.domain.auth.infrastructure.jwt.JwtTokenProvider;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginTokenService implements IssueLoginTokenUseCase {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;

    @Override
    public AuthTokens issueLoginToken(Long userId, Provider provider, Role role, String name) {
        String roleString = Role.PREFIX + role.name();

        String access = jwtTokenProvider.createAccessToken(userId, provider.getValue(), roleString, name);
        String refresh = jwtTokenProvider.createRefreshToken(userId, provider.getValue(), roleString, name);

        jwtService.storeRefreshToken(refresh, userId);

        return new AuthTokens(access, refresh, roleString);
    }
}
