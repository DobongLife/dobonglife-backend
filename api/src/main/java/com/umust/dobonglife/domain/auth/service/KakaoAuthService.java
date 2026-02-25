package com.umust.dobonglife.domain.auth.service;

import com.umust.dobonglife.domain.auth.dto.request.KakaoLoginRequest;
import com.umust.dobonglife.domain.auth.dto.response.TokenResponse;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.global.common.constant.Role;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.domain.auth.service.dto.KakaoUserInfo;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private static final String KAKAO_UNLINK_URL = "https://kapi.kakao.com/v1/user/unlink";

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final JwtService jwtService;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String USER_INFO_URI;

    @Value("${kakao.admin-key}")
    private String kakaoAdminKey;

    @Transactional
    public TokenResponse login(KakaoLoginRequest request) {
        KakaoUserInfo userInfo = getUserInfo(request.getAccessToken());

        String providerId = String.valueOf(userInfo.id());
        String email = userInfo.kakaoAccount().email();
        String name = userInfo.kakaoAccount().profile().nickname();

        User user = userService.findOrCreateOAuthUser(
                Provider.KAKAO, providerId, email, name
        );

        if (request.getFcmToken() != null && !request.getFcmToken().isBlank()) {
            user.setFcmToken(request.getFcmToken());
        }

        String access = jwtUtil.createAccessToken(user.getId(), Provider.KAKAO.getValue(), Role.PREFIX + user.getRole().name(), user.getName());
        String refresh = jwtUtil.createRefreshToken(user.getId(), Provider.KAKAO.getValue(), Role.PREFIX + user.getRole().name(), user.getName());

        jwtService.storeRefreshToken(refresh, user.getId());

        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .role(Role.PREFIX + user.getRole().name())
                .build();
    }

    public void unlinkUser(String providerId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoAdminKey);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("target_id_type", "user_id");
            body.add("target_id", providerId);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(KAKAO_UNLINK_URL, entity, String.class);
            log.info("카카오 연결 해제 성공: providerId={}", providerId);
        } catch (Exception e) {
            log.warn("카카오 연결 해제 실패: providerId={}, error={}", providerId, e.getMessage());
        }
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
