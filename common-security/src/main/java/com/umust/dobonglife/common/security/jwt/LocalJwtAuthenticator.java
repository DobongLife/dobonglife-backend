package com.umust.dobonglife.common.security.jwt;

import com.umust.dobonglife.domain.auth.application.dto.AuthenticatedUser;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.out.TokenStore;
import com.umust.dobonglife.domain.auth.exception.AuthErrorCode;
import com.umust.dobonglife.domain.auth.exception.CustomJwtException;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class LocalJwtAuthenticator implements AuthenticateAccessTokenUseCase {

    private static final String LOGOUT_VALUE = "logout";

    private final SecretKey secretKey;
    private final TokenStore tokenStore;

    public LocalJwtAuthenticator(
            @Value("${jwt.secret}") String secret,
            TokenStore tokenStore
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.tokenStore = tokenStore;
    }

    @Override
    public AuthenticatedUser authenticate(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (!"access".equals(claims.get("tokenType", String.class))) {
            throw new CustomJwtException(AuthErrorCode.INVALID_TOKEN_TYPE);
        }

        if (tokenStore.find(accessToken).filter(LOGOUT_VALUE::equals).isPresent()) {
            throw new CustomJwtException(AuthErrorCode.SECURITY_UNAUTHORIZED);
        }

        return new AuthenticatedUser(
                claims.get("userId", Long.class),
                claims.get("name", String.class),
                Role.fromRole(claims.get("role", String.class)),
                Provider.fromProvider(claims.get("provider", String.class))
        );
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException(AuthErrorCode.EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomJwtException(AuthErrorCode.UNSUPPORTED_TOKEN_TYPE);
        } catch (MalformedJwtException e) {
            throw new CustomJwtException(AuthErrorCode.MALFORMED_TOKEN_TYPE);
        } catch (SignatureException e) {
            throw new CustomJwtException(AuthErrorCode.INVALID_SIGNATURE_JWT);
        } catch (IllegalArgumentException e) {
            throw new CustomJwtException(AuthErrorCode.EMPTY_AUTHORIZATION_HEADER);
        } catch (Exception e) {
            throw new CustomJwtException(AuthErrorCode.SECURITY_INVALID_TOKEN);
        }
    }
}
