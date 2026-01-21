package com.umust.dobonglife.domain.mypage.controller.dto.response;

import com.umust.dobonglife.domain.banners.service.dto.BannerSummaryResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionSummaryItem;
import com.umust.dobonglife.domain.point.controller.dto.response.PointResponse;
import com.umust.dobonglife.domain.user.controller.dto.response.MyPageResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.slice.SliceResponse;

public record MyPageSummaryResponse(MyPageResponse userInfo,
                                    SliceResponse<PointResponse> pointList) {
}
