package com.umust.dobonglife.domain.coupon.presentation.dto.response;

import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CouponItem(
        Long couponId,
        Long promotionId,
        String category,
        String title,
        String description,
        String img,
        DiscountType discountType,
        BigDecimal discountValue,
        Long minPrice,
        Long maxPrice,
        LocalDate endDate,
        CouponStatus couponStatus
) {
    public static CouponItem from(Coupon coupon) {
        return new CouponItem(
                coupon.getId(),
                coupon.getPromotion().getId(),
                coupon.getPromotion().getCategory(),
                coupon.getPromotion().getTitle(),
                coupon.getPromotion().getDescription(),
                coupon.getPromotion().getImg(),
                coupon.getPromotion().getDiscountType(),
                coupon.getPromotion().getDiscountValue(),
                coupon.getPromotion().getMinPrice(),
                coupon.getPromotion().getMaxPrice(),
                coupon.getIssueEndDate(),
                coupon.getCouponStatus()
        );
    }
}
