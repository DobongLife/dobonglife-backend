package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.global.composition.dto.response.MyCouponGetResponse;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
