package com.umust.dobonglife.user.withdraw;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawOrchestrator {

    private final WithdrawServiceClient withdrawServiceClient;

    public void execute(Long userId, String accessToken, String refreshToken) {
        boolean pendingMarked = false;
        boolean reviewCleaned = false;
        boolean likeCleaned = false;
        boolean couponCleaned = false;
        boolean pointCleaned = false;

        revokeSocialAccount(userId);

        try {
            // Mark user as pending (local call to user-service internal)
            withdrawServiceClient.markPending(userId);
            pendingMarked = true;

            // Cleanup content-service data
            withdrawServiceClient.cleanupReviews(userId);
            reviewCleaned = true;

            withdrawServiceClient.cleanupLikes(userId);
            likeCleaned = true;

            // Cleanup commerce-service data
            withdrawServiceClient.cleanupCoupons(userId);
            couponCleaned = true;

            withdrawServiceClient.cleanupPoints(userId);
            pointCleaned = true;

            // Delete user account
            withdrawServiceClient.deleteAccount(userId);

        } catch (Exception e) {
            compensate(userId, pointCleaned, couponCleaned, likeCleaned, reviewCleaned, pendingMarked);
            throw e;
        }

        finalize(userId);
        withdrawServiceClient.invalidateToken(accessToken, refreshToken);
    }

    private void finalize(Long userId) {
        safeCall(() -> withdrawServiceClient.finalizeReviews(userId), "리뷰 finalize");
        safeCall(() -> withdrawServiceClient.finalizeLikes(userId), "좋아요 finalize");
        safeCall(() -> withdrawServiceClient.finalizeCoupons(userId), "쿠폰 finalize");
        safeCall(() -> withdrawServiceClient.finalizePoints(userId), "포인트 finalize");
    }

    private void compensate(Long userId, boolean pointCleaned, boolean couponCleaned,
                            boolean likeCleaned, boolean reviewCleaned, boolean pendingMarked) {
        if (pointCleaned) safeCall(() -> withdrawServiceClient.restorePoints(userId), "포인트 복구");
        if (couponCleaned) safeCall(() -> withdrawServiceClient.restoreCoupons(userId), "쿠폰 복구");
        if (likeCleaned) safeCall(() -> withdrawServiceClient.restoreLikes(userId), "좋아요 복구");
        if (reviewCleaned) safeCall(() -> withdrawServiceClient.restoreReviews(userId), "리뷰 복구");
        if (pendingMarked) safeCall(() -> withdrawServiceClient.restoreAccount(userId), "사용자 상태 복구");
    }

    private void revokeSocialAccount(Long userId) {
        safeCall(() -> withdrawServiceClient.revokeSocialAccount(userId), "소셜 연동 해제");
    }

    private void safeCall(Runnable action, String label) {
        try {
            action.run();
        } catch (Exception e) {
            log.warn("[회원탈퇴] {} 실패. {}", label, e.getMessage());
        }
    }
}
