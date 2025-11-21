package com.umust.dobonglife.course.exception;

import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.ErrorCode;

public class CourseException extends BusinessException {
    public CourseException(ErrorCode code) {
        super(code);
    }
}
