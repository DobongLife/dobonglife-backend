package com.umust.dobonglife.domain.coupon.service;

import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import com.umust.dobonglife.domain.coupon.domain.repository.CouponRepository;
import com.umust.dobonglife.domain.coupon.domain.repository.PromotionRepository;
import com.umust.dobonglife.domain.coupon.presentation.dto.request.CouponCodeRequest;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.CouponItem;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.MyCouponResponse;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.MyCouponStatus;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.UsedCouponResponse;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final PromotionRepository promotionRepository;

    public MyCouponResponse getMyCoupon(Long userId) {
        MyCouponStatus myCouponStatus = getMyCouponStatus(userId);
        List<CouponItem> myCouponItemList = getCouponItemList(userId);

        return new MyCouponResponse(myCouponStatus, myCouponItemList);
    }

    private List<CouponItem> getCouponItemList(Long userId) {
        return couponRepository.getCouponItemList(userId);
    }

    private MyCouponStatus getMyCouponStatus(Long userId) {
        return couponRepository.getMyCouponStatus(userId);
    }

    @Transactional
    public UsedCouponResponse useMyCoupon(CouponCodeRequest request) {
        if(!validateCode(request.promotionId(), request.code()))
            throw new BusinessException(ErrorCode.INVALID_CODE);

        Coupon coupon = couponRepository.findById(request.couponId())
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


}
