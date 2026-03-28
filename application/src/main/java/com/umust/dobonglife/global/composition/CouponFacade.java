package com.umust.dobonglife.application.coupon;

import com.umust.dobonglife.application.coupon.dto.CouponSummary;
import com.umust.dobonglife.application.coupon.dto.CouponUsedResponse;
import com.umust.dobonglife.application.coupon.dto.MyCouponGetResponse;
import com.umust.dobonglife.domain.coupon.application.CouponService;
import com.umust.dobonglife.domain.coupon.application.dto.CouponDetail;
import com.umust.dobonglife.domain.coupon.application.dto.MyCouponStatus;
import com.umust.dobonglife.domain.promotion.application.PromotionService;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;
    private final PromotionService promotionService;

    public MyCouponGetResponse getMyCoupon(Long userId, Long lastId, int size) {
        CursorResponse<CouponDetail> couponPage = couponService.getMyCoupons(userId, lastId, size);

        List<Long> promotionIds = couponPage.getContent().stream()
                .map(CouponDetail::promotionId)
                .distinct()
                .toList();
        List<Promotion> promotions = promotionService.getPromotionsByIds(promotionIds);
        Map<Long, Promotion> promotionMap = promotions.stream()
                .collect(Collectors.toMap(Promotion::getId, Function.identity()));

        CursorResponse<CouponSummary> myCouponList = CursorUtils.convert(
                couponPage, coupon -> CouponSummary.of(coupon, promotionMap.get(coupon.promotionId())));

        MyCouponStatus status = couponService.getMyCouponStatus(userId);

        return new MyCouponGetResponse(status, myCouponList);
    }

    public CouponUsedResponse useCoupon(Long promotionId, String code, Long couponId, Long userId) {
        promotionService.validateCode(promotionId, code);
        couponService.useCoupon(couponId, userId);
        return CouponUsedResponse.of(couponId);
    }
}
