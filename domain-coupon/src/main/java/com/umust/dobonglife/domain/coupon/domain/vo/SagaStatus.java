package com.umust.dobonglife.domain.coupon.domain.vo;

public enum SagaStatus {

    STARTED,
    USER_VALIDATED,
    POINT_DEDUCTED,
    STOCK_DEDUCTED,
    COUPON_ISSUED,
    COMPLETED,

    // 보상 상태
    STOCK_DEDUCT_FAILED,
    COUPON_ISSUE_FAILED,
    STOCK_RESTORING,
    STOCK_RESTORED,
    POINT_REFUNDING,
    POINT_REFUNDED,

    FAILED
}
