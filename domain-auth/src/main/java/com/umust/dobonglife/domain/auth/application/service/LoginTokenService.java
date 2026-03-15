package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.in.IssueLoginTokenUseCase;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginTokenService implements IssueLoginTokenUseCase {

    private final TokenIssuanceHelper tokenIssuanceHelper;

    @Override
    public AuthTokens issueLoginToken(Long userId, Provider provider, Role role, String name) {
        String roleString = Role.PREFIX + role.name();
        return tokenIssuanceHelper.issueAndStore(userId, provider.getValue(), roleString, name);
    }
}
