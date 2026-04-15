package com.umust.dobonglife.domain.auth.presentation;

import com.umust.dobonglife.domain.auth.presentation.dto.request.RevokeSocialAccountRequest;
import com.umust.dobonglife.domain.auth.presentation.dto.request.TokenPairRequest;
import com.umust.dobonglife.domain.auth.presentation.dto.request.TokenRequest;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.port.auth.dto.AuthTokens;
import com.umust.dobonglife.global.port.auth.dto.AuthenticatedUser;
import com.umust.dobonglife.global.port.auth.dto.LoginSuccessCommand;
import com.umust.dobonglife.global.port.auth.dto.SocialAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/auth")
@RequiredArgsConstructor
public class InternalAuthController {

    private final com.umust.dobonglife.domain.auth.application.port.in.AuthTokenUseCase authTokenUseCase;
    private final com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;
    private final com.umust.dobonglife.domain.auth.application.port.in.LoginSuccessUseCase loginSuccessUseCase;
    private final com.umust.dobonglife.domain.auth.application.port.in.RevokeSocialAccountUseCase revokeSocialAccountUseCase;
    private final com.umust.dobonglife.global.port.auth.out.KakaoOAuthPort kakaoOAuthPort;
    private final com.umust.dobonglife.global.port.auth.out.GoogleOAuthPort googleOAuthPort;
    private final com.umust.dobonglife.global.port.auth.out.AppleOAuthPort appleOAuthPort;

    @PostMapping("/authenticate")
    public AuthenticatedUser authenticate(@RequestBody TokenRequest request) {
        var authenticatedUser = authenticateAccessTokenUseCase.authenticate(request.token());
        return new AuthenticatedUser(
                authenticatedUser.userId(),
                authenticatedUser.userName(),
                authenticatedUser.role(),
                authenticatedUser.provider()
        );
    }

    @PostMapping("/logout")
    public void logout(@RequestBody TokenPairRequest request) {
        authTokenUseCase.logout(request.accessToken(), request.refreshToken());
    }

    @PostMapping("/reissue")
    public AuthTokens reissue(@RequestBody TokenRequest request) {
        var authTokens = authTokenUseCase.reissueTokens(request.token());
        return new AuthTokens(authTokens.accessToken(), authTokens.refreshToken(), authTokens.role());
    }

    @PostMapping("/invalidate-access")
    public void invalidateAccess(@RequestBody TokenRequest request) {
        authTokenUseCase.invalidateAccessToken(request.token());
    }

    @PostMapping("/delete-refresh")
    public void deleteRefresh(@RequestBody TokenRequest request) {
        authTokenUseCase.deleteRefreshToken(request.token());
    }

    @PostMapping("/login-success")
    public AuthTokens handleLoginSuccess(@RequestBody LoginSuccessCommand command) {
        var authTokens = loginSuccessUseCase.handleLoginSuccess(
                new com.umust.dobonglife.domain.auth.application.dto.LoginSuccessCommand(
                        command.userId(),
                        command.provider(),
                        command.role(),
                        command.userName()
                )
        );
        return new AuthTokens(authTokens.accessToken(), authTokens.refreshToken(), authTokens.role());
    }

    @PostMapping("/revoke-social")
    public void revokeSocial(@RequestBody RevokeSocialAccountRequest request) {
        revokeSocialAccountUseCase.revoke(
                Provider.valueOf(request.provider()),
                request.providerIdOrToken()
        );
    }

    @PostMapping("/oauth/kakao/verify")
    public SocialAuthUserInfo verifyKakao(@RequestBody TokenRequest request) {
        var userInfo = kakaoOAuthPort.verify(request.token());
        return new SocialAuthUserInfo(userInfo.providerId(), userInfo.email(), userInfo.name());
    }

    @PostMapping("/oauth/kakao/unlink")
    public void unlinkKakao(@RequestBody TokenRequest request) {
        kakaoOAuthPort.unlinkUser(request.token());
    }

    @PostMapping("/oauth/google/verify")
    public SocialAuthUserInfo verifyGoogle(@RequestBody TokenRequest request) {
        var userInfo = googleOAuthPort.verify(request.token());
        return new SocialAuthUserInfo(userInfo.providerId(), userInfo.email(), userInfo.name());
    }

    @PostMapping("/oauth/google/revoke")
    public void revokeGoogle(@RequestBody TokenRequest request) {
        googleOAuthPort.revokeToken(request.token());
    }

    @PostMapping("/oauth/apple/verify")
    public SocialAuthUserInfo verifyApple(@RequestBody TokenRequest request) {
        var userInfo = appleOAuthPort.verify(request.token());
        return new SocialAuthUserInfo(userInfo.providerId(), userInfo.email(), userInfo.name());
    }

    @PostMapping("/oauth/apple/exchange")
    public String exchangeAppleAuthorizationCode(@RequestBody TokenRequest request) {
        return appleOAuthPort.exchangeAuthorizationCode(request.token());
    }

    @PostMapping("/oauth/apple/revoke")
    public void revokeApple(@RequestBody TokenRequest request) {
        appleOAuthPort.revokeToken(request.token());
    }
}
