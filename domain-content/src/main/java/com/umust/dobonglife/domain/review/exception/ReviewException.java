package com.umust.dobonglife.domain.review.exception;

import com.umust.dobonglife.global.common.error.exception.BusinessException;

public class ReviewException extends BusinessException {

    public ReviewException(ReviewErrorCode errorCode) {
        super(errorCode);
    }

    public ReviewException(ReviewErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
