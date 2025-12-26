package com.umust.dobonglife.domain.coupon.controller.dto.response;

import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PromotionItem(Long promotionId, String category, String title,
                            String description, String img, DiscountType discountType, BigDecimal discountValue,
                            Long point, LocalDate endDate) {
}
