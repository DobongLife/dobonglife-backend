package com.umust.dobonglife.global.composition.dto;

import com.umust.dobonglife.domain.coupon.application.dto.CouponDetail;
import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;
import com.umust.dobonglife.global.common.Identifiable;
import com.umust.dobonglife.global.port.dto.PromotionInfo;

import java.time.LocalDate;
import java.util.List;

public record CouponSummary(
        Long couponId,
        Long promotionId,
        String category,
        String title,
        String description,
        List<String> imageUrls,
        String discountType,
        Long discountValue,
        Long minPrice,
        Long maxPrice,
        LocalDate issueEndDate,
        CouponStatus couponStatus
) implements Identifiable {

    public static CouponSummary of(CouponDetail coupon, PromotionInfo promotion) {
        return new CouponSummary(
                coupon.couponId(),
                coupon.promotionId(),
                promotion.category(),
                promotion.title(),
                promotion.description(),
                promotion.imageUrls(),
                promotion.discountType(),
                promotion.discountValue(),
                promotion.minPrice(),
                promotion.maxPrice(),
                coupon.issueEndDate(),
                coupon.couponStatus()
        );
    }

    @Override
    public Long getId() {
        return couponId;
    }
}
