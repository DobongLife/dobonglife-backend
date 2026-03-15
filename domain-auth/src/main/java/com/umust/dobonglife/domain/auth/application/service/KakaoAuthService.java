package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.in.VerifyKakaoTokenUseCase;
import com.umust.dobonglife.domain.auth.domain.SocialUserInfo;
import com.umust.dobonglife.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.umust.dobonglife.global.error.DomainErrorCode;
import com.umust.dobonglife.domain.auth.application.service.dto.KakaoUserInfo;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService implements VerifyKakaoTokenUseCase {

    private static final String KAKAO_UNLINK_URL = "https://kapi.kakao.com/v1/user/unlink";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String USER_INFO_URI;

    @Value("${kakao.admin-key}")
    private String kakaoAdminKey;

    @Override
    public SocialUserInfo verify(String accessToken) {
        KakaoUserInfo userInfo = getUserInfo(accessToken);
        return new SocialUserInfo(
                String.valueOf(userInfo.id()),
                userInfo.kakaoAccount().email(),
                userInfo.kakaoAccount().profile().nickname()
        );
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
                throw new BusinessException(DomainErrorCode.INVALID_KAKAO_ACCESS_TOKEN);
            }
            return res.getBody();
        } catch (Exception e) {
            throw new BusinessException(DomainErrorCode.INVALID_KAKAO_ACCESS_TOKEN);
        }
    }
}
