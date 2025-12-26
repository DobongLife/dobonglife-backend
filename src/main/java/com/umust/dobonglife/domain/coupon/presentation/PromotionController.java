package com.umust.dobonglife.domain.coupon.presentation;

import com.umust.dobonglife.domain.coupon.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/promotion")
public class PromotionController {
    private final PromotionService promotionService;

//    @GetMapping
//    public BaseResponse<PromotionResponse> getPromotion(){
//        PromotionResponse response = promotionService.getPromotion();
//        return BaseResponse.ok(response);
//    }

//    @PostMapping
//    public BaseResponse<> changePointToCoupon(request){
//        UsedCouponResponse response = couponService.useMyCoupon(request);
//        return BaseResponse.ok(response);
//    }
}
