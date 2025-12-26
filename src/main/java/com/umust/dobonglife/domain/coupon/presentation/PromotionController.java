package com.umust.dobonglife.domain.coupon.presentation;

import com.umust.dobonglife.domain.auth.domain.entity.UserPrincipal;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.PromotionResponse;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.UsedCouponResponse;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/promotion")
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping
    public BaseResponse<PromotionResponse> getPromotion(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                        @RequestParam(required = false) Long lastId,
                                                        @RequestParam(defaultValue = "2") int size){
        Long userId = userPrincipal.getUserId();
        PromotionResponse response = promotionService.getPromotion(userId, lastId, size);
        return BaseResponse.ok(response);
    }

    @PostMapping("/{promotionId}")
    public BaseResponse<UsedCouponResponse> changePointToCoupon(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                @PathVariable(name = "promotionId") Long promotionId){
        Long userId = userPrincipal.getUserId();
        UsedCouponResponse response = promotionService.changePointToCoupon(userId, promotionId);
        return BaseResponse.ok(response);
    }
}
