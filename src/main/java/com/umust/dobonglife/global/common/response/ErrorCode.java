package com.umust.dobonglife.global.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode{

    // Common
    ILLEGAL_ARGUMENT(100, HttpStatus.BAD_REQUEST.value(), "잘못된 요청값입니다."),
    NOT_FOUND(101, HttpStatus.NOT_FOUND.value(), "존재하지 않는 API 입니다."),
    METHOD_NOT_ALLOWED(102, HttpStatus.METHOD_NOT_ALLOWED.value(), "유효하지 않은 Http 메서드입니다."),
    SERVER_ERROR(103, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 오류가 발생했습니다."),

    // User
    USER_NOT_FOUND(200, HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다."),
    USER_DUPLICATE_EMAIL(201, HttpStatus.BAD_REQUEST.value(), "중복된 이메일의 사용자가 있습니다."),
    USER_DUPLICATE_NICKNAME(202, HttpStatus.BAD_REQUEST.value(), "중복된 닉네임의 사용자가 있습니다."),
    USER_MAIL_NOT_FOUND(203, HttpStatus.NOT_FOUND.value(), "해당 이메일의 사용자를 찾을 수 없습니다."),
    USER_ROLE_BAD_REQUEST(203, HttpStatus.BAD_REQUEST.value(), "잘못된 Role의 요청값 입니다."),

    // Place
    PLACE_NOT_FOUND(300, HttpStatus.NOT_FOUND.value(), "장소를 찾을 수 없습니다."),

    // Course
    COURSE_NOT_FOUND(400, HttpStatus.NOT_FOUND.value(), "코스를 찾을 수 없습니다."),

    // Auth
    SECURITY_UNAUTHORIZED(600,HttpStatus.UNAUTHORIZED.value(), "인증 정보가 유효하지 않습니다"),
    SECURITY_INVALID_TOKEN(602, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 token입니다."),
    SECURITY_INVALID_ACCESS_TOKEN(603, HttpStatus.UNAUTHORIZED.value(), "access token이 유효하지 않습니다."),
    SECURITY_ACCESS_DENIED(604, HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다."),
    INVALID_REFRESH_TYPE(605, HttpStatus.BAD_REQUEST.value(), "refresh token 타입이 유효하지 않습니다."),
    INVALID_TOKEN_TYPE(601, HttpStatus.UNAUTHORIZED.value(), "access token 타입이 유효하지 않습니다."),
    MAIL_SEND_FAILED(606, HttpStatus.BAD_REQUEST.value(), "메일 전송에 실패했습니다."),
    INVALID_EMAIL_CODE(607, HttpStatus.UNAUTHORIZED.value(), "인증 번호가 다릅니다."),
    EXPIRED_EMAIL_CODE(608, HttpStatus.UNAUTHORIZED.value(), "인증 번호가 만료되었거나 없습니다."),
    AUTHCODE_ALREADY_AUTHENTICATED(609, HttpStatus.BAD_REQUEST.value(), "이미 인증이 된 번호입니다."),
    AUTHCODE_UNAUTHORIZED(610, HttpStatus.UNAUTHORIZED.value(), "이메일 인증을 하지 않았습니다."),
    EMPTY_AUTHORIZATION_HEADER(612, HttpStatus.BAD_REQUEST.value(),"Authorization 헤더가 존재하지 않습니다."),
    EXPIRED_ACCESS_TOKEN(613, HttpStatus.UNAUTHORIZED.value(), "이미 만료된 Access 토큰입니다."),
    UNSUPPORTED_TOKEN_TYPE(614, HttpStatus.UNAUTHORIZED.value(),"지원되지 않는 토큰 형식입니다."),
    MALFORMED_TOKEN_TYPE(615, HttpStatus.UNAUTHORIZED.value(),"인증 토큰이 올바르게 구성되지 않았습니다."),
    INVALID_SIGNATURE_JWT(616, HttpStatus.UNAUTHORIZED.value(), "인증 시그니처가 올바르지 않습니다"),
    INVALID_EMAIL_OR_PASSWORD(617, HttpStatus.UNAUTHORIZED.value(), "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_PASSWORD(618, HttpStatus.UNAUTHORIZED.value(), "기존 비밀번호가 유효하지 않습니다"),

    // Course
    INVALID_COURSE_ID(404, HttpStatus.NOT_FOUND.value(), "코스 아이디가 유효하지 않습니다"),
    NOT_COURSE_OWNER(403, HttpStatus.FORBIDDEN.value(), "해당 코스를 삭제할 권한이 없습니다."),

    // Img
    FAIL_IMG(500, HttpStatus.INTERNAL_SERVER_ERROR.value(),"S3에 이미지를 업로드하는데 실패했습니다"),
    INVALID_IMG(400, HttpStatus.BAD_REQUEST.value(), "잘못된 파일입니다. 파일 내용이 손상되었거나 유효하지 않습니다"),
    INVALID_IMG_FORMAT(415, HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "잘못된 파일 형식입니다. 허용되지 않은 파일 형식입니다."),
    NOT_FOUND_IMG(404, HttpStatus.NOT_FOUND.value(), "저장된 이미지가 없습니다"),

    // Notification
    FORBIDDEN_USER_ID(403, HttpStatus.FORBIDDEN.value(), "이 알림에 대한 권한이 없습니다."),
    INVALID_NOTIFICATION_ID(400, HttpStatus.BAD_REQUEST.value(), "존재하지 않는 알림 ID입니다."),

    // Coupon
    INVALID_COUPON_ID(401, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 쿠폰 아이디 입니다."),
    INVALID_CODE(401, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 인증코드 입니다."),

    // Promotion
    INVALID_PROMOTION_ID(401, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 프로모션 아이디 입니다."),

    // Review
    NOT_REVIEW_OWNER(403, HttpStatus.FORBIDDEN.value(), "해당 리뷰에 대한 권한이 없습니다."),

    // Point
    INVALID_POINT(400, HttpStatus.BAD_REQUEST.value(), "포인트가 부족합니다."),

    // FireBase
    SERVER_ERROR_FIREBASE(500, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Firebase 초기화에 실패했습니다."),
    SERVER_ERROR_MESSAGE(500, HttpStatus.INTERNAL_SERVER_ERROR.value(), "FCM 메시지 전송을 실패했습니다."),
    SERVER_ERROR_TOPIC(500, HttpStatus.INTERNAL_SERVER_ERROR.value(), "FCM 토픽 구독을 실패했습니다."),
    SERVER_ERROR_TOPIC_CANCEL(500, HttpStatus.INTERNAL_SERVER_ERROR.value(), "FCM 토픽 구독 취소를 실패했습니다.");

    private final int code;
    private final int httpStatus;
    private final String message;
}
