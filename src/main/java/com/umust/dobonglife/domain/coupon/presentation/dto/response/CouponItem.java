package com.umust.dobonglife.domain.coupon.presentation.dto.response;

import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CouponItem(Long couponId, Long promotionId, String category, String title,
                         String description, String img, DiscountType discountType, BigDecimal discountValue,
                         Long minPrice, Long maxPrice, LocalDate endDate, CouponStatus couponStatus) {
}
