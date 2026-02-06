package com.umust.dobonglife.domain.business.controller.dto.response;

import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class BusinessPromotionResponse {

    private Long promotionId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean canUsed;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal usedValue;
    private Long usedCount;
    private Long issuedCount;
    private String code;
    private String description;
}
