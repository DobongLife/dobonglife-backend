package com.umust.dobonglife.domain.coupon.exception;

import com.umust.dobonglife.global.error.exception.BusinessException;

public class CouponException extends BusinessException {

    public CouponException(CouponErrorCode errorCode) {
        super(errorCode);
    }

    public CouponException(CouponErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
