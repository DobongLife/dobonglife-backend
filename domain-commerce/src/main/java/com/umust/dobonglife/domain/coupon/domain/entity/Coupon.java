package com.umust.dobonglife.domain.coupon.domain.entity;

import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;
import com.umust.dobonglife.domain.coupon.exception.CouponErrorCode;
import com.umust.dobonglife.domain.coupon.exception.CouponException;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long promotionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_status", length = 20)
    private CouponStatus couponStatus;

    private LocalDate issueStartDate;

    private LocalDate issueEndDate;

    @Builder
    public Coupon(Long userId, Long promotionId, CouponStatus couponStatus, LocalDate issueStartDate, LocalDate issueEndDate) {
        this.userId = userId;
        this.promotionId = promotionId;
        this.couponStatus = couponStatus;
        this.issueStartDate = issueStartDate;
        this.issueEndDate = issueEndDate;
    }

    public void used() {
        if (couponStatus != CouponStatus.AVAILABLE) {
            throw new CouponException(CouponErrorCode.COUPON_CANNOT_USE);
        }
        couponStatus = CouponStatus.USED;
    }
}
