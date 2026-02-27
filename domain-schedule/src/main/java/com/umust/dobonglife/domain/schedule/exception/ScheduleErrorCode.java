package com.umust.dobonglife.domain.schedule.exception;

import com.umust.dobonglife.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ScheduleErrorCode implements ErrorCode {

    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "일정을 찾을 수 없습니다."),
    END_TIME_BEFORE_START_TIME(HttpStatus.BAD_REQUEST, "종료 시간은 시작 시간 이후여야 합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
