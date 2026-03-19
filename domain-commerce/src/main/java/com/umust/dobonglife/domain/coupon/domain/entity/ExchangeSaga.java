package com.umust.dobonglife.domain.coupon.domain.entity;

import com.umust.dobonglife.domain.coupon.domain.vo.SagaStatus;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "exchange_sagas")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeSaga extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long promotionId;

    private Long couponId;

    private Long pointAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "saga_status", length = 30)
    private SagaStatus sagaStatus;

    @Column(length = 500)
    private String failReason;

    public static ExchangeSaga create(Long userId, Long promotionId) {
        ExchangeSaga saga = new ExchangeSaga();
        saga.userId = userId;
        saga.promotionId = promotionId;
        saga.sagaStatus = SagaStatus.STARTED;
        return saga;
    }

    public void setPointAmount(Long pointAmount) {
        this.pointAmount = pointAmount;
    }

    public void markUserValidated() {
        this.sagaStatus = SagaStatus.USER_VALIDATED;
    }

    public void markPointDeducted() {
        this.sagaStatus = SagaStatus.POINT_DEDUCTED;
    }

    public void markStockDeducted() {
        this.sagaStatus = SagaStatus.STOCK_DEDUCTED;
    }

    public void markCouponIssued(Long couponId) {
        this.couponId = couponId;
        this.sagaStatus = SagaStatus.COUPON_ISSUED;
    }

    public void complete() {
        this.sagaStatus = SagaStatus.COMPLETED;
    }

    public void markStockRestoring() {
        this.sagaStatus = SagaStatus.STOCK_RESTORING;
    }

    public void markStockRestored() {
        this.sagaStatus = SagaStatus.STOCK_RESTORED;
    }

    public void markPointRefunding() {
        this.sagaStatus = SagaStatus.POINT_REFUNDING;
    }

    public void markPointRefunded() {
        this.sagaStatus = SagaStatus.POINT_REFUNDED;
    }

    public void fail(String reason) {
        this.sagaStatus = SagaStatus.FAILED;
        this.failReason = reason;
    }
}
