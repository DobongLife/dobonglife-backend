package com.umust.dobonglife.domain.coupon.presentation;

import com.umust.dobonglife.domain.coupon.application.port.in.CouponCleanupUseCase;
import com.umust.dobonglife.domain.coupon.application.port.in.CouponRestoreUseCase;
import com.umust.dobonglife.domain.coupon.application.port.in.GetCouponUseCase;
import com.umust.dobonglife.domain.coupon.application.port.in.ManageCouponUseCase;
import com.umust.dobonglife.domain.coupon.application.dto.CouponDetail;
import com.umust.dobonglife.domain.coupon.application.dto.MyCouponStatus;
import com.umust.dobonglife.domain.promotion.application.port.in.GetPromotionUseCase;
import com.umust.dobonglife.domain.promotion.application.port.in.ManagePromotionUseCase;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.domain.promotion.domain.entity.PromotionImage;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import com.umust.dobonglife.global.port.dto.commerce.CouponSummaryInfo;
import com.umust.dobonglife.global.port.dto.commerce.MyCouponInfo;
import com.umust.dobonglife.global.port.dto.commerce.MyCouponStatusInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/coupon")
@RequiredArgsConstructor
public class InternalCouponController {

    private final GetCouponUseCase getCouponUseCase;
    private final ManageCouponUseCase manageCouponUseCase;
    private final GetPromotionUseCase getPromotionUseCase;
    private final ManagePromotionUseCase managePromotionUseCase;
    private final CouponCleanupUseCase couponCleanupUseCase;
    private final CouponRestoreUseCase couponRestoreUseCase;

    @GetMapping("/my")
    public MyCouponInfo getMyCoupons(
            @RequestParam Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam int size) {

        CursorResponse<CouponDetail> couponPage = getCouponUseCase.getMyCoupons(userId, lastId, size);

        List<Long> promotionIds = couponPage.getContent().stream()
                .map(CouponDetail::promotionId)
                .distinct()
                .toList();
        List<Promotion> promotions = getPromotionUseCase.getPromotionsByIds(promotionIds);
        Map<Long, Promotion> promotionMap = promotions.stream()
                .collect(Collectors.toMap(Promotion::getId, Function.identity()));

        CursorResponse<CouponSummaryInfo> coupons = CursorUtils.convert(
                couponPage, coupon -> {
                    Promotion p = promotionMap.get(coupon.promotionId());
                    return new CouponSummaryInfo(
                            coupon.couponId(),
                            coupon.promotionId(),
                            p != null ? p.getCategory().name() : null,
                            p != null ? p.getTitle() : null,
                            p != null ? p.getDescription() : null,
                            p != null ? p.getImages().stream().map(PromotionImage::getImageUrl).toList() : List.of(),
                            p != null ? p.getDiscountType().name() : null,
                            p != null ? p.getDiscountValue() : null,
                            p != null ? p.getMinPrice() : null,
                            p != null ? p.getMaxPrice() : null,
                            coupon.issueEndDate(),
                            coupon.couponStatus().name()
                    );
                });

        MyCouponStatus status = getCouponUseCase.getMyCouponStatus(userId);
        MyCouponStatusInfo statusInfo = new MyCouponStatusInfo(status.available(), status.used(), status.expired());

        return new MyCouponInfo(statusInfo, coupons);
    }

    @PostMapping("/withdraw/{userId}/cleanup")
    public void cleanupCoupons(@PathVariable Long userId) {
        couponCleanupUseCase.markPendingByUserId(userId);
    }

    @PostMapping("/withdraw/{userId}/restore")
    public void restoreCoupons(@PathVariable Long userId) {
        couponRestoreUseCase.restoreByUserId(userId);
    }

    @PostMapping("/withdraw/{userId}/finalize")
    public void finalizeCoupons(@PathVariable Long userId) {
        couponCleanupUseCase.finalizeByUserId(userId);
    }

    @PostMapping("/use/{couponId}")
    public void useCoupon(
            @PathVariable Long couponId,
            @RequestParam Long userId,
            @RequestParam Long promotionId,
            @RequestParam String code) {
        managePromotionUseCase.validateCode(promotionId, code);
        manageCouponUseCase.useCoupon(couponId, userId);
    }
}
