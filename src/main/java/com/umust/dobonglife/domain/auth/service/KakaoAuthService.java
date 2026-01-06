package com.umust.dobonglife.domain.auth.service;

import com.umust.dobonglife.domain.auth.controller.dto.response.TokenResponse;
import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.umust.dobonglife.global.error.ErrorCode;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public TokenResponse login(String kakaoAccessToken) {
        KakaoUserInfo userInfo = getUserInfo(kakaoAccessToken);

        String providerId = String.valueOf(userInfo.id());
        String email = userInfo.kakaoAccount().email();   // 동의 안 하면 null일 수 있음
        String name = userInfo.kakaoAccount().profile().nickname();

        User user = userService.findOrCreateOAuthUser(
                Provider.KAKAO, providerId, email, name
        );

        String access = jwtUtil.createAccessToken(user.getId(), "KAKAO", user.getRole().name(), user.getName());
        String refresh = jwtUtil.createRefreshToken(user.getId(), "KAKAO", user.getRole().name());

        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }

    private KakaoUserInfo getUserInfo(String token) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfo> res =
                    restTemplate.exchange(url, HttpMethod.GET, entity, KakaoUserInfo.class);

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                throw new BusinessException(ErrorCode.INVALID_KAKAO_ACCESS_TOKEN);
            }
            return res.getBody();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_KAKAO_ACCESS_TOKEN);
        }
    }
}
