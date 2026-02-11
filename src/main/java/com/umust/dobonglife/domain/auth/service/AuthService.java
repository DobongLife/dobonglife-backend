package com.umust.dobonglife.domain.auth.service;

import com.umust.dobonglife.domain.auth.exception.CustomAuthenticationException;
import com.umust.dobonglife.domain.auth.exception.CustomJwtException;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.business.service.BusinessService;
import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.service.CouponService;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
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

    private final JwtService jwtService;
    private final UserService userService;
    private final PointService pointService;
    private final CouponService couponService;
    private final PromotionService promotionService;
    private final BusinessService businessService;
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
        String accessToken = jwtUtil.extractAccessToken(request)
                .orElseThrow(() -> new CustomAuthenticationException(ErrorCode.SECURITY_INVALID_ACCESS_TOKEN));

        Business business = businessService.getBusinessByUser(userId);
        if (business != null) {
            promotionService.deleteByBusinessId(business.getId());
            businessService.deleteById(business.getId());
        }

        pointService.deleteByUserId(userId);
        couponService.deleteByUserId(userId);

        userService.deleteAccount(userId); // 유저 삭제(FCM 포함)

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                jwtService.invalidAccessToken(accessToken);
            }
        });
    }
}
