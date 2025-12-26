package com.umust.dobonglife.domain.notification.exception;

import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.ErrorCode;

public class NotificationException extends BusinessException {
    public NotificationException(ErrorCode code) {
        super(code);
    }
}
