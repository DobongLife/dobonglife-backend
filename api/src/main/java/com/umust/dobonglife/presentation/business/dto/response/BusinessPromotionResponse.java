package com.umust.dobonglife.presentation.business.dto.response;

import com.umust.dobonglife.application.business.service.BusinessFacade;
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
    private String discountType;
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

    public static BusinessPromotionResponse of(BusinessFacade.PromotionInfo promotion, Long usedCount) {
        LocalDate today = LocalDate.now();
        boolean inPeriod = !today.isBefore(promotion.startDate()) && !today.isAfter(promotion.endDate());
        int usedValue = promotion.totalQuantity() > 0
                ? (int) (usedCount * 100 / promotion.totalQuantity())
                : 0;

        return BusinessPromotionResponse.builder()
                .promotionId(promotion.promotionId())
                .title(promotion.title())
                .startDate(promotion.startDate())
                .endDate(promotion.endDate())
                .inPeriod(inPeriod)
                .discountType(promotion.discountType())
                .discountValue(promotion.discountValue())
                .usedValue(usedValue)
                .usedCount(usedCount)
                .totalCount(promotion.totalQuantity())
                .code(promotion.code())
                .description(promotion.description())
                .validPeriod(promotion.couponValidDays())
                .build();
    }
}
