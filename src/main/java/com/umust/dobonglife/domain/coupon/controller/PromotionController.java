package com.umust.dobonglife.domain.coupon.controller;

import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.UsedCouponResponse;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/promotion")
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping
    public BaseResponse<PromotionResponse> getPromotion(@CurrentUserId Long userId,
                                                        @RequestParam(required = false) Long lastId,
                                                        @RequestParam(defaultValue = "2") int size){
        PromotionResponse response = promotionService.getPromotion(userId, lastId, size);
        return BaseResponse.ok(response);
    }

    @PostMapping("/{promotionId}")
    public BaseResponse<UsedCouponResponse> changePointToCoupon(@CurrentUserId Long userId,
                                                                @PathVariable(name = "promotionId") Long promotionId){
        UsedCouponResponse response = promotionService.changePointToCoupon(userId, promotionId);
        return BaseResponse.ok(response);
    }
}
