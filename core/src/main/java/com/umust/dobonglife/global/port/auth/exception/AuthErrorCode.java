package com.umust.dobonglife.global.port.auth.exception;

import com.umust.dobonglife.global.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    SECURITY_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 정보가 유효하지 않습니다."),
    SECURITY_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 token입니다."),
    SECURITY_INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "access token이 유효하지 않습니다."),
    SECURITY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_REFRESH_TYPE(HttpStatus.BAD_REQUEST, "refresh token 타입이 유효하지 않습니다."),
    INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "access token 타입이 유효하지 않습니다."),
    INVALID_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "기존 비밀번호가 유효하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "RefreshToken이 존재하지 않습니다."),
    EMPTY_AUTHORIZATION_HEADER(HttpStatus.BAD_REQUEST, "Authorization 헤더가 존재하지 않습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "이미 만료된 Access 토큰입니다."),
    UNSUPPORTED_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "지원되지 않는 토큰 형식입니다."),
    MALFORMED_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "인증 토큰이 올바르게 구성되지 않았습니다."),
    INVALID_SIGNATURE_JWT(HttpStatus.UNAUTHORIZED, "인증 시그니처가 올바르지 않습니다."),
    DUPLICATED_LOGIN(HttpStatus.UNAUTHORIZED, "다른 기기에서 로그인되어 현재 로그인이 종료되었습니다."),
    INVALID_GOOGLE_ID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 idToken값 입니다."),
    INVALID_KAKAO_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 Kakao 엑세스 토큰값 입니다."),
    INVALID_APPLE_IDENTITY_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 Apple Identity Token 입니다."),
    APPLE_JWKS_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Apple 공개키(JWKS) 조회에 실패했습니다."),
    APPLE_TOKEN_SIGNATURE_INVALID(HttpStatus.BAD_REQUEST, "Apple Identity Token 서명 검증에 실패했습니다."),
    APPLE_TOKEN_ISSUER_MISMATCH(HttpStatus.BAD_REQUEST, "Apple Identity Token 발급자(issuer)가 유효하지 않습니다."),
    APPLE_TOKEN_AUDIENCE_MISMATCH(HttpStatus.BAD_REQUEST, "Apple Identity Token 대상(audience)이 일치하지 않습니다."),
    APPLE_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "Apple Identity Token이 만료되었습니다."),
    APPLE_TOKEN_EMAIL_MISSING(HttpStatus.BAD_REQUEST, "Apple Identity Token에 이메일 정보가 없습니다."),
    APPLE_TOKEN_EXCHANGE_INVALID_GRANT(HttpStatus.BAD_REQUEST, "Apple 인증 코드가 만료되었거나 이미 사용되었습니다."),
    APPLE_TOKEN_EXCHANGE_INVALID_CLIENT(HttpStatus.INTERNAL_SERVER_ERROR, "Apple 클라이언트 인증에 실패했습니다."),
    APPLE_TOKEN_EXCHANGE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Apple 토큰 교환에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getStatus() { return httpStatus.value(); }

    @Override
    public String getCode() { return name(); }
}
