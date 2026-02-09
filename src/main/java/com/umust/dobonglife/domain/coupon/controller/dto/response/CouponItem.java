package com.umust.dobonglife.domain.coupon.controller.dto.response;

import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.global.common.Identifiable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CouponItem(
        Long couponId,
        Long placeId,
        String placeName,
        String operatingHour,
        Long promotionId,
        String category,
        String title,
        String description,
        List<String> img,
        DiscountType discountType,
        BigDecimal discountValue,
        Long minPrice,
        Long maxPrice,
        LocalDate endDate,
        CouponStatus couponStatus
)implements Identifiable {
    public static CouponItem from(Coupon coupon) {
        return new CouponItem(
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
