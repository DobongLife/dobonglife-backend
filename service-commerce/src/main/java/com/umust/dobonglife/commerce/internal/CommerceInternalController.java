package com.umust.dobonglife.commerce.internal;

import com.umust.dobonglife.domain.coupon.application.port.in.CouponCleanupUseCase;
import com.umust.dobonglife.domain.coupon.application.port.in.CouponRestoreUseCase;
import com.umust.dobonglife.domain.point.application.port.in.PointCleanupUseCase;
import com.umust.dobonglife.domain.point.application.port.in.PointRestoreUseCase;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/withdraw")
public class CommerceInternalController {

    private final CouponCleanupUseCase couponCleanupUseCase;
    private final CouponRestoreUseCase couponRestoreUseCase;
    private final PointCleanupUseCase pointCleanupUseCase;
    private final PointRestoreUseCase pointRestoreUseCase;

    @PostMapping("/coupons/{userId}/cleanup")
    public BaseResponse<Void> cleanupCoupons(@PathVariable Long userId) {
        couponCleanupUseCase.markPendingByUserId(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/points/{userId}/cleanup")
    public BaseResponse<Void> cleanupPoints(@PathVariable Long userId) {
        pointCleanupUseCase.markPendingByUserId(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/coupons/{userId}/restore")
    public BaseResponse<Void> restoreCoupons(@PathVariable Long userId) {
        couponRestoreUseCase.restoreByUserId(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/points/{userId}/restore")
    public BaseResponse<Void> restorePoints(@PathVariable Long userId) {
        pointRestoreUseCase.restoreByUserId(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/coupons/{userId}/finalize")
    public BaseResponse<Void> finalizeCoupons(@PathVariable Long userId) {
        couponCleanupUseCase.finalizeByUserId(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/points/{userId}/finalize")
    public BaseResponse<Void> finalizePoints(@PathVariable Long userId) {
        pointCleanupUseCase.finalizeByUserId(userId);
        return BaseResponse.ok(null);
    }
}
