package com.umust.dobonglife.domain.course.exception;

import com.umust.dobonglife.global.common.error.exception.BusinessException;

public class CourseException extends BusinessException {

    public CourseException(CourseErrorCode errorCode) {
        super(errorCode);
    }

    public CourseException(CourseErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
