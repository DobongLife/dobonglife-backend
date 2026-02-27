package com.umust.dobonglife.domain.promotion.presentation.dto.response;

import com.umust.dobonglife.global.common.response.CursorResponse;

public record PromotionGetResponse(
        CursorResponse<PromotionItem> promotions
) {
}
