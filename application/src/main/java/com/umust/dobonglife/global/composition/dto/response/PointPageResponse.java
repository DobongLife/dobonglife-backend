package com.umust.dobonglife.global.composition.dto.response;

import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.port.dto.commerce.PromotionAdInfo;

public record PointPageResponse(
        Long totalPoint,
        CursorResponse<PromotionAdInfo> promotions
) {}
