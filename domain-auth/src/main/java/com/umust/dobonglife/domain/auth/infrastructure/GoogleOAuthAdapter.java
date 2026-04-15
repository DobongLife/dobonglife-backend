package com.umust.dobonglife.domain.auth.infrastructure;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.umust.dobonglife.domain.auth.exception.AuthErrorCode;
import com.umust.dobonglife.global.common.error.exception.BusinessException;
import com.umust.dobonglife.global.port.auth.dto.SocialAuthUserInfo;
import com.umust.dobonglife.global.port.auth.out.GoogleOAuthPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
public class GoogleOAuthAdapter implements GoogleOAuthPort {

    private static final String GOOGLE_REVOKE_URL = "https://oauth2.googleapis.com/revoke";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Override
    public SocialAuthUserInfo verify(String idTokenString) {
        GoogleIdToken.Payload payload = verifyIdToken(idTokenString);
        return new SocialAuthUserInfo(
                payload.getSubject(),
                payload.getEmail(),
                (String) payload.get("name")
        );
    }

    @Override
    public void revokeToken(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            log.warn("구글 토큰 해제 생략: accessToken이 없습니다");
            return;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("token", accessToken);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(GOOGLE_REVOKE_URL, entity, String.class);
            log.info("구글 토큰 해제 성공");
        } catch (Exception e) {
            log.warn("구글 토큰 해제 실패: error={}", e.getMessage());
        }
    }

    private GoogleIdToken.Payload verifyIdToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(List.of(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new BusinessException(AuthErrorCode.INVALID_GOOGLE_ID_TOKEN);
            }
            return idToken.getPayload();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(AuthErrorCode.INVALID_GOOGLE_ID_TOKEN);
        }
    }
}
