package com.umust.dobonglife.domain.home.service;

import com.umust.dobonglife.domain.banners.service.BannerService;
import com.umust.dobonglife.domain.banners.service.dto.BannerSummaryResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionItem;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionSummaryItem;
import com.umust.dobonglife.domain.coupon.service.CouponService;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.domain.home.controller.dto.response.HomeSummaryResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final CourseService courseService;
    private final PromotionService promotionService;
    private final BannerService bannerService;

    @Transactional(readOnly = true)
    public HomeSummaryResponse getHomeSummary(Long lastId, int size) {
        CursorResponse<PromotionSummaryItem> promotions = promotionService.getPromotionSummary(lastId, size);
        CursorResponse<BannerSummaryResponse> banners = bannerService.getBanners();
        return new HomeSummaryResponse(banners, promotions);
    }
}
