package com.umust.dobonglife.global.composition.dto.response;

import com.umust.dobonglife.domain.promotion.application.dto.PromotionAdSummary;
import com.umust.dobonglife.global.common.response.CursorResponse;

public record PointPageResponse(
        Long totalPoint,
        CursorResponse<PromotionAdSummary> promotions
) {}
