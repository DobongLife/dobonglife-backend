package com.umust.dobonglife.domain.business.controller.dto.response;

import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class BusinessPromotionResponse {

    private Long promotionId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean inPeriod;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private int usedValue;
    private Long usedCount;
    private Long totalCount;
    private String code;
    private String description;
    private Long validPeriod;

    public static BusinessPromotionResponse of(
            Long promotionId,
            String title,
            LocalDate startDate,
            LocalDate endDate,
            boolean inPeriod,
            DiscountType discountType,
            BigDecimal discountValue,
            int usedValue,
            Long usedCount,
            Long totalCount,
            String code,
            String description,
            Long validPeriod
    ) {
        return BusinessPromotionResponse.builder()
                .promotionId(promotionId)
                .title(title)
                .startDate(startDate)
                .endDate(endDate)
                .inPeriod(inPeriod)
                .discountType(discountType)
                .discountValue(discountValue)
                .usedValue(usedValue)
                .usedCount(usedCount)
                .totalCount(totalCount)
                .code(code)
                .description(description)
                .validPeriod(validPeriod)
                .build();
    }
}
