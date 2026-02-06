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
    private int usedValue;
    private Long usedCount;
    private Long issuedCount;
    private String code;
    private String description;

    public static BusinessPromotionResponse of(
            Long promotionId,
            String title,
            LocalDate startDate,
            LocalDate endDate,
            boolean canUsed,
            DiscountType discountType,
            BigDecimal discountValue,
            int usedValue,
            Long usedCount,
            Long issuedCount,
            String code,
            String description
    ) {
        return BusinessPromotionResponse.builder()
                .promotionId(promotionId)
                .title(title)
                .startDate(startDate)
                .endDate(endDate)
                .canUsed(canUsed)
                .discountType(discountType)
                .discountValue(discountValue)
                .usedValue(usedValue)
                .usedCount(usedCount)
                .issuedCount(issuedCount)
                .code(code)
                .description(description)
                .build();
    }
}
