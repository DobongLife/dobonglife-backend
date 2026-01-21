package com.umust.dobonglife.domain.point.controller.dto.response;

import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionItem;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.slice.SliceResponse;

public record MyPointsResponse(Long totalPoint,
                              SliceResponse<PointResponse> pointList) {
}
