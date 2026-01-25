package com.umust.dobonglife.domain.coupon.controller.dto.response;

import com.umust.dobonglife.global.common.response.CursorResponse;

public record PromotionGetResponse(boolean isBlockedUser,
                                   CursorResponse<PromotionItem> promotions) {
}
