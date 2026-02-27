package com.umust.dobonglife.domain.promotion.exception;

import com.umust.dobonglife.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PromotionErrorCode implements ErrorCode {

    // 조회
    PROMOTION_NOT_FOUND(HttpStatus.NOT_FOUND, "프로모션을 찾을 수 없습니다."),

    // 권한
    NOT_PROMOTION_OWNER(HttpStatus.FORBIDDEN, "해당 프로모션에 대한 권한이 없습니다."),

    // 생성 검증 - 코드
    INVALID_COUPON_CODE(HttpStatus.BAD_REQUEST, "쿠폰 코드는 6자리여야 합니다."),

    // 생성 검증 - 할인
    INVALID_DISCOUNT_VALUE(HttpStatus.BAD_REQUEST, "할인 값은 0 이상이어야 합니다."),
    DISCOUNT_PERCENT_EXCEEDED(HttpStatus.BAD_REQUEST, "퍼센트 할인은 100을 초과할 수 없습니다."),

    // 생성 검증 - 가격
    NEGATIVE_PRICE(HttpStatus.BAD_REQUEST, "가격은 음수일 수 없습니다."),
    MIN_PRICE_EXCEEDS_MAX(HttpStatus.BAD_REQUEST, "최소 금액이 최대 금액보다 클 수 없습니다."),

    // 생성 검증 - 기간
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "시작일은 종료일보다 이전이어야 합니다."),

    // 수정 검증
    QUANTITY_BELOW_ISSUED(HttpStatus.BAD_REQUEST, "총 수량은 이미 발급된 수량보다 적을 수 없습니다."),

    // 발급
    PROMOTION_PERIOD_EXPIRED(HttpStatus.CONFLICT, "쿠폰을 발급할 수 있는 기간이 지났습니다."),
    COUPON_SOLD_OUT(HttpStatus.CONFLICT, "쿠폰의 재고가 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
