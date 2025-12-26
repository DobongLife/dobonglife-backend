package com.umust.dobonglife.domain.auth.controller;

import com.umust.dobonglife.domain.auth.controller.dto.request.RefreshTokenRequest;
import com.umust.dobonglife.domain.auth.controller.dto.response.TokenResponse;
import com.umust.dobonglife.domain.auth.service.JwtService;
import com.umust.dobonglife.global.common.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "인증 인가 API", description = "인증 인가 관련 API")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(("/api/auth"))
@RestController
public class AuthController {

    private final JwtService jwtService;

    @Operation(summary = "카카오 로그인", description = "카카오 로그인을 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "카카오 소셜 로그인에 성공하였습니다."
    )
    @GetMapping("/login/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/kakao");

    }

    @Operation(summary = "구글 로그인", description = "구글 로그인을 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "구글 소셜 로그인에 성공하였습니다."
    )
    @GetMapping("/login/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "로그아웃에 성공하였습니다."
    )
    @PostMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest request,
                                     @RequestBody RefreshTokenRequest tokenRequest){
        jwtService.logout(request, tokenRequest);
        return BaseResponse.ok(null);
    }

    @Operation(summary = "토큰 재발급", description = "토큰을 재발급합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "엑세스 토큰 재발급 성공하였습니다."
    )
    @PostMapping("/reissue")
    public BaseResponse<TokenResponse> reissueTokens(@RequestBody RefreshTokenRequest tokenRequest,
                                                     @CurrentUserId Long userId) {
        TokenResponse response = jwtService.reissueTokens(tokenRequest, userId);
        return BaseResponse.ok(response);
    }
}
