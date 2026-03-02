package com.umust.dobonglife.global.port.dto;

import java.time.LocalDate;

public record PromotionInfo(
        Long promotionId,
        String title,
        Long point,
        Integer couponValidDays,
        LocalDate startDate,
        LocalDate endDate
) {
}
