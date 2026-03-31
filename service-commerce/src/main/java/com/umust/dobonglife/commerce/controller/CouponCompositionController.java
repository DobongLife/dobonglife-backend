package com.umust.dobonglife.commerce.controller;

import com.umust.dobonglife.commerce.facade.CouponFacade;
import com.umust.dobonglife.commerce.facade.CouponFacade.CouponUsedResponse;
import com.umust.dobonglife.commerce.facade.CouponFacade.MyCouponGetResponse;
import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/composition/coupon")
public class CouponCompositionController {

    private final CouponFacade couponFacade;

    @GetMapping("/my")
    public ResponseEntity<BaseResponse<MyCouponGetResponse>> getMyCoupon(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.COUPON) int size) {
        return ResponseEntity.ok(BaseResponse.ok(couponFacade.getMyCoupon(userId, lastId, size)));
    }

    @PostMapping("/use/{couponId}")
    public ResponseEntity<BaseResponse<CouponUsedResponse>> useCoupon(
            @CurrentUserId Long userId,
            @PathVariable Long couponId,
            @RequestBody CouponUseRequest request) {
        return ResponseEntity.ok(BaseResponse.ok(
                couponFacade.useCoupon(request.promotionId(), request.code(), couponId, userId)));
    }

    public record CouponUseRequest(Long promotionId, String code) {}
}
