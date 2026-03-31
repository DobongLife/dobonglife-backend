package com.umust.dobonglife.auth.controller;

import com.umust.dobonglife.auth.service.AppleLoginService;
import com.umust.dobonglife.auth.service.AuthFacade;
import com.umust.dobonglife.auth.service.GoogleLoginService;
import com.umust.dobonglife.auth.service.KakaoLoginService;
import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final KakaoLoginService kakaoLoginService;
    private final GoogleLoginService googleLoginService;
    private final AppleLoginService appleLoginService;
    private final AuthFacade authFacade;
    private final TokenExtractor tokenExtractor;

    @PostMapping("/login/kakao")
    public BaseResponse<TokenResponse> loginKakao(@RequestBody @Valid KakaoLoginRequest request) {
        return BaseResponse.ok(TokenResponse.from(kakaoLoginService.login(request.accessToken(), request.fcmToken())));
    }

    @PostMapping("/login/google")
    public BaseResponse<TokenResponse> loginGoogle(@RequestBody @Valid GoogleLoginRequest request) {
        return BaseResponse.ok(TokenResponse.from(googleLoginService.login(request.idToken(), request.fcmToken())));
    }

    @PostMapping("/login/apple")
    public BaseResponse<TokenResponse> loginApple(@RequestBody @Valid AppleLoginRequest request) {
        return BaseResponse.ok(TokenResponse.from(appleLoginService.login(request.identityToken(), request.fcmToken(), request.providerToken())));
    }

    @PostMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest request, @CurrentUserId Long userId) {
        String accessToken = tokenExtractor.extractAccessToken(request);
        String refreshToken = tokenExtractor.extractRefreshToken(request);
        authFacade.logout(accessToken, refreshToken, userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/reissue")
    public BaseResponse<TokenResponse> reissueTokens(HttpServletRequest request) {
        String refreshToken = tokenExtractor.extractRefreshToken(request);
        return BaseResponse.ok(TokenResponse.from(authFacade.reissueTokens(refreshToken)));
    }

    // Request/Response DTOs
    public record KakaoLoginRequest(@NotBlank String accessToken, String fcmToken) {}
    public record GoogleLoginRequest(@NotBlank String idToken, String fcmToken) {}
    public record AppleLoginRequest(@NotBlank String identityToken, String fcmToken, String providerToken) {}
    public record TokenResponse(String accessToken, String refreshToken) {
        public static TokenResponse from(AuthTokens tokens) {
            return new TokenResponse(tokens.accessToken(), tokens.refreshToken());
        }
    }
}
