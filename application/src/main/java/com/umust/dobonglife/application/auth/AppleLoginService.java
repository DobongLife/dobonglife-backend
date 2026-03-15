package com.umust.dobonglife.application.auth;

import com.umust.dobonglife.domain.auth.application.service.AppleAuthService;
import com.umust.dobonglife.domain.auth.application.service.JwtService;
import com.umust.dobonglife.domain.auth.domain.SocialUserInfo;
import com.umust.dobonglife.domain.auth.dto.request.AppleLoginRequest;
import com.umust.dobonglife.domain.auth.dto.response.TokenResponse;
import com.umust.dobonglife.domain.auth.infrastructure.jwt.JwtTokenProvider;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.OAuthUserUseCase;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleLoginService {

    private final AppleAuthService appleAuthService;
    private final OAuthUserUseCase oAuthUserUseCase;
    private final ManageUserUseCase manageUserUseCase;
    private final JwtTokenProvider jwtUtil;
    private final JwtService jwtService;

    @Transactional
    public TokenResponse login(AppleLoginRequest request) {
        SocialUserInfo socialUser = appleAuthService.verify(request.getIdentityToken());

        User user = oAuthUserUseCase.findOrCreateOAuthUser(
                Provider.APPLE, socialUser.providerId(), socialUser.email(), socialUser.email()
        );

        if (request.getFcmToken() != null && !request.getFcmToken().isBlank()) {
            manageUserUseCase.updateFcmToken(user.getId(), request.getFcmToken());
        }

        if (request.getProviderToken() != null && !request.getProviderToken().isBlank()) {
            String providerRefreshToken = appleAuthService.exchangeAuthorizationCode(request.getProviderToken());
            manageUserUseCase.updateProviderToken(user.getId(), providerRefreshToken);
        } else {
            log.warn("[Apple Login] providerToken(authorizationCode)이 요청에 없음: userId={}", user.getId());
        }

        String access = jwtUtil.createAccessToken(user.getId(), Provider.APPLE.getValue(), Role.PREFIX + user.getRole().name(), user.getName());
        String refresh = jwtUtil.createRefreshToken(user.getId(), Provider.APPLE.getValue(), Role.PREFIX + user.getRole().name(), user.getName());

        jwtService.storeRefreshToken(refresh, user.getId());

        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .role(Role.PREFIX + user.getRole().name())
                .build();
    }
}
