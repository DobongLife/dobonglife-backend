package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.port.dto.commerce.MyCouponInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/composition/coupon")
public class CouponCompositionController {

    private final CouponFacade couponFacade;

    @GetMapping("/my")
    public ResponseEntity<BaseResponse<MyCouponInfo>> getMyCoupon(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.COUPON) int size) {
        return ResponseEntity.ok(BaseResponse.ok(couponFacade.getMyCoupon(userId, lastId, size)));
    }

    @PostMapping("/use/{couponId}")
    public ResponseEntity<BaseResponse<Void>> useCoupon(
            @CurrentUserId Long userId,
            @PathVariable Long couponId,
            @RequestBody Map<String, Object> request) {
        couponFacade.useCoupon(
                Long.valueOf(request.get("promotionId").toString()),
                (String) request.get("code"),
                couponId, userId);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}
