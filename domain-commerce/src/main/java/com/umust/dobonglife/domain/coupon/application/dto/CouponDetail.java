package com.umust.dobonglife.domain.coupon.application.dto;

import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;
import com.umust.dobonglife.global.common.Identifiable;

import java.time.LocalDate;

public record CouponDetail(
        Long couponId,
        Long promotionId,
        CouponStatus couponStatus,
        LocalDate issueStartDate,
        LocalDate issueEndDate
) implements Identifiable {

    public static CouponDetail from(Coupon coupon) {
        return new CouponDetail(
                coupon.getId(),
                coupon.getPromotionId(),
                coupon.getCouponStatus(),
                coupon.getIssueStartDate(),
                coupon.getIssueEndDate()
        );
    }

    @Override
    public Long getId() {
        return couponId;
    }
}
