package com.umust.dobonglife.domain.auth.service;

import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.domain.auth.exception.CustomJwtException;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.business.service.BusinessService;
import com.umust.dobonglife.domain.coupon.service.CouponService;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.domain.user.domain.entity.User;
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
    private final KakaoAuthService kakaoAuthService;
    private final GoogleAuthService googleAuthService;
    private final AppleAuthService appleAuthService;

    @Transactional
    public void logout(HttpServletRequest request) {
        String accessToken = jwtUtil.extractAccessToken(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.SECURITY_INVALID_ACCESS_TOKEN));

        log.info("LogOut Access Token: {}", accessToken);

        jwtUtil.validateToken(accessToken);

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

        revokeProviderAccount(userId);

        String accessToken = jwtUtil.extractAccessToken(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.SECURITY_INVALID_ACCESS_TOKEN));

        String refreshToken = jwtUtil.extractRefreshToken(request).orElse(null);

        if (businessService.isBusiness(userId)) {
            Business business = businessService.getBusinessByUser(userId);
            Place place = business.getPlace();

            if (place != null) {
                Long placeId = place.getId();

                reviewService.deleteByPlaceId(placeId);
                placeService.deletePlaceLikeByPlaceId(placeId);
                courseService.nullifyPlaceInPlans(placeId);

                promotionService.deleteByUserId(userId);
                businessService.nullifyPlace(business.getId());
                entityManager.flush();

                log.info("=== DB와 메모리 정화 완료. Place 삭제 시도 ===");
                placeService.deleteById(placeId);
                entityManager.flush();
            }
            log.info("=== DB와 메모리 정화 완료. Business 삭제 시도 ===");
            businessService.deleteById(business.getId());
        }
        reviewService.setNullByUserId(userId);
        pointService.deleteByUserId(userId);
        couponService.deleteByUserId(userId);
        userService.deleteAccount(userId);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                jwtService.invalidAccessToken(accessToken);
                if (refreshToken != null) {
                    jwtService.deleteRefreshToken(refreshToken);
                }
            }
        });
    }

    private void revokeProviderAccount(Long userId) {
        try {
            User user = userService.findById(userId);
            Provider provider = user.getProvider();
            if (provider == null || provider == Provider.LOCAL) {
                return;
            }
            switch (provider) {
                case KAKAO -> kakaoAuthService.unlinkUser(user.getProviderId());
                case APPLE -> appleAuthService.revokeToken(user.getProviderToken());
                default -> log.warn("지원하지 않는 소셜 프로바이더: {}", provider);
            }
        } catch (Exception e) {
            log.warn("소셜 프로바이더 연결 해제 실패 (계정 삭제는 계속 진행): userId={}, error={}", userId, e.getMessage());
        }
    }
}
