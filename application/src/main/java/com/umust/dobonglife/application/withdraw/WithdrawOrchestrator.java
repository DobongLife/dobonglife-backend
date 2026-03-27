package com.umust.dobonglife.application.withdraw;

import com.umust.dobonglife.domain.auth.application.port.in.AuthTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.RevokeSocialAccountUseCase;
import com.umust.dobonglife.domain.coupon.application.port.in.CouponCleanupUseCase;
import com.umust.dobonglife.domain.coupon.application.port.in.CouponRestoreUseCase;
import com.umust.dobonglife.domain.like.application.port.in.LikeCleanupUseCase;
import com.umust.dobonglife.domain.like.application.port.in.LikeRestoreUseCase;
import com.umust.dobonglife.domain.point.application.port.in.PointCleanupUseCase;
import com.umust.dobonglife.domain.point.application.port.in.PointRestoreUseCase;
import com.umust.dobonglife.domain.review.application.port.in.ReviewCleanupUseCase;
import com.umust.dobonglife.domain.review.application.port.in.ReviewRestoreUseCase;
import com.umust.dobonglife.domain.user.application.port.in.DeleteAccountUseCase;
import com.umust.dobonglife.domain.user.application.port.in.GetUserUseCase;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawOrchestrator {

    private final DeleteAccountUseCase deleteAccountUseCase;
    private final GetUserUseCase getUserUseCase;
    private final RevokeSocialAccountUseCase revokeSocialAccountUseCase;
    private final AuthTokenUseCase authTokenUseCase;

    private final ReviewCleanupUseCase reviewCleanupUseCase;
    private final LikeCleanupUseCase likeCleanupUseCase;
    private final CouponCleanupUseCase couponCleanupUseCase;
    private final PointCleanupUseCase pointCleanupUseCase;

    private final ReviewRestoreUseCase reviewRestoreUseCase;
    private final LikeRestoreUseCase likeRestoreUseCase;
    private final CouponRestoreUseCase couponRestoreUseCase;
    private final PointRestoreUseCase pointRestoreUseCase;

    public void execute(Long userId, String accessToken, String refreshToken) {
        boolean pendingMarked = false;
        boolean reviewCleaned = false;
        boolean likeCleaned = false;
        boolean couponCleaned = false;
        boolean pointCleaned = false;

        // 소셜 연동 해제 (best-effort, 실패해도 탈퇴 진행)
        revokeSocialAccount(userId);

        try {
            // ACTIVE → PENDING
            deleteAccountUseCase.markPending(userId);
            pendingMarked = true;

            // 종속 데이터 정리
            reviewCleanupUseCase.nullifyByUserId(userId);
            reviewCleaned = true;

            likeCleanupUseCase.deleteByUserId(userId);
            likeCleaned = true;

            couponCleanupUseCase.deleteByUserId(userId);
            couponCleaned = true;

            pointCleanupUseCase.deleteByUserId(userId);
            pointCleaned = true;

            // PENDING → INACTIVE
            deleteAccountUseCase.deleteAccount(userId);

        } catch (Exception e) {
            compensate(userId, pointCleaned, couponCleaned, likeCleaned, reviewCleaned, pendingMarked);
            throw e;
        }

        // 모든 단계 성공 후 토큰 무효화
        authTokenUseCase.invalidateAccessToken(accessToken);
        if (refreshToken != null) {
            authTokenUseCase.deleteRefreshToken(refreshToken);
        }
    }

    private void compensate(
            Long userId,
            boolean pointCleaned,
            boolean couponCleaned,
            boolean likeCleaned,
            boolean reviewCleaned,
            boolean pendingMarked
    ) {
        // 역순 보상
        try {
            if (pointCleaned) {
                pointRestoreUseCase.restoreByUserId(userId);
                log.info("[회원탈퇴 보상] 포인트 복구 완료. userId={}", userId);
            }
        } catch (Exception e) {
            log.error("[회원탈퇴 보상] 포인트 복구 실패. userId={}", userId, e);
        }

        try {
            if (couponCleaned) {
                couponRestoreUseCase.restoreByUserId(userId);
                log.info("[회원탈퇴 보상] 쿠폰 복구 완료. userId={}", userId);
            }
        } catch (Exception e) {
            log.error("[회원탈퇴 보상] 쿠폰 복구 실패. userId={}", userId, e);
        }

        try {
            if (likeCleaned) {
                likeRestoreUseCase.restoreByUserId(userId);
                log.info("[회원탈퇴 보상] 좋아요 복구 완료. userId={}", userId);
            }
        } catch (Exception e) {
            log.error("[회원탈퇴 보상] 좋아요 복구 실패. userId={}", userId, e);
        }

        try {
            if (reviewCleaned) {
                reviewRestoreUseCase.restoreByUserId(userId);
                log.info("[회원탈퇴 보상] 리뷰 복구 완료. userId={}", userId);
            }
        } catch (Exception e) {
            log.error("[회원탈퇴 보상] 리뷰 복구 실패. userId={}", userId, e);
        }

        try {
            if (pendingMarked) {
                deleteAccountUseCase.restoreAccount(userId);
                log.info("[회원탈퇴 보상] 사용자 상태 ACTIVE 복구 완료. userId={}", userId);
            }
        } catch (Exception e) {
            log.error("[회원탈퇴 보상] 사용자 상태 복구 실패. userId={}", userId, e);
        }
    }

    private void revokeSocialAccount(Long userId) {
        try {
            Provider provider = getUserUseCase.getProvider(userId);
            String providerId = getUserUseCase.getProviderId(userId);
            revokeSocialAccountUseCase.revoke(provider, providerId);
            log.info("[회원탈퇴] 소셜 연동 해제 완료. userId={}", userId);
        } catch (Exception e) {
            log.warn("[회원탈퇴] 소셜 연동 해제 실패 (탈퇴는 계속 진행). userId={}", userId, e);
        }
    }
}
