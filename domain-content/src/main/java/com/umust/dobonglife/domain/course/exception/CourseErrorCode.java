package com.umust.dobonglife.domain.course.exception;

import com.umust.dobonglife.global.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CourseErrorCode implements ErrorCode {

    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "코스를 찾을 수 없습니다."),
    INVALID_COURSE_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 코스 ID입니다."),
    NOT_OWNER(HttpStatus.FORBIDDEN, "해당 기능에 대한 접근 권한이 없습니다."),
    COURSE_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "코스 비즈니스 로직 처리가 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getStatus() { return httpStatus.value(); }

    @Override
    public String getCode() { return name(); }
}
