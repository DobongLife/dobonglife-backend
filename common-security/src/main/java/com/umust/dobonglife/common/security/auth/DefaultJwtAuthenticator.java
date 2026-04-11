package com.umust.dobonglife.common.security.auth;

import com.umust.dobonglife.domain.auth.application.dto.AuthenticatedUser;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.auth.exception.AuthErrorCode;
import com.umust.dobonglife.domain.auth.exception.CustomJwtException;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class DefaultJwtAuthenticator implements AuthenticateAccessTokenUseCase {

    private final SecretKey secretKey;

    public DefaultJwtAuthenticator(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public AuthenticatedUser authenticate(String accessToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();

            String tokenType = claims.get("tokenType", String.class);
            if (!"access".equals(tokenType)) {
                throw new CustomJwtException(AuthErrorCode.INVALID_TOKEN_TYPE);
            }

            return new AuthenticatedUser(
                    claims.get("userId", Long.class),
                    claims.get("name", String.class),
                    Role.fromRole("ROLE_" + claims.get("role", String.class)),
                    Provider.fromProvider(claims.get("provider", String.class))
            );
        } catch (CustomJwtException e) {
            throw e;
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException(AuthErrorCode.EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomJwtException(AuthErrorCode.UNSUPPORTED_TOKEN_TYPE);
        } catch (MalformedJwtException e) {
            throw new CustomJwtException(AuthErrorCode.MALFORMED_TOKEN_TYPE);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            throw new CustomJwtException(AuthErrorCode.INVALID_SIGNATURE_JWT);
        } catch (Exception e) {
            throw new CustomJwtException(AuthErrorCode.SECURITY_INVALID_TOKEN);
        }
    }
}
