package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.global.composition.dto.request.CouponUseRequest;
import com.umust.dobonglife.global.composition.dto.response.CouponUsedResponse;
import com.umust.dobonglife.global.composition.dto.response.MyCouponGetResponse;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
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
    public ResponseEntity<BaseResponse<MyCouponGetResponse>> getMyCoupon(@CurrentUserId Long userId,
                                                                         @RequestParam(required = false) Long lastId,
                                                                         @RequestParam(defaultValue = PageSizeType.COUPON) int size) {
        return ResponseEntity.ok(BaseResponse.ok(couponFacade.getMyCoupon(userId, lastId, size)));
    }

    @PostMapping("/use/{couponId}")
    public ResponseEntity<BaseResponse<CouponUsedResponse>> useCoupon(@CurrentUserId Long userId,
                                                                      @PathVariable Long couponId,
                                                                      @RequestBody CouponUseRequest request){
        return ResponseEntity.ok(BaseResponse.ok(couponFacade.useCoupon(request, couponId, userId)));

    }
}
