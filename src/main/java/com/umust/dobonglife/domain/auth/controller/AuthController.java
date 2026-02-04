package com.umust.dobonglife.domain.auth.controller;

import com.umust.dobonglife.domain.auth.controller.dto.request.GoogleLoginRequest;
import com.umust.dobonglife.domain.auth.controller.dto.request.KakaoLoginRequest;
import com.umust.dobonglife.domain.auth.controller.dto.request.RefreshTokenRequest;
import com.umust.dobonglife.domain.auth.controller.dto.response.TokenResponse;
import com.umust.dobonglife.domain.auth.service.JwtService;
import com.umust.dobonglife.domain.auth.service.KakaoAuthService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.umust.dobonglife.domain.auth.service.GoogleAuthService;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "인증 인가 API", description = "인증 인가 관련 API")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(("/api/auth"))
@RestController
public class AuthController {

    private final JwtService jwtService;
    private final GoogleAuthService googleAuthService;
    private final KakaoAuthService kakaoAuthService;

    @Operation(summary = "카카오 로그인", description = "카카오 로그인을 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "카카오 소셜 로그인에 성공하였습니다."
    )
    @PostMapping("/login/kakao")
    public BaseResponse<TokenResponse> loginKakao(@RequestBody @Valid KakaoLoginRequest request) {
        return BaseResponse.ok(kakaoAuthService.login(request));
    }

    @Operation(summary = "구글 로그인", description = "구글 로그인을 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "구글 소셜 로그인에 성공하였습니다."
    )
    @PostMapping("/login/google")
    public BaseResponse<TokenResponse> loginGoogle(@RequestBody @Valid GoogleLoginRequest request) {
        return BaseResponse.ok(googleAuthService.login(request));
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "로그아웃에 성공하였습니다."
    )
    @PostMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest request){
        jwtService.logout(request);
        return BaseResponse.ok(null);
    }

    @Operation(summary = "토큰 재발급", description = "토큰을 재발급합니다." +
            " 리프레쉬 토큰의 헤더는 Authorization-refresh 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "엑세스 토큰 재발급 성공하였습니다.",
            headers = {
            @Header(
                    name = "Authorization",
                    description = "재발급된 Access Token (Bearer {accessToken})",
                    schema = @Schema(type = "string")
            ),
            @Header(
                    name = "Authorization-refresh",
                    description = "재발급된 Refresh Token (Bearer {refreshToken})",
                    schema = @Schema(type = "string")
            )
    }
    )
    @SecurityRequirement(name = "RefreshAuth")
    @PostMapping("/reissue")
    public BaseResponse<TokenResponse> reissueTokens(HttpServletRequest request,
                                                     @CurrentUserId Long userId) {
        TokenResponse response = jwtService.reissueTokens(request, userId);
        return BaseResponse.ok(response);
    }
}
