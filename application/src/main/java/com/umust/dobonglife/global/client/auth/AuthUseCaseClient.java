package com.umust.dobonglife.global.client.auth;

import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import com.umust.dobonglife.global.port.auth.dto.AuthTokens;
import com.umust.dobonglife.global.port.auth.dto.AuthenticatedUser;
import com.umust.dobonglife.global.port.auth.dto.LoginSuccessCommand;
import com.umust.dobonglife.global.port.auth.in.AuthTokenUseCase;
import com.umust.dobonglife.global.port.auth.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.global.port.auth.in.LoginSuccessUseCase;
import com.umust.dobonglife.global.port.auth.in.RevokeSocialAccountUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class AuthUseCaseClient implements
        AuthTokenUseCase,
        AuthenticateAccessTokenUseCase,
        LoginSuccessUseCase,
        RevokeSocialAccountUseCase {

    private final RestClient restClient;

    public AuthUseCaseClient(@Value("${service.auth.url}") String baseUrl) {
        this.restClient = InternalRestClientFactory.create(baseUrl, "auth-service");
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        restClient.post()
                .uri("/internal/auth/logout")
                .body(Map.of("accessToken", accessToken, "refreshToken", refreshToken))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void invalidateAccessToken(String accessToken) {
        restClient.post()
                .uri("/internal/auth/invalidate-access")
                .body(Map.of("token", accessToken))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        restClient.post()
                .uri("/internal/auth/delete-refresh")
                .body(Map.of("token", refreshToken))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public AuthTokens reissueTokens(String refreshToken) {
        return restClient.post()
                .uri("/internal/auth/reissue")
                .body(Map.of("token", refreshToken))
                .retrieve()
                .body(AuthTokens.class);
    }

    @Override
    public AuthenticatedUser authenticate(String accessToken) {
        return restClient.post()
                .uri("/internal/auth/authenticate")
                .body(Map.of("token", accessToken))
                .retrieve()
                .body(AuthenticatedUser.class);
    }

    @Override
    public AuthTokens issueLoginToken(Long userId, Provider provider, Role role, String name) {
        return handleLoginSuccess(new LoginSuccessCommand(
                userId,
                provider.getValue(),
                role.toAuthority().getAuthority(),
                name
        ));
    }

    @Override
    public AuthTokens handleLoginSuccess(LoginSuccessCommand command) {
        return restClient.post()
                .uri("/internal/auth/login-success")
                .body(command)
                .retrieve()
                .body(AuthTokens.class);
    }

    @Override
    public void revoke(Provider provider, String providerIdOrToken) {
        restClient.post()
                .uri("/internal/auth/revoke-social")
                .body(Map.of(
                        "provider", provider.name(),
                        "providerIdOrToken", providerIdOrToken == null ? "" : providerIdOrToken
                ))
                .retrieve()
                .toBodilessEntity();
    }
}
