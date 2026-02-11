package com.umust.dobonglife.domain.auth.service;

import com.umust.dobonglife.domain.auth.exception.CustomAuthenticationException;
import com.umust.dobonglife.domain.auth.exception.CustomJwtException;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.business.service.BusinessService;
import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.service.CouponService;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    @PersistenceContext
    private EntityManager entityManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final PointService pointService;
    private final CouponService couponService;
    private final PromotionService promotionService;
    private final BusinessService businessService;
    private final ReviewService reviewService;
    private final PlaceService placeService;
    private final CourseService courseService;
    private final JwtUtil jwtUtil;

    @Transactional
    public void logout(HttpServletRequest request) {
        String accessToken = jwtUtil.extractAccessToken(request)
                .orElseThrow(() -> new CustomAuthenticationException(ErrorCode.SECURITY_INVALID_ACCESS_TOKEN));

        log.info("LogOut Access Token: {}", accessToken);

        String refreshToken = jwtUtil.extractRefreshToken(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        jwtUtil.validateToken(refreshToken);
        if (!"refresh".equals(jwtUtil.getTokenType(refreshToken))) {
            throw new CustomJwtException(ErrorCode.INVALID_REFRESH_TYPE);
        }

        Long userId = jwtUtil.getUserId(accessToken);

        userService.inValidFcmToken(userId);

        jwtService.deleteRefreshToken(refreshToken);
        jwtService.invalidAccessToken(accessToken);
    }
    @Transactional
    public void deleteAccount(HttpServletRequest request, Long userId) {
        log.info("=== [회원탈퇴 시작] userId: {}", userId);

        String accessToken = jwtUtil.extractAccessToken(request)
                .orElseThrow(() -> new CustomAuthenticationException(ErrorCode.SECURITY_INVALID_ACCESS_TOKEN));

        Business business = businessService.getBusinessByUser(userId);

        if (business != null) {
            Long businessId = business.getId();
            Place place = business.getPlace();

            if (place != null) {
                Long placeId = place.getId();

                reviewService.deleteByPlaceId(placeId);
                placeService.deletePlaceLikeByPlaceId(placeId);
                courseService.nullifyPlaceInPlans(placeId);

                promotionService.deleteByBusinessId(businessId);
                businessService.nullifyPlace(businessId);
                entityManager.flush();

                log.info("=== DB와 메모리 정화 완료. Place 삭제 시도 ===");
                placeService.deleteById(placeId);
                entityManager.flush();
            }
            log.info("=== DB와 메모리 정화 완료. Business 삭제 시도 ===");
            businessService.deleteById(businessId);
        }
        reviewService.deleteByUserId(userId);
        pointService.deleteByUserId(userId);
        couponService.deleteByUserId(userId);
        userService.deleteAccount(userId);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                jwtService.invalidAccessToken(accessToken);
            }
        });
    }

//    @Transactional
//    public void deleteAccount(HttpServletRequest request, Long userId) { // TODO: 이벤트 형식으로 변경
//        String accessToken = jwtUtil.extractAccessToken(request)
//                .orElseThrow(() -> new CustomAuthenticationException(ErrorCode.SECURITY_INVALID_ACCESS_TOKEN));
//
//        Business business = businessService.getBusinessByUser(userId);
//        if (business != null) {
//            Long businessId = business.getId();
//            Place place = business.getPlace();
//
//            if(place != null) {
//                Long placeId = place.getId();
//                reviewService.deleteByPlaceId(placeId);         // 리뷰 삭제
//                placeService.deletePlaceLikeByPlaceId(placeId); // 찜 삭제
//                courseService.nullifyPlaceInPlans(placeId);     // 코스 내 참조 null 처리
//
//                promotionService.deleteByBusinessId(businessId);
//                promotionService.flush();
//                placeService.deleteByPlace(place);              // 장소 삭제
//            }
//            businessService.deleteById(businessId);         // 비즈니스 삭제
//        }
//
//        reviewService.deleteByUserId(userId);
//        pointService.deleteByUserId(userId);
//        couponService.deleteByUserId(userId);
//        userService.deleteAccount(userId); // 유저 삭제(FCM 포함)
//
//
//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//            @Override
//            public void afterCommit() {
//                jwtService.invalidAccessToken(accessToken);
//            }
//        });
//    }
}
