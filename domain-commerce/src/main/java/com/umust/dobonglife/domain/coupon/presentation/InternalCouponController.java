package com.umust.dobonglife.domain.coupon.presentation;

import com.umust.dobonglife.domain.coupon.application.CouponService;
import com.umust.dobonglife.domain.coupon.application.dto.CouponDetail;
import com.umust.dobonglife.domain.coupon.application.dto.MyCouponStatus;
import com.umust.dobonglife.domain.promotion.application.PromotionService;
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

    private final CouponService couponService;
    private final PromotionService promotionService;

    @GetMapping("/my")
    public MyCouponInfo getMyCoupons(
            @RequestParam Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam int size) {

        CursorResponse<CouponDetail> couponPage = couponService.getMyCoupons(userId, lastId, size);

        List<Long> promotionIds = couponPage.getContent().stream()
                .map(CouponDetail::promotionId)
                .distinct()
                .toList();
        List<Promotion> promotions = promotionService.getPromotionsByIds(promotionIds);
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

        MyCouponStatus status = couponService.getMyCouponStatus(userId);
        MyCouponStatusInfo statusInfo = new MyCouponStatusInfo(status.available(), status.used(), status.expired());

        return new MyCouponInfo(statusInfo, coupons);
    }

    @PostMapping("/use/{couponId}")
    public void useCoupon(
            @PathVariable Long couponId,
            @RequestParam Long userId,
            @RequestParam Long promotionId,
            @RequestParam String code) {
        promotionService.validateCode(promotionId, code);
        couponService.useCoupon(couponId, userId);
    }
}
