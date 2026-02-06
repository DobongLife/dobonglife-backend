package com.umust.dobonglife.domain.point.controller.dto.response;

import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionBannerItem;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionItem;
import com.umust.dobonglife.global.common.response.CursorResponse;

import java.util.List;

public record PointPageResponse(Long totalPoint,
                                CursorResponse<PromotionBannerItem> promotionList) {
}
