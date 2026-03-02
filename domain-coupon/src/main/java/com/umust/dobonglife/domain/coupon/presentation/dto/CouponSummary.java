package com.umust.dobonglife.domain.coupon.presentation.dto;

import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;
import com.umust.dobonglife.global.common.Identifiable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CouponSummary(Long couponId,
                            Long placeId,
                            String placeName,
                            String operatingHour,
                            Long promotionId,
                            String category,
                            String title,
                            String description,
                            List<String> imgUrls,
                            String discountType,
                            Long discountValue,
                            Long minPrice,
                            Long maxPrice,
                            LocalDate endDate,
                            CouponStatus couponStatus
)implements Identifiable {
    public static CouponSummary from(CouponInfo coupon, PromotionInfo promotion) {
        return new CouponSummary(
                coupon.getId(),
                coupon.getPromotion().getPlace().getId(),
                coupon.getPromotion().getPlace().getName(),
                coupon.getPromotion().getPlace().getOperatingHour(),
                coupon.getPromotion().getId(),
                coupon.getPromotion().getCategory().getDescription(),
                coupon.getPromotion().getTitle(),
                coupon.getPromotion().getDescription(),
                coupon.getPromotion().getImgUrls(),
                coupon.getPromotion().getDiscountType(),
                coupon.getPromotion().getDiscountValue(),
                coupon.getPromotion().getMinPrice(),
                coupon.getPromotion().getMaxPrice(),
                coupon.getIssueEndDate(),
                coupon.getCouponStatus()
        );
    }

    @Override
    public Long getId() {
        return couponId;
    }
}