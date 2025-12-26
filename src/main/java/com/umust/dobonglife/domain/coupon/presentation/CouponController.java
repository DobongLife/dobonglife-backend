package com.umust.dobonglife.domain.coupon.presentation;

import com.umust.dobonglife.domain.auth.domain.entity.UserPrincipal;
import com.umust.dobonglife.domain.coupon.presentation.dto.request.CouponCodeRequest;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.MyCouponResponse;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.UsedCouponResponse;
import com.umust.dobonglife.domain.coupon.service.CouponService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;
    @GetMapping("/my")
    public BaseResponse<MyCouponResponse> getMyCoupon(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                      @RequestParam(required = false) Long lastId,
                                                      @RequestParam(defaultValue = "2") int size){
        Long userId = userPrincipal.getUserId();
        MyCouponResponse response = couponService.getMyCoupon(userId, lastId, size);
        return BaseResponse.ok(response);
    }

    @PostMapping("/my")
    public BaseResponse<UsedCouponResponse> useMyCoupon(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                        CouponCodeRequest request){
        Long userId = userPrincipal.getUserId();
        UsedCouponResponse response = couponService.useMyCoupon(userId, request);
        return BaseResponse.ok(response);
    }

}
