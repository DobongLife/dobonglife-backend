package com.umust.dobonglife.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DomainErrorCode implements ErrorCode {

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    USER_NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    USER_MAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일의 사용자를 찾을 수 없습니다."),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 권한입니다."),
    USER_IS_SOCIAL_LOGGED(HttpStatus.BAD_REQUEST, "소셜 로그인 사용자는 비밀번호 변경이 불가능합니다."),

    // Place
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "장소를 찾을 수 없습니다."),
    PLACE_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "장소 등록에는 최소 한 장의 사진이 필요합니다."),

    // Course
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "코스를 찾을 수 없습니다."),
    INVALID_COURSE_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 코스 ID입니다."),
    NOT_OWNER(HttpStatus.FORBIDDEN, "해당 기능에 대한 접근 권한이 없습니다."),
    COURSE_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "코스 비즈니스 로직 처리가 실패했습니다."),

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
    INVALID_APPLE_IDENTITY_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 Apple Identity Token 입니다."),

    // Notification
    FORBIDDEN_USER_ID(HttpStatus.FORBIDDEN, "이 알림에 대한 권한이 없습니다."),
    INVALID_NOTIFICATION_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 알림 ID입니다."),

    // Coupon
    INVALID_COUPON_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 쿠폰 ID입니다."),
    INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 인증 코드입니다."),
    COUPON_CANNOT_USE(HttpStatus.CONFLICT, "쿠폰을 사용할 수 없습니다."),

    // Promotion
    INVALID_PROMOTION_ID(HttpStatus.NOT_FOUND, "유효하지 않은 프로모션 아이디 입니다."),
    PROMOTION_NOT_FOUND(HttpStatus.NOT_FOUND, "프로모션을 찾을 수 없습니다."),
    PROMOTION_PERIOD_INVALID(HttpStatus.CONFLICT, "쿠폰을 발급할 수 있는 기간이 지났습니다."),
    COUPON_SOLD_OUT(HttpStatus.CONFLICT, "쿠폰의 재고가 없습니다."),

    // Review
    NOT_REVIEW_OWNER(HttpStatus.FORBIDDEN, "해당 리뷰에 대한 권한이 없습니다."),

    // Point
    INVALID_POINT(HttpStatus.BAD_REQUEST, "포인트가 부족합니다."),
    POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "포인트를 찾을 수 없습니다."),
    POINT_ALREADY_USED(HttpStatus.CONFLICT, "이미 사용된 포인트입니다."),
    POINT_CANNOT_NEGATIVE(HttpStatus.CONFLICT, "포인트 내역은 음수가 될 수 없습니다."),

    // Schedule
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "일정을 찾을 수 없습니다."),
    END_TIME_BEFORE_START_TIME(HttpStatus.BAD_REQUEST, "종료 시간은 시작 시간 이후여야 합니다."),

    // Business
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
