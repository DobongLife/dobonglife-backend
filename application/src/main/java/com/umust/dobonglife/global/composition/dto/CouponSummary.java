package com.umust.dobonglife.application.coupon.dto;

import com.umust.dobonglife.domain.coupon.application.dto.CouponDetail;
import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.domain.promotion.domain.entity.PromotionImage;
import com.umust.dobonglife.global.common.Identifiable;

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

    public static CouponSummary of(CouponDetail coupon, Promotion promotion) {
        List<String> imageUrls = promotion.getImages().stream()
                .map(PromotionImage::getImageUrl)
                .toList();

        return new CouponSummary(
                coupon.couponId(),
                coupon.promotionId(),
                promotion.getCategory().name(),
                promotion.getTitle(),
                promotion.getDescription(),
                imageUrls,
                promotion.getDiscountType().name(),
                promotion.getDiscountValue(),
                promotion.getMinPrice(),
                promotion.getMaxPrice(),
                coupon.issueEndDate(),
                coupon.couponStatus()
        );
    }

    @Override
    public Long getId() {
        return couponId;
    }
}
