package com.umust.dobonglife.domain.coupon.service;

import com.umust.dobonglife.domain.business.service.BusinessService;
import com.umust.dobonglife.domain.coupon.controller.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.domain.coupon.controller.dto.request.PromotionUpdateRequest;
import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import com.umust.dobonglife.domain.coupon.domain.repository.PromotionRepository;
import com.umust.dobonglife.domain.coupon.controller.dto.response.*;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.notification.domain.constant.NotificationType;
import com.umust.dobonglife.domain.notification.service.NotificationService;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.response.CursorUtils;
import com.umust.dobonglife.global.common.response.slice.SliceResponse;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.external.s3.S3Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionService {

    @PersistenceContext
    private EntityManager entityManager;
    private final PromotionRepository promotionRepository;
    private final PointService pointService;
    private final PlaceService placeService;
    private final UserService userService;
    private final BusinessService businessService;
    private final CouponService couponService;
    private final S3Utils s3Utils;
    private final NotificationService notificationService;

    public void registerPromotion(Promotion promotion) {
        promotionRepository.save(promotion);
    }

    public PromotionGetResponse getPromotionWithBlocked(Long userId, Long lastId, int size) {
        CursorResponse<PromotionItem> cursorResponse = getPromotion(lastId, size);
        boolean blockedUser = userService.isBlockedUser(userId);

        return new PromotionGetResponse(blockedUser, cursorResponse);
    }

    public CursorResponse<PromotionItem> getPromotion(Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Promotion> promotions = promotionRepository.findPromotionWithPlaceNoOffset(lastId, pageable);

        return CursorUtils.toCursorResponse(promotions, PromotionItem::from);
    }

    public CursorResponse<PromotionSummaryItem> getPromotionSummary(Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Promotion> promotions = promotionRepository.findPromotionWithPlaceNoOffset(lastId, pageable);

        return CursorUtils.toCursorResponse(promotions, PromotionSummaryItem::from);
    }

    public UsedCouponResponse changePointToCoupon(Long userId, Long promotionId) {
        User user = userService.findById(userId);
        userService.canExchangeCoupon(userId);
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMOTION_NOT_FOUND));


        if(!pointService.processUserPoint(userId, promotion.getPoint()))
            throw new BusinessException(ErrorCode.INVALID_POINT);
        // TODO: 쿠폰 발급 시스템
        pointService.usePoint(userId, promotion.getTitle(), promotion.getPoint());

        Long couponId = couponService.createCoupon(promotion, userId, promotion.getStartDate(), promotion.getValidPeriod());
        notificationService.createNotification(user,
                NotificationType.POINT,
                "포인트 사용 안내",
                promotion.getPoint() + "포인트가 사용되었습니다! (포인트 교환)",
                null);

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
        Place place = placeService.findById(placeId);
        Promotion promotion = Promotion.createPromotion(request, imageUrls, userId, place);
        Promotion savedPromotion = promotionRepository.save(promotion);
        return PromotionRegisterResponse.from(savedPromotion);
    }

    @Transactional
    public PromotionUpdateResponse updateCoupon(PromotionUpdateRequest request, Long promotionId, Long userId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_PROMOTION_ID));

        if (!promotion.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.BUSINESS_NOT_FOUND);
        }

        promotion.update(request);
        return PromotionUpdateResponse.from(promotion);
    }

    @Transactional(readOnly = true)
    public CursorResponse<PromotionBannerItem> getPromotionBanner(Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Promotion> promotions = promotionRepository.findPromotionBannersNoOffset(lastId, pageable);

        return CursorUtils.toCursorResponse(promotions, PromotionBannerItem::from);
    }

//    @Transactional
//    public void deleteByBusinessId(Long businessId) {
//        List<Promotion> promotions = promotionRepository.findAllByBusinessId(businessId);
//
//        if (!promotions.isEmpty()) {
//            couponService.setDisabled(promotions);
//            promotionRepository.deleteAll(promotions);
//        }
//    }

    @Transactional
    public void deleteByUserId(Long userId) {
        log.info("=== [Promotion 삭제] userId: {} 조회 시작", userId);
        List<Promotion> promotions = promotionRepository.findAllByUserId(userId);

        log.info("=== 찾은 Promotion 개수: {}", promotions.size());
        if (!promotions.isEmpty()) {
            log.info("=== Promotion ID 목록: {}",
                    promotions.stream().map(Promotion::getId).collect(Collectors.toList()));

            couponService.deleteCoupons(promotions);
            log.info("=== Coupon 비활성화 완료");

            promotionRepository.clearPlaceByUserId(userId);
            promotionRepository.deleteAllInBatch(promotions);
            entityManager.flush();
            entityManager.clear();
            log.info("=== Promotion deleteAll 호출 완료");
        } else {
            log.warn("=== ⚠️ 삭제할 Promotion이 없습니다! userId: {}", userId);
        }
    }
}
