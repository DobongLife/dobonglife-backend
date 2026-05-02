package com.umust.dobonglife.domain.auth.infrastructure;

import com.umust.dobonglife.domain.auth.application.dto.KakaoUserInfo;
import com.umust.dobonglife.domain.auth.exception.AuthErrorCode;
import com.umust.dobonglife.global.common.error.exception.BusinessException;
import com.umust.dobonglife.global.port.auth.dto.SocialAuthUserInfo;
import com.umust.dobonglife.global.port.auth.out.KakaoOAuthPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class KakaoOAuthAdapter implements KakaoOAuthPort {

    private static final String KAKAO_UNLINK_URL = "https://kapi.kakao.com/v1/user/unlink";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String userInfoUri;

    @Value("${kakao.admin-key}")
    private String kakaoAdminKey;

    @Override
    public SocialAuthUserInfo verify(String accessToken) {
        KakaoUserInfo userInfo = getUserInfo(accessToken);
        return new SocialAuthUserInfo(
                String.valueOf(userInfo.id()),
                userInfo.kakaoAccount().email(),
                userInfo.kakaoAccount().profile().nickname()
        );
    }

    @Override
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
                    restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, KakaoUserInfo.class);

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                throw new BusinessException(AuthErrorCode.INVALID_KAKAO_ACCESS_TOKEN);
            }
            return res.getBody();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(AuthErrorCode.INVALID_KAKAO_ACCESS_TOKEN);
        }
    }
}
