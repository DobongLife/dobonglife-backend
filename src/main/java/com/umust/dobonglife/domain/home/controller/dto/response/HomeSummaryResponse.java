package com.umust.dobonglife.domain.home.controller.dto.response;

import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionItem;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;

public record HomeSummaryResponse(CursorResponse<CourseSummaryResponse> courses,
                                  CursorResponse<PromotionItem> promotions) {
}
