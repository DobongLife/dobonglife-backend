package com.umust.dobonglife.global.composition.dto.response;

import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionItem;
import com.umust.dobonglife.global.common.response.CursorResponse;

public record PromotionWithBlockedResponse(boolean isBlockedUser,
                                           CursorResponse<PromotionItem> promotions) {
    public static PromotionWithBlockedResponse of(boolean isBlockedUser, CursorResponse<PromotionItem> promotions){
        return new PromotionWithBlockedResponse(isBlockedUser, promotions);
    }
}
