package com.umust.dobonglife.domain.auth.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.umust.dobonglife.domain.auth.controller.dto.request.AppleLoginRequest;
import com.umust.dobonglife.domain.auth.controller.dto.response.TokenResponse;
import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleAuthService {

    private static final String APPLE_JWKS_URL = "https://appleid.apple.com/auth/keys";
    private static final String APPLE_ISSUER = "https://appleid.apple.com";
    private static final String APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token";
    private static final String APPLE_REVOKE_URL = "https://appleid.apple.com/auth/revoke";
    private static final long CACHE_TTL_MS = 24 * 60 * 60 * 1000L;
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 5000;
    private static final int SIZE_LIMIT_BYTES = 50 * 1024;

    private final RestTemplate restTemplate = createRestTemplate();
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final JwtService jwtService;

    @Value("${apple.client-id}")
    private String appleClientId;

    @Value("${apple.team-id}")
    private String appleTeamId;

    @Value("${apple.key-id}")
    private String appleKeyId;

    @Value("${apple.private-key-path}")
    private String applePrivateKeyPath;

    private volatile JWKSet cachedJwkSet;
    private volatile long cacheTimestamp;
    private final ReentrantLock jwkLock = new ReentrantLock();

    @Transactional
    public TokenResponse login(AppleLoginRequest request) {
        JWTClaimsSet claims = verifyIdentityToken(request.getIdentityToken());

        String sub = claims.getSubject();
        String email = (String) claims.getClaim("email");

        if (email == null || email.isBlank()) {
            throw new BusinessException(ErrorCode.APPLE_TOKEN_EMAIL_MISSING);
        }

        User user = userService.findOrCreateOAuthUser(
                Provider.APPLE, sub, email, email
        );

        if (request.getFcmToken() != null && !request.getFcmToken().isBlank()) {
            user.setFcmToken(request.getFcmToken());
        }

        if (request.getProviderToken() != null && !request.getProviderToken().isBlank()) {
            String refreshToken = exchangeAuthorizationCodeForRefreshToken(request.getProviderToken());
            user.setProviderToken(refreshToken);
        } else {
            log.warn("[Apple Login] providerToken(authorizationCode)이 요청에 없음: userId={}", user.getId());
        }

        String access = jwtUtil.createAccessToken(user.getId(), Provider.APPLE.getValue(), Role.PREFIX + user.getRole().name(), user.getName());
        String refresh = jwtUtil.createRefreshToken(user.getId(), Provider.APPLE.getValue(), Role.PREFIX + user.getRole().name(), user.getName());

        jwtService.storeRefreshToken(refresh, user.getId());

        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .role(Role.PREFIX + user.getRole().name())
                .build();
    }

    public void revokeToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            log.warn("애플 토큰 해제 생략: refreshToken이 없습니다");
            return;
        }
        try {
            String clientSecret = generateClientSecret();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", appleClientId);
            body.add("client_secret", clientSecret);
            body.add("token", refreshToken);
            body.add("token_type_hint", "refresh_token");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(APPLE_REVOKE_URL, entity, String.class);
            log.info("애플 토큰 해제 성공");
        } catch (Exception e) {
            log.warn("애플 토큰 해제 실패: error={}", e.getMessage());
        }
    }

    private String exchangeAuthorizationCodeForRefreshToken(String authorizationCode) {
        try {
            String clientSecret = generateClientSecret();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", appleClientId);
            body.add("client_secret", clientSecret);
            body.add("code", authorizationCode);
            body.add("grant_type", "authorization_code");

            log.info("[Apple Token Exchange] client_id={}, code_length={}", appleClientId, authorizationCode.length());

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
            var response = restTemplate.postForEntity(APPLE_TOKEN_URL, entity, java.util.Map.class);

            if (response.getBody() != null && response.getBody().containsKey("refresh_token")) {
                log.info("애플 authorization_code → refresh_token 교환 성공");
                return (String) response.getBody().get("refresh_token");
            }
            log.error("[Apple Token Exchange] refresh_token 없음: response_body={}", response.getBody());
            throw new BusinessException(ErrorCode.APPLE_TOKEN_EXCHANGE_FAILED);
        } catch (BusinessException e) {
            throw e;
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            log.error("[Apple Token Exchange] HTTP {} response={}", e.getStatusCode(), responseBody);
            throw new BusinessException(mapAppleTokenError(responseBody));
        } catch (Exception e) {
            log.error("[Apple Token Exchange] 예외 발생: class={}, message={}", e.getClass().getSimpleName(), e.getMessage());
            throw new BusinessException(ErrorCode.APPLE_TOKEN_EXCHANGE_FAILED);
        }
    }

    private ErrorCode mapAppleTokenError(String responseBody) {
        if (responseBody == null) {
            return ErrorCode.APPLE_TOKEN_EXCHANGE_FAILED;
        }
        if (responseBody.contains("invalid_grant")) {
            return ErrorCode.APPLE_TOKEN_EXCHANGE_INVALID_GRANT;
        }
        if (responseBody.contains("invalid_client")) {
            return ErrorCode.APPLE_TOKEN_EXCHANGE_INVALID_CLIENT;
        }
        return ErrorCode.APPLE_TOKEN_EXCHANGE_FAILED;
    }

    private String generateClientSecret() throws Exception {
        String keyContent = new String(Files.readAllBytes(new File(applePrivateKeyPath).toPath()))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(keyContent);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        ECPrivateKey privateKey = (ECPrivateKey) keyFactory.generatePrivate(keySpec);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + 15777000000L); // ~6 months

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .keyID(appleKeyId)
                .build();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(appleTeamId)
                .issueTime(now)
                .expirationTime(expiration)
                .audience(APPLE_ISSUER)
                .subject(appleClientId)
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claims);
        signedJWT.sign(new ECDSASigner(privateKey));

        return signedJWT.serialize();
    }

    private JWTClaimsSet verifyIdentityToken(String identityToken) {
        try {
            return processToken(identityToken, getAppleJwkSet());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Apple Identity Token 검증 실패, JWKS 강제 갱신 후 재시도: {}", e.getMessage());
            try {
                return processToken(identityToken, forceRefreshAppleJwkSet());
            } catch (BusinessException be) {
                throw be;
            } catch (BadJWTException retryEx) {
                log.error("Apple Identity Token claims 검증 실패 (만료 등): {}", retryEx.getMessage());
                throw new BusinessException(ErrorCode.APPLE_TOKEN_EXPIRED);
            } catch (BadJOSEException retryEx) {
                log.error("Apple Identity Token 서명 검증 실패: {}", retryEx.getMessage());
                throw new BusinessException(ErrorCode.APPLE_TOKEN_SIGNATURE_INVALID);
            } catch (Exception retryEx) {
                log.error("Apple Identity Token 검증 실패: {}", retryEx.getMessage());
                throw new BusinessException(ErrorCode.APPLE_TOKEN_SIGNATURE_INVALID);
            }
        }
    }

    private JWTClaimsSet processToken(String identityToken, JWKSet jwkSet) throws Exception {
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(
                JWSAlgorithm.RS256,
                new ImmutableJWKSet<>(jwkSet)
        );
        jwtProcessor.setJWSKeySelector(keySelector);

        JWTClaimsSet claims = jwtProcessor.process(identityToken, null);

        if (!APPLE_ISSUER.equals(claims.getIssuer())) {
            log.error("Apple Identity Token issuer 불일치: expected={}, actual={}", APPLE_ISSUER, claims.getIssuer());
            throw new BusinessException(ErrorCode.APPLE_TOKEN_ISSUER_MISMATCH);
        }

        if (claims.getAudience() == null || !claims.getAudience().contains(appleClientId)) {
            log.error("Apple Identity Token audience 불일치: expected={}, actual={}", appleClientId, claims.getAudience());
            throw new BusinessException(ErrorCode.APPLE_TOKEN_AUDIENCE_MISMATCH);
        }

        return claims;
    }

    private JWKSet getAppleJwkSet() {
        if (cachedJwkSet != null && (System.currentTimeMillis() - cacheTimestamp) < CACHE_TTL_MS) {
            return cachedJwkSet;
        }
        return refreshAppleJwkSet();
    }

    private JWKSet forceRefreshAppleJwkSet() {
        jwkLock.lock();
        try {
            JWKSet freshJwkSet = JWKSet.load(new URL(APPLE_JWKS_URL), CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS, SIZE_LIMIT_BYTES);
            cachedJwkSet = freshJwkSet;
            cacheTimestamp = System.currentTimeMillis();
            return freshJwkSet;
        } catch (Exception e) {
            log.warn("Apple JWK Set 강제 갱신 실패, 기존 캐시 사용", e);
            if (cachedJwkSet != null) {
                return cachedJwkSet;
            }
            throw new BusinessException(ErrorCode.APPLE_JWKS_FETCH_FAILED);
        } finally {
            jwkLock.unlock();
        }
    }

    private JWKSet refreshAppleJwkSet() {
        jwkLock.lock();
        try {
            if (cachedJwkSet != null && (System.currentTimeMillis() - cacheTimestamp) < CACHE_TTL_MS) {
                return cachedJwkSet;
            }

            JWKSet freshJwkSet = JWKSet.load(new URL(APPLE_JWKS_URL), CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS, SIZE_LIMIT_BYTES);
            cachedJwkSet = freshJwkSet;
            cacheTimestamp = System.currentTimeMillis();
            return freshJwkSet;
        } catch (Exception e) {
            log.warn("Apple JWK Set 갱신 실패, 기존 캐시 사용", e);
            if (cachedJwkSet != null) {
                return cachedJwkSet;
            }
            throw new BusinessException(ErrorCode.APPLE_JWKS_FETCH_FAILED);
        } finally {
            jwkLock.unlock();
        }
    }

    private static RestTemplate createRestTemplate() {
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT_MS);
        factory.setReadTimeout(READ_TIMEOUT_MS);
        return new RestTemplate(factory);
    }
}
