package com.umust.dobonglife.domain.home.controller.dto.response;

import com.umust.dobonglife.domain.banners.service.dto.BannerSummaryResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionItem;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionSummaryItem;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;

public record HomeSummaryResponse(CursorResponse<BannerSummaryResponse> banners,
                                  CursorResponse<PromotionSummaryItem> promotions) {
}
