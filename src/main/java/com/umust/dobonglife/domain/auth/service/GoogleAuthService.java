package com.umust.dobonglife.domain.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.umust.dobonglife.domain.auth.controller.dto.response.OAuth2Response;
import com.umust.dobonglife.domain.auth.controller.dto.response.TokenResponse;
import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.auth.domain.entity.UserPrincipal;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.umust.dobonglife.global.error.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UserService userService; // findOrCreate 같은거
    private final JwtUtil jwtUtil;


    @Value("${oauth.google.client-id}")
    private String googleClientId;

    public TokenResponse login(String idToken) {
        GoogleIdToken.Payload payload = verify(idToken);

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String providerId = payload.getSubject();

        User user = userService.findOrCreateOAuthUser(
                Provider.GOOGLE, providerId, email, name
        );

        String access = jwtUtil.createAccessToken(user.getId(), "GOOGLE", user.getRole().name(), user.getName());
        String refresh = jwtUtil.createRefreshToken(user.getId(), "GOOGLE", user.getRole().name());

        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }

    private GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new JacksonFactory()
            )
                    .setAudience(List.of(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new BusinessException(ErrorCode.INVALID_GOOGLE_ID_TOKEN);
            }
            return idToken.getPayload();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_GOOGLE_ID_TOKEN);
        }
    }
}
