package com.umust.dobonglife.domain.auth.utils;

import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.jooeon.mybeauty.global.common.exception.exception.auth.JwtException;
import me.jooeon.mybeauty.global.common.model.enums.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
public class JwtUtil {

    private SecretKey secretKey;

    @Value("${secret.jwt-access-expired-in}")
    private Long ACCESS_TOKEN_EXPIRED_IN;

    @Value("${secret.jwt-refresh-expired-in}")
    private Long REFRESH_TOKEN_EXPIRED_IN;

    public final String BEARER = "Bearer ";

    public JwtUtil(@Value("${secret.jwt-secret-key}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Long getMemberId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("memberId", Long.class);
    }

    public String getProviderId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("providerId", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String getTokenType(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("tokenType", String.class);
    }

    public String getEmail(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getName(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("name", String.class);
    }

    public Boolean isTokenExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createAccessToken(Long memberId, String providerId, String role, String name) {

        return Jwts.builder()
                .claim("tokenType", "access")
                .claim("memberId", memberId)
                .claim("providerId", providerId)
                .claim("role", role)
                .claim("name", name)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRED_IN))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(Long memberId, String providerId, String role) {

        return Jwts.builder()
                .claim("tokenType", "refresh")
                .claim("memberId", memberId)
                .claim("providerId", providerId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRED_IN))
                .signWith(secretKey)
                .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) { // 토큰 만료
            throw new JwtException(BaseResponseStatus.EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException e) { // 지원되지 않는 형식
            throw new JwtException(BaseResponseStatus.UNSUPPORTED_TOKEN_TYPE);
        } catch (MalformedJwtException e) { // 구조가 잘못된 토큰
            throw new JwtException(BaseResponseStatus.MALFORMED_TOKEN_TYPE);
        } catch (SignatureException e) { // 서명 위조 (곧 지원 중단)
            throw new JwtException(BaseResponseStatus.INVALID_SIGNATURE_JWT);
        } catch (IllegalArgumentException e) { // 토큰이 비어 있거나 Null
            throw new JwtException(BaseResponseStatus.EMPTY_AUTHORIZATION_HEADER);
        } catch (Exception e) { // 기타 예외 상황
            throw new JwtException(BaseResponseStatus.INVALID_ACCESS_TOKEN);
        }
    }

    public String getUserNameFromToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("name", String.class);
    }

    public String resolveAccessToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("REFRESH_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

//    public String resolveToken(HttpServletRequest request) {
////        // 1. Authorization 헤더 방식
////        String bearerToken = request.getHeader(AUTHORIZATION);
////        log.info("token = {}", bearerToken);
////        if((StringUtils.hasText(bearerToken)) && bearerToken.startsWith(BEARER)) {
////            log.info("token = {}", bearerToken);
////            return bearerToken.substring(BEARER.length());
////        }
//        // 2. 쿠키에서 ACCESS_TOKEN 조회
//        if (request.getCookies() != null) {
//            for (Cookie cookie : request.getCookies()) {
//                if ("ACCESS_TOKEN".equals(cookie.getName())) {
//                    return cookie.getValue();
//                }
//            }
//        }
//        return null;
//    }

    public Optional<String> extractAccessToken(HttpServletRequest request, String accessHeader) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request, String refreshHeader) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }
}
