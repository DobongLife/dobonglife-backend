package com.umust.dobonglife.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DomainErrorCode implements ErrorCode {

    // Auth
    SECURITY_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 정보가 유효하지 않습니다."),
    SECURITY_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 token입니다."),
    SECURITY_INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "access token이 유효하지 않습니다."),
    SECURITY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_REFRESH_TYPE(HttpStatus.BAD_REQUEST, "refresh token 타입이 유효하지 않습니다."),
    INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "access token 타입이 유효하지 않습니다."),
    MAIL_SEND_FAILED(HttpStatus.BAD_REQUEST, "메일 전송에 실패했습니다."),
    INVALID_EMAIL_CODE(HttpStatus.UNAUTHORIZED, "인증 번호가 다릅니다."),
    EXPIRED_EMAIL_CODE(HttpStatus.UNAUTHORIZED, "인증 번호가 만료되었거나 없습니다."),
    AUTH_CODE_ALREADY_AUTHENTICATED(HttpStatus.BAD_REQUEST, "이미 인증이 된 번호입니다."),
    AUTH_CODE_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "이메일 인증을 하지 않았습니다."),
    EMPTY_AUTHORIZATION_HEADER(HttpStatus.BAD_REQUEST, "Authorization 헤더가 존재하지 않습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "이미 만료된 Access 토큰입니다."),
    UNSUPPORTED_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "지원되지 않는 토큰 형식입니다."),
    MALFORMED_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "인증 토큰이 올바르게 구성되지 않았습니다."),
    INVALID_SIGNATURE_JWT(HttpStatus.UNAUTHORIZED, "인증 시그니처가 올바르지 않습니다."),
    INVALID_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "기존 비밀번호가 유효하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "RefreshToken이 존재하지 않습니다."),
    INVALID_GOOGLE_ID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 idToken값 입니다."),
    INVALID_KAKAO_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 Kakao 엑세스 토큰값 입니다."),
    DUPLICATED_LOGIN(HttpStatus.UNAUTHORIZED, "다른 기기에서 로그인되어 현재 로그인이 종료되었습니다."),
    INVALID_APPLE_IDENTITY_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 Apple Identity Token 입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
