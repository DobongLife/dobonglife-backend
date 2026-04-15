package com.umust.dobonglife.global.common.error.exception;

import com.umust.dobonglife.global.common.error.CommonErrorCode;
import lombok.Getter;

@Getter
public class ServiceCallException extends BusinessException {

    private final String serviceName;
    private final int statusCode;

    public ServiceCallException(String serviceName, int statusCode, Throwable cause) {
        super(CommonErrorCode.SERVICE_UNAVAILABLE, cause);
        this.serviceName = serviceName;
        this.statusCode = statusCode;
    }

    public ServiceCallException(String serviceName, Throwable cause) {
        super(CommonErrorCode.SERVICE_UNAVAILABLE, cause);
        this.serviceName = serviceName;
        this.statusCode = 0;
    }
}
