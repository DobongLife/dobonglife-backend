package com.umust.dobonglife.domain.coupon.service;

import com.umust.dobonglife.domain.business.service.BusinessService;
import com.umust.dobonglife.domain.coupon.controller.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import com.umust.dobonglife.domain.coupon.domain.repository.PromotionRepository;
import com.umust.dobonglife.domain.coupon.controller.dto.response.*;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.response.CursorUtils;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.external.s3.S3Utils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final PointService pointService;
    private final UserService userService;
    private final BusinessService businessService;
    private final CouponService couponService;
    private final S3Utils s3Utils;

    public void registerPromotion(Promotion promotion) {
        promotionRepository.save(promotion);
    }

    public CursorResponse<PromotionItem> getPromotion(Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Promotion> promotions = promotionRepository.findPromotionNoOffset(lastId, pageable);

        return CursorUtils.toCursorResponse(promotions, PromotionItem::from);
    }

    public CursorResponse<PromotionSummaryItem> getPromotionSummary(Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Promotion> promotions = promotionRepository.findPromotionNoOffset(lastId, pageable);

        return CursorUtils.toCursorResponse(promotions, PromotionSummaryItem::from);
    }

    public UsedCouponResponse changePointToCoupon(Long userId, Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new EntityNotFoundException("[ERROR] 프로모션이 존재하지 않습니다."));
        User user = userService.findById(userId);

        if(!pointService.processUserPoint(userId, promotion.getPoint()))
            throw new BusinessException(ErrorCode.INVALID_POINT);
        // TODO: 쿠폰 발급 시스템
        pointService.usePoint(promotion.getTitle(), promotion.getPoint(), user);

        Long couponId = couponService.createCoupon(promotion, userId, promotion.getStartDate(), promotion.getValidPeriod());
        return new UsedCouponResponse(couponId, CouponStatus.AVAILABLE);
    }

    public PromotionRegisterResponse registerCoupon(PromotionRegisterRequest request, Long userId, List<MultipartFile> imageFiles) {
        Long placeId = businessService.returnBusinessPlaceId(userId);

        List<String> imageUrls = new ArrayList<>();
        if (imageFiles != null && !imageFiles.isEmpty() && !imageFiles.get(0).isEmpty()) {
            imageUrls = s3Utils.uploadImages(imageFiles);
        }

        try {
            return savePromotionWithTransaction(request, imageUrls, userId, placeId);

        } catch (Exception e) {
            if (!imageUrls.isEmpty()) {
                s3Utils.deleteImages(imageUrls);
            }
            throw e;
        }
    }

    @Transactional
    public PromotionRegisterResponse savePromotionWithTransaction(PromotionRegisterRequest request, List<String> imageUrls, Long userId, Long placeId) {
        Promotion promotion = Promotion.createPromotion(request, imageUrls, userId, placeId);
        Promotion savedPromotion = promotionRepository.save(promotion);
        return PromotionRegisterResponse.from(savedPromotion);
    }

    private void validateManagerRole(Long userId) {
        Role userRole = userService.getUserRole(userId);
        if (userRole != Role.MANAGER) {
            throw new BusinessException(ErrorCode.NOT_BUSINESS);
        }
    }
}
