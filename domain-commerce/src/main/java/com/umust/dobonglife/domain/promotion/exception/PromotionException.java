package com.umust.dobonglife.domain.promotion.exception;

import com.umust.dobonglife.global.common.error.exception.BusinessException;

public class PromotionException extends BusinessException {

    public PromotionException(PromotionErrorCode errorCode) {
        super(errorCode);
    }

    public PromotionException(PromotionErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
