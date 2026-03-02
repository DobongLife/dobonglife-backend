package com.umust.dobonglife.domain.promotion.presentation.dto.response;

import com.umust.dobonglife.domain.promotion.application.dto.PromotionSummary;
import com.umust.dobonglife.global.common.response.CursorResponse;

public record PromotionWithBlockedResponse(boolean isBlockedUser,
                                           CursorResponse<PromotionSummary> promotions) {
    public static PromotionWithBlockedResponse of(boolean isBlockedUser, CursorResponse<PromotionSummary> promotions){
        return new PromotionWithBlockedResponse(isBlockedUser, promotions);
    }
}
