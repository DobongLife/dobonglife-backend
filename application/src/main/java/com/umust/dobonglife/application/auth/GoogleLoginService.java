package com.umust.dobonglife.application.auth;

import com.umust.dobonglife.domain.auth.application.service.GoogleAuthService;
import com.umust.dobonglife.domain.auth.application.service.JwtService;
import com.umust.dobonglife.domain.auth.domain.SocialUserInfo;
import com.umust.dobonglife.domain.auth.dto.request.GoogleLoginRequest;
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
public class GoogleLoginService {

    private final GoogleAuthService googleAuthService;
    private final OAuthUserUseCase oAuthUserUseCase;
    private final ManageUserUseCase manageUserUseCase;
    private final JwtTokenProvider jwtUtil;
    private final JwtService jwtService;

    @Transactional
    public TokenResponse login(GoogleLoginRequest request) {
        SocialUserInfo socialUser = googleAuthService.verify(request.getIdToken());

        User user = oAuthUserUseCase.findOrCreateOAuthUser(
                Provider.GOOGLE, socialUser.providerId(), socialUser.email(), socialUser.name()
        );

        if (request.getFcmToken() != null && !request.getFcmToken().isBlank()) {
            manageUserUseCase.updateFcmToken(user.getId(), request.getFcmToken());
        }

        String access = jwtUtil.createAccessToken(user.getId(), Provider.GOOGLE.getValue(), Role.PREFIX + user.getRole().name(), user.getName());
        String refresh = jwtUtil.createRefreshToken(user.getId(), Provider.GOOGLE.getValue(), Role.PREFIX + user.getRole().name(), user.getName());

        jwtService.storeRefreshToken(refresh, user.getId());

        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .role(Role.PREFIX + user.getRole().name())
                .build();
    }
}
