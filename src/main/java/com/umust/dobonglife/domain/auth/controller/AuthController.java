package com.umust.dobonglife.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(("/api/auth/"))
@RestController
public class AuthController {

    private final JwtUtil jwtUtil;
    private final JwtService jwtService;

    @GetMapping("/login/success")
    public BaseResponse<AccesstokenDTO> success(HttpServletRequest request) {

        // 쿠키에서 토큰 받기
        String accessToken = cookieUtil.getCookieValue(request, "ACCESS_TOKEN");
        String refreshToken = cookieUtil.getCookieValue(request, "REFRESH_TOKEN");
        log.info("Cookie AT: {} RT: {}", accessToken, refreshToken);

        // JwtInfo 객체 생성
        // JwtInfo jwtInfo = new JwtInfo(accessToken, refreshToken);

        String name = jwtUtil.getUserNameFromToken(accessToken); // 아래에 구현 설명

        AccesstokenDTO accesstokenDTO = new AccesstokenDTO(accessToken, name);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, accesstokenDTO);
    }

    @PostMapping("/logout")
    public BaseResponse<BaseResponseStatus> logout(HttpServletRequest request) {
        jwtService.logout(request);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    @PostMapping("/reissue")
    public BaseResponse<BaseResponseStatus> reissue(HttpServletRequest request, HttpServletResponse response) {
        jwtService.reissueToken(request, response);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }
}
