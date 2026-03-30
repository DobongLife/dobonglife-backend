package com.umust.dobonglife.presentation.business.dto.response;

import com.umust.dobonglife.domain.promotion.domain.constant.DiscountType;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.global.common.Identifiable;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class BusinessPromotionResponse implements Identifiable {

    private Long promotionId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean inPeriod;
    private DiscountType discountType;
    private Long discountValue;
    private int usedValue;
    private Long usedCount;
    private Long totalCount;
    private String code;
    private String description;
    private Integer validPeriod;

    @Override
    public Long getId() {
        return promotionId;
    }

    public static BusinessPromotionResponse of(Promotion promotion, Long usedCount) {
        LocalDate today = LocalDate.now();
        boolean inPeriod = !today.isBefore(promotion.getStartDate()) && !today.isAfter(promotion.getEndDate());
        int usedValue = promotion.getTotalQuantity() > 0
                ? (int) (usedCount * 100 / promotion.getTotalQuantity())
                : 0;

        return BusinessPromotionResponse.builder()
                .promotionId(promotion.getId())
                .title(promotion.getTitle())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .inPeriod(inPeriod)
                .discountType(promotion.getDiscountType())
                .discountValue(promotion.getDiscountValue())
                .usedValue(usedValue)
                .usedCount(usedCount)
                .totalCount(promotion.getTotalQuantity())
                .code(promotion.getCode())
                .description(promotion.getDescription())
                .validPeriod(promotion.getCouponValidDays())
                .build();
    }
}
