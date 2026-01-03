package com.umust.dobonglife.domain.coupon.controller;

import com.umust.dobonglife.domain.coupon.controller.dto.request.CouponCodeRequest;
import com.umust.dobonglife.domain.coupon.controller.dto.response.MyCouponResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.UsedCouponResponse;
import com.umust.dobonglife.domain.coupon.service.CouponService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;
    @GetMapping("/my")
    public BaseResponse<MyCouponResponse> getMyCoupon(@CurrentUserId Long userId,
                                                      @RequestParam(required = false) Long lastId,
                                                      @RequestParam(defaultValue = "2") int size){
        MyCouponResponse response = couponService.getMyCoupon(userId, lastId, size);
        return BaseResponse.ok(response);
    }

    @PostMapping("/my")
    public BaseResponse<UsedCouponResponse> useMyCoupon(@CurrentUserId Long userId,
                                                        CouponCodeRequest request){
        UsedCouponResponse response = couponService.useMyCoupon(userId, request);
        return BaseResponse.ok(response);
    }

}
