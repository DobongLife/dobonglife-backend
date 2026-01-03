package com.umust.dobonglife.domain.home.service;

import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionItem;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionResponse;
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

    @Transactional(readOnly = true)
    public HomeSummaryResponse getHomeSummary(Long lastId, int size) {
        CursorResponse<CourseSummaryResponse> courses = courseService.getCourses(lastId, size);
        CursorResponse<PromotionItem> promotions = promotionService.getPromotion(lastId, size);
        return new HomeSummaryResponse(courses, promotions);
    }
}
