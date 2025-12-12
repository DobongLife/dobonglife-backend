package com.umust.dobonglife.domain.coupon.presentation;

import com.umust.dobonglife.domain.auth.model.UserPrincipal;
import com.umust.dobonglife.domain.coupon.presentation.dto.request.CouponCodeRequest;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.MyCouponResponse;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.PromotionResponse;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.UsedCouponResponse;
import com.umust.dobonglife.domain.coupon.service.CouponService;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
