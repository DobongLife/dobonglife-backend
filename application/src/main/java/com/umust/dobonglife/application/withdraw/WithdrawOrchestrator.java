package com.umust.dobonglife.application.withdraw;

import com.umust.dobonglife.application.withdraw.client.WithdrawRestClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawOrchestrator {

    private final WithdrawRestClient withdrawRestClient;

    public void execute(Long userId, String accessToken, String refreshToken) {
        boolean pendingMarked = false;
        boolean reviewCleaned = false;
        boolean likeCleaned = false;
        boolean couponCleaned = false;
        boolean pointCleaned = false;

        // 소셜 연동 해제 (best-effort, 실패해도 탈퇴 진행)
        revokeSocialAccount(userId);

        try {
            // 1단계: ACTIVE → PENDING
            withdrawRestClient.markPending(userId);
            pendingMarked = true;

            withdrawRestClient.cleanupReviews(userId);
            reviewCleaned = true;

            withdrawRestClient.cleanupLikes(userId);
            likeCleaned = true;

            withdrawRestClient.cleanupCoupons(userId);
            couponCleaned = true;

            withdrawRestClient.cleanupPoints(userId);
            pointCleaned = true;

            // 2단계: 사용자 삭제 (PENDING → INACTIVE)
            withdrawRestClient.deleteAccount(userId);

        } catch (Exception e) {
            compensate(userId, pointCleaned, couponCleaned, likeCleaned, reviewCleaned, pendingMarked);
            throw e;
        }

        // 3단계: PENDING 데이터 최종 정리 (best-effort)
        finalize(userId);

        // 4단계: 토큰 무효화
        withdrawRestClient.invalidateToken(accessToken, refreshToken);
    }

    private void finalize(Long userId) {
        try {
            withdrawRestClient.finalizeReviews(userId);
        } catch (Exception e) {
            log.error("[회원탈퇴 최종정리] 리뷰 finalize 실패. userId={}", userId, e);
        }
        try {
            withdrawRestClient.finalizeLikes(userId);
        } catch (Exception e) {
            log.error("[회원탈퇴 최종정리] 좋아요 finalize 실패. userId={}", userId, e);
        }
        try {
            withdrawRestClient.finalizeCoupons(userId);
        } catch (Exception e) {
            log.error("[회원탈퇴 최종정리] 쿠폰 finalize 실패. userId={}", userId, e);
        }
        try {
            withdrawRestClient.finalizePoints(userId);
        } catch (Exception e) {
            log.error("[회원탈퇴 최종정리] 포인트 finalize 실패. userId={}", userId, e);
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
        // 역순 보상 (PENDING → ACTIVE)
        try {
            if (pointCleaned) {
                withdrawRestClient.restorePoints(userId);
                log.info("[회원탈퇴 보상] 포인트 복구 완료. userId={}", userId);
            }
        } catch (Exception e) {
            log.error("[회원탈퇴 보상] 포인트 복구 실패. userId={}", userId, e);
        }

        try {
            if (couponCleaned) {
                withdrawRestClient.restoreCoupons(userId);
                log.info("[회원탈퇴 보상] 쿠폰 복구 완료. userId={}", userId);
            }
        } catch (Exception e) {
            log.error("[회원탈퇴 보상] 쿠폰 복구 실패. userId={}", userId, e);
        }

        try {
            if (likeCleaned) {
                withdrawRestClient.restoreLikes(userId);
                log.info("[회원탈퇴 보상] 좋아요 복구 완료. userId={}", userId);
            }
        } catch (Exception e) {
            log.error("[회원탈퇴 보상] 좋아요 복구 실패. userId={}", userId, e);
        }

        try {
            if (reviewCleaned) {
                withdrawRestClient.restoreReviews(userId);
                log.info("[회원탈퇴 보상] 리뷰 복구 완료. userId={}", userId);
            }
        } catch (Exception e) {
            log.error("[회원탈퇴 보상] 리뷰 복구 실패. userId={}", userId, e);
        }

        try {
            if (pendingMarked) {
                withdrawRestClient.restoreAccount(userId);
                log.info("[회원탈퇴 보상] 사용자 상태 ACTIVE 복구 완료. userId={}", userId);
            }
        } catch (Exception e) {
            log.error("[회원탈퇴 보상] 사용자 상태 복구 실패. userId={}", userId, e);
        }
    }

    private void revokeSocialAccount(Long userId) {
        try {
            withdrawRestClient.revokeSocialAccount(userId);
            log.info("[회원탈퇴] 소셜 연동 해제 완료. userId={}", userId);
        } catch (Exception e) {
            log.warn("[회원탈퇴] 소셜 연동 해제 실패 (탈퇴는 계속 진행). userId={}", userId, e);
        }
    }
}
