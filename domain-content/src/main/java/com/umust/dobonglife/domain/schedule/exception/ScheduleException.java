package com.umust.dobonglife.domain.schedule.exception;

import com.umust.dobonglife.global.common.error.exception.BusinessException;

public class ScheduleException extends BusinessException {

    public ScheduleException(ScheduleErrorCode errorCode) {
        super(errorCode);
    }

    public ScheduleException(ScheduleErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
