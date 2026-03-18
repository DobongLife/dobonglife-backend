package com.umust.dobonglife.domain.user.exception;

import com.umust.dobonglife.global.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    USER_NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    USER_MAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일의 사용자를 찾을 수 없습니다."),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 권한입니다."),
    USER_IS_SOCIAL_LOGGED(HttpStatus.BAD_REQUEST, "소셜 로그인 사용자는 비밀번호 변경이 불가능합니다."),
    USER_BLOCKED(HttpStatus.FORBIDDEN, "차단된 사용자입니다."),
    AUTH_CODE_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "이메일 인증을 하지 않았습니다."),
    INVALID_EMAIL_CODE(HttpStatus.UNAUTHORIZED, "인증 번호가 다릅니다."),
    COUPON_EXCHANGE_RESTRICTED(HttpStatus.FORBIDDEN, "리뷰 정책 위반(3회 삭제)으로 인해 쿠폰 교환이 제한되었습니다."),
    INVALID_POINT(HttpStatus.BAD_REQUEST, "포인트가 부족합니다."),
    POINT_CANNOT_NEGATIVE(HttpStatus.CONFLICT, "포인트 내역은 음수가 될 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getStatus() {
        return httpStatus.value();
    }

    @Override
    public String getCode() {
        return name();
    }
}
