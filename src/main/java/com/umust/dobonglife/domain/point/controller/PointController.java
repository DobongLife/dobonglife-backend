package com.umust.dobonglife.domain.point.controller;

import com.umust.dobonglife.domain.coupon.controller.dto.response.MyCouponResponse;
import com.umust.dobonglife.domain.point.controller.dto.response.PointPageResponse;
import com.umust.dobonglife.domain.point.service.PointPromotionService;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/point")
public class PointController {
    private final PointPromotionService pointService;
    @GetMapping
    public BaseResponse<PointPageResponse> getMyPoint(@CurrentUserId Long userId){
        PointPageResponse response = pointService.getMyPoint(userId);
        return BaseResponse.ok(response);
    }
}
