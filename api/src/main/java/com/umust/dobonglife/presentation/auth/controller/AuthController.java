package com.umust.dobonglife.presentation.auth.controller;

import com.umust.dobonglife.application.auth.service.AuthFacade;
import com.umust.dobonglife.application.auth.port.in.AppleLoginUseCase;
import com.umust.dobonglife.application.auth.port.in.GoogleLoginUseCase;
import com.umust.dobonglife.application.auth.port.in.KakaoLoginUseCase;
import com.umust.dobonglife.auth.security.extractor.TokenExtractor;
import com.umust.dobonglife.presentation.auth.dto.request.AppleLoginRequest;
import com.umust.dobonglife.presentation.auth.dto.request.GoogleLoginRequest;
import com.umust.dobonglife.presentation.auth.dto.request.KakaoLoginRequest;
import com.umust.dobonglife.presentation.auth.dto.response.TokenResponse;
import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

@Tag(name = "인증 인가 API", description = "인증 인가 관련 API")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(("/api/auth"))
@RestController
public class AuthController {

    private final KakaoLoginUseCase kakaoLoginUseCase;
    private final GoogleLoginUseCase googleLoginUseCase;
    private final AppleLoginUseCase appleLoginUseCase;
    private final AuthFacade authFacade;
    private final TokenExtractor tokenExtractor;

    @Operation(summary = "카카오 로그인", description = "카카오 로그인을 합니다.")
    @ApiResponse(responseCode = "200", description = "카카오 소셜 로그인에 성공하였습니다.")
    @PostMapping("/login/kakao")
    public BaseResponse<TokenResponse> loginKakao(@RequestBody @Valid KakaoLoginRequest request) {
        return BaseResponse.ok(TokenResponse.from(
                kakaoLoginUseCase.login(request.getAccessToken(), request.getFcmToken())
        ));
    }

    @Operation(summary = "구글 로그인", description = "구글 로그인을 합니다.")
    @ApiResponse(responseCode = "200", description = "구글 소셜 로그인에 성공하였습니다.")
    @PostMapping("/login/google")
    public BaseResponse<TokenResponse> loginGoogle(@RequestBody @Valid GoogleLoginRequest request) {
        return BaseResponse.ok(TokenResponse.from(
                googleLoginUseCase.login(request.getIdToken(), request.getFcmToken())
        ));
    }

    @Operation(summary = "애플 로그인", description = "애플 로그인을 합니다.")
    @ApiResponse(responseCode = "200", description = "애플 소셜 로그인에 성공하였습니다.")
    @PostMapping("/login/apple")
    public BaseResponse<TokenResponse> loginApple(@RequestBody @Valid AppleLoginRequest request) {
        return BaseResponse.ok(TokenResponse.from(
                appleLoginUseCase.login(request.getIdentityToken(), request.getFcmToken(), request.getProviderToken())
        ));
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 합니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃에 성공하였습니다.")
    @PostMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest request, @CurrentUserId Long userId) {
        String accessToken = tokenExtractor.extractAccessToken(request);
        String refreshToken = tokenExtractor.extractRefreshToken(request);
        authFacade.logout(accessToken, refreshToken, userId);
        return BaseResponse.ok(null);
    }

    @Operation(summary = "토큰 재발급", description = "토큰을 재발급합니다." +
            " 리프레쉬 토큰의 헤더는 Authorization-refresh 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "엑세스 토큰 재발급 성공하였습니다.",
            headers = {
            @Header(name = "Authorization", description = "재발급된 Access Token (Bearer {accessToken})",
                    schema = @Schema(type = "string")),
            @Header(name = "Authorization-refresh", description = "재발급된 Refresh Token (Bearer {refreshToken})",
                    schema = @Schema(type = "string"))
    })
    @SecurityRequirement(name = "RefreshAuth")
    @PostMapping("/reissue")
    public BaseResponse<TokenResponse> reissueTokens(HttpServletRequest request,
                                                     @CurrentUserId Long userId) {
        String refreshToken = tokenExtractor.extractRefreshToken(request);
        return BaseResponse.ok(TokenResponse.from(authFacade.reissueTokens(refreshToken)));
    }
}
