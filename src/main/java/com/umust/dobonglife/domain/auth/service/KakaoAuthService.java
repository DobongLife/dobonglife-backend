package com.umust.dobonglife.domain.auth.service;

import com.umust.dobonglife.domain.auth.controller.dto.response.TokenResponse;
import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.domain.auth.service.dto.KakaoUserInfo;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String USER_INFO_URI;

    public TokenResponse login(String kakaoAccessToken) {
        KakaoUserInfo userInfo = getUserInfo(kakaoAccessToken);

        String providerId = String.valueOf(userInfo.id());
        String email = userInfo.kakaoAccount().email();
        String name = userInfo.kakaoAccount().profile().nickname();

        User user = userService.findOrCreateOAuthUser(
                Provider.KAKAO, providerId, email, name
        );

        String access = jwtUtil.createAccessToken(user.getId(), Provider.KAKAO.getValue(), Role.PREFIX + user.getRole().name(), user.getName());
        String refresh = jwtUtil.createRefreshToken(user.getId(), Provider.KAKAO.getValue(), Role.PREFIX + user.getRole().name(), user.getName());

        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }

    private KakaoUserInfo getUserInfo(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfo> res =
                    restTemplate.exchange(USER_INFO_URI, HttpMethod.GET, entity, KakaoUserInfo.class);

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                throw new BusinessException(ErrorCode.INVALID_KAKAO_ACCESS_TOKEN);
            }
            return res.getBody();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_KAKAO_ACCESS_TOKEN);
        }
    }
}
