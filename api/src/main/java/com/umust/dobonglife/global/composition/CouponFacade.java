package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.domain.coupon.application.CouponService;
import com.umust.dobonglife.domain.coupon.application.dto.CouponDetail;
import com.umust.dobonglife.domain.coupon.application.dto.MyCouponStatus;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import com.umust.dobonglife.global.composition.dto.CouponSummary;
import com.umust.dobonglife.global.composition.dto.request.CouponUseRequest;
import com.umust.dobonglife.global.composition.dto.response.CouponUsedResponse;
import com.umust.dobonglife.global.composition.dto.response.MyCouponGetResponse;
import com.umust.dobonglife.global.port.PromotionPort;
import com.umust.dobonglife.global.port.dto.PromotionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;
    private final PromotionPort promotionPort;

    public MyCouponGetResponse getMyCoupon(Long userId, Long lastId, int size) {
        CursorResponse<CouponDetail> couponPage = couponService.getMyCoupons(userId, lastId, size);

        List<Long> promotionIds = couponPage.getContent().stream()
                .map(CouponDetail::promotionId)
                .distinct()
                .toList();
        Map<Long, PromotionInfo> promotionMap = promotionPort.getPromotionsByIds(promotionIds);

        // 데이터 조합
        CursorResponse<CouponSummary> myCouponList = CursorUtils.convert(
                couponPage, coupon -> CouponSummary.of(coupon, promotionMap.get(coupon.promotionId())));

        // 쿠폰 상태별 개수
        MyCouponStatus status = couponService.getMyCouponStatus(userId);

        return new MyCouponGetResponse(status, myCouponList);
    }

    public CouponUsedResponse useCoupon(CouponUseRequest request, Long couponId, Long userId) {
        Long promotionId = request.promotionId();
        String code = request.code();

        promotionPort.validateCode(promotionId, code);
        couponService.useCoupon(couponId, userId);

        return CouponUsedResponse.of(couponId);
    }
}
