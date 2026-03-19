package com.umust.dobonglife.global.port.dto.commerce;

import java.time.LocalDate;
import java.util.List;

public record PromotionRegisterInfo(
        Long promotionId,
        String couponName,
        String code,
        Long point,
        String discountType,
        Long discountValue,
        LocalDate startDate,
        LocalDate endDate,
        List<String> imageUrls
) {}
