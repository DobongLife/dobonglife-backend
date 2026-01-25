package com.umust.dobonglife.domain.coupon.controller.dto.response;

import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import com.umust.dobonglife.domain.place.domain.entity.Place;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PromotionItem(
        Long promotionId, Long placeId, String placeName, String operatingHour, // 추가
        String category, String title, String description, List<String> imgUrls,
        DiscountType discountType, BigDecimal discountValue,
        Long point, Long minPrice, Long maxPrice, LocalDate endDate
) {
    public static PromotionItem from(Promotion promotion) {
        Place place = promotion.getPlace();
        return new PromotionItem(
                promotion.getId(),
                place.getId(),
                place.getName(),
                place.getOperatingHour(),
                promotion.getCategory().getDescription(),
                promotion.getTitle(),
                promotion.getDescription(),
                promotion.getImgUrls(),
                promotion.getDiscountType(),
                promotion.getDiscountValue(),
                promotion.getPoint(),
                promotion.getMinPrice(),
                promotion.getMaxPrice(),
                promotion.getEndDate()
        );
    }
}