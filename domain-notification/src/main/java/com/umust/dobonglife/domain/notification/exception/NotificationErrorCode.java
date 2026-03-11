package com.umust.dobonglife.domain.notification.exception;

import com.umust.dobonglife.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    FORBIDDEN_USER_ID(HttpStatus.FORBIDDEN, "이 알림에 대한 권한이 없습니다."),
    INVALID_NOTIFICATION_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 알림 ID입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
