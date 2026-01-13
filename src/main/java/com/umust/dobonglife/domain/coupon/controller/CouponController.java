package com.umust.dobonglife.domain.coupon.controller;

import com.umust.dobonglife.domain.coupon.controller.dto.request.CouponCodeRequest;
import com.umust.dobonglife.domain.coupon.controller.dto.response.MyCouponResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.UsedCouponResponse;
import com.umust.dobonglife.domain.coupon.service.CouponService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "쿠폰 API", description = "쿠폰 관련 API")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon")
public class CouponController {

    private final CouponService couponService;
    @Operation(summary = "내 쿠폰 조회", description = "내 쿠폰을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping("/my")
    public BaseResponse<MyCouponResponse> getMyCoupon(@CurrentUserId Long userId,
                                                      @RequestParam(required = false) Long lastId,
                                                      @RequestParam(defaultValue = "2") int size){
        MyCouponResponse response = couponService.getMyCoupon(userId, lastId, size);
        return BaseResponse.ok(response);
    }

    @Operation(summary = "쿠폰 사용", description = "쿠폰을 사용합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @PostMapping("/my")
    public BaseResponse<UsedCouponResponse> useMyCoupon(@CurrentUserId Long userId,
                                                        @RequestBody CouponCodeRequest request){
        UsedCouponResponse response = couponService.useMyCoupon(userId, request);
        return BaseResponse.ok(response);
    }

}
