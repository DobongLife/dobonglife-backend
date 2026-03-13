package com.umust.dobonglife.domain.coupon.exception;

import com.umust.dobonglife.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CouponErrorCode implements ErrorCode {

    INVALID_COUPON_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 쿠폰 ID입니다."),
    INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 인증 코드입니다."),
    COUPON_CANNOT_USE(HttpStatus.CONFLICT, "쿠폰을 사용할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
