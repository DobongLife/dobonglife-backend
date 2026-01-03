package com.umust.dobonglife.domain.notification.exception;

import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.error.ErrorCode;

public class NotificationException extends BusinessException {
    public NotificationException(ErrorCode code) {
        super(code);
    }
}
