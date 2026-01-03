package com.umust.dobonglife.domain.coupon.service;

import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import com.umust.dobonglife.domain.coupon.domain.repository.CouponRepository;
import com.umust.dobonglife.domain.coupon.domain.repository.PromotionRepository;
import com.umust.dobonglife.domain.coupon.controller.dto.request.CouponCodeRequest;
import com.umust.dobonglife.domain.coupon.controller.dto.response.CouponItem;
import com.umust.dobonglife.domain.coupon.controller.dto.response.MyCouponResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.MyCouponStatus;
import com.umust.dobonglife.domain.coupon.controller.dto.response.UsedCouponResponse;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final PromotionRepository promotionRepository;

    public MyCouponResponse getMyCoupon(Long userId, Long lastId, int size) {
        MyCouponStatus myCouponStatus = getMyCouponStatus(userId);
        CursorResponse<CouponItem> myCouponItemList = getCouponItemList(userId, lastId, size);

        return new MyCouponResponse(myCouponStatus, myCouponItemList);
    }

    private CursorResponse<CouponItem> getCouponItemList(Long userId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        Slice<Coupon> coupons = couponRepository.findCouponsNoOffset(userId, lastId, pageable);

        return convertToCursorResponse(coupons);
    }

    private MyCouponStatus getMyCouponStatus(Long userId) {
        int available = couponRepository.countByUserIdAndStatus(userId, CouponStatus.AVAILABLE);
        int used = couponRepository.countByUserIdAndStatus(userId, CouponStatus.USED);
        int expired = couponRepository.countByUserIdAndStatus(userId, CouponStatus.EXPIRED);

        return new MyCouponStatus(available, used, expired);
    }

    @Transactional
    public UsedCouponResponse useMyCoupon(Long userId, CouponCodeRequest request) {
        if(!validateCode(request.promotionId(), request.code()))
            throw new BusinessException(ErrorCode.INVALID_CODE);

        Coupon coupon = couponRepository.findByUserIdAndCouponId(userId, request.couponId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_COUPON_ID));
        coupon.updateCouponStatus();
        couponRepository.save(coupon);

        return new UsedCouponResponse(request.couponId(), CouponStatus.USED);
    }

    private boolean validateCode(Long promotionId, String code) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_PROMOTION_ID));
        return promotion.getCode().equals(code);
    }

    private CursorResponse<CouponItem> convertToCursorResponse(Slice<Coupon> coupons) {
        List<CouponItem> content = coupons.getContent().stream()
                .map(CouponItem::from)
                .toList();

        return new CursorResponse<>(content, coupons.hasNext());
    }

    public int getOwnedCouponCount(Long userId) {
        return couponRepository.countByUserIdAndStatus(userId, CouponStatus.AVAILABLE);
    }
}
