package com.umust.dobonglife.auth.internal;

import com.umust.dobonglife.auth.client.UserServiceClient;
import com.umust.dobonglife.auth.client.UserServiceClient.UserProviderResponse;
import com.umust.dobonglife.domain.auth.application.port.in.AuthTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.RevokeSocialAccountUseCase;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/auth")
public class AuthInternalController {

    private final RevokeSocialAccountUseCase revokeSocialAccountUseCase;
    private final AuthTokenUseCase authTokenUseCase;
    private final UserServiceClient userServiceClient;

    @PostMapping("/{userId}/revoke-social")
    public BaseResponse<Void> revokeSocialAccount(@PathVariable Long userId) {
        UserProviderResponse providerInfo = userServiceClient.getUserProvider(userId);
        revokeSocialAccountUseCase.revoke(
                Provider.valueOf(providerInfo.provider()), providerInfo.providerId()
        );
        return BaseResponse.ok(null);
    }

    @PostMapping("/invalidate-token")
    public BaseResponse<Void> invalidateToken(
            @RequestParam String accessToken,
            @RequestParam(required = false) String refreshToken) {
        authTokenUseCase.invalidateAccessToken(accessToken);
        if (refreshToken != null) {
            authTokenUseCase.deleteRefreshToken(refreshToken);
        }
        return BaseResponse.ok(null);
    }
}
