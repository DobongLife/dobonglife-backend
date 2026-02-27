package com.umust.dobonglife.domain.business.exception;

import com.umust.dobonglife.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BusinessErrorCode implements ErrorCode {

    NOT_BUSINESS(HttpStatus.FORBIDDEN, "해당 비즈니스에 대한 권한이 없습니다."),
    BUSINESS_NOT_FOUND(HttpStatus.NOT_FOUND, "사업장을 찾을 수 없습니다."),
    NOT_BUSINESS_OWNER(HttpStatus.FORBIDDEN, "사업장의 소유자가 아닙니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "할인값은 음수일 수 없습니다."),
    INVALID_DISCOUNT_VALUE_NEGATIVE(HttpStatus.BAD_REQUEST, "할인 100%를 초과할 수 없습니다."),
    INVALID_COUPON_CODE(HttpStatus.BAD_REQUEST, "쿠폰 코드는 6자리입니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "시작일은 종료일보다 빠를 수 없습니다."),
    COUPON_EXCHANGE_RESTRICTED(HttpStatus.FORBIDDEN, "리뷰 정책 위반(3회 삭제)으로 인해 쿠폰 교환이 제한되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
