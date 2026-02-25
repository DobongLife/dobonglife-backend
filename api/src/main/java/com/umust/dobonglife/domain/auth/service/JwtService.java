package com.umust.dobonglife.domain.auth.service;

import com.umust.dobonglife.domain.auth.dto.response.TokenResponse;
import com.umust.dobonglife.domain.auth.exception.CustomAuthenticationException;
import com.umust.dobonglife.domain.auth.exception.CustomJwtException;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.infra.redis.RedisService;
import com.umust.dobonglife.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.access.expiration}")
    private Long ACCESS_TOKEN_EXPIRED_IN;

    @Value("${jwt.refresh.expiration}")
    private Long REFRESH_TOKEN_EXPIRED_IN;

    @Value("${jwt.access.header}")
    private String ACCESS_HEADER;

    @Value("${jwt.refresh.header}")
    private String REFRESH_HEADER;

    private static final String LOGOUT_VALUE = "logout";
    private static final String REFRESH_TOKEN_KEY_PREFIX = "auth:refresh:";
    private final String BEARER_PREFIX = "Bearer ";

    private final RedisService redisService;
    private final JwtUtil jwtUtil;

    public TokenResponse reissueTokens(jakarta.servlet.http.HttpServletRequest request, Long userId) {
        String refreshToken = jwtUtil.extractRefreshToken(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        jwtUtil.validateToken(refreshToken);
        if (!"refresh".equals(jwtUtil.getTokenType(refreshToken))) {
            throw new CustomJwtException(ErrorCode.INVALID_REFRESH_TYPE);
        }
        validateStoredRefreshToken(refreshToken);
        return reissueAndSendTokens(refreshToken, userId);
    }

    private void validateStoredRefreshToken(String refreshToken) {
        String stored = redisService.getValues(REFRESH_TOKEN_KEY_PREFIX + refreshToken).orElse(null);
        if (stored == null || "false".equals(stored)) {
            throw new CustomJwtException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
    }

    public void checkLogout(String accessToken) {
        String value = redisService.getValues(accessToken).orElse(null);
        if (LOGOUT_VALUE.equals(value)) {
            throw new CustomAuthenticationException(ErrorCode.SECURITY_UNAUTHORIZED);
        }
    }

    public void storeRefreshToken(String refreshToken, Long userId) {
        redisService.setValues(REFRESH_TOKEN_KEY_PREFIX+refreshToken, String.valueOf(userId), Duration.ofMillis(REFRESH_TOKEN_EXPIRED_IN));
    }

    public void deleteRefreshToken(String refreshToken){
        if(refreshToken == null){
            throw new CustomJwtException(ErrorCode.INVALID_REFRESH_TYPE);
        }
        redisService.delete(REFRESH_TOKEN_KEY_PREFIX+refreshToken);
    }

    public void invalidAccessToken(String accessToken) {
        redisService.setValues(accessToken, LOGOUT_VALUE,
                Duration.ofMillis(ACCESS_TOKEN_EXPIRED_IN));
    }

    private TokenResponse reissueAndSendTokens(String refreshToken, Long userId) {

        String reissuedAccessToken = jwtUtil.createAccessToken(jwtUtil.getUserId(refreshToken), jwtUtil.getProvider(refreshToken), jwtUtil.getRole(refreshToken), jwtUtil.getName(refreshToken));
        String reissuedRefreshToken = jwtUtil.createRefreshToken(jwtUtil.getUserId(refreshToken), jwtUtil.getProvider(refreshToken), jwtUtil.getRole(refreshToken), jwtUtil.getName(refreshToken));

        storeRefreshToken(reissuedRefreshToken, userId);

        deleteRefreshToken(refreshToken);

        return TokenResponse.builder()
                .accessToken(reissuedAccessToken)
                .refreshToken(reissuedRefreshToken)
                .build();
    }
}
