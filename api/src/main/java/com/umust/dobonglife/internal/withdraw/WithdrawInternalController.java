package com.umust.dobonglife.internal.withdraw;

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
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/withdraw")
public class WithdrawInternalController {

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

    // ── User 상태 변경 ──

    @PostMapping("/users/{userId}/mark-pending")
    public BaseResponse<Void> markPending(@PathVariable Long userId) {
        deleteAccountUseCase.markPending(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/users/{userId}/delete")
    public BaseResponse<Void> deleteAccount(@PathVariable Long userId) {
        deleteAccountUseCase.deleteAccount(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/users/{userId}/restore")
    public BaseResponse<Void> restoreAccount(@PathVariable Long userId) {
        deleteAccountUseCase.restoreAccount(userId);
        return BaseResponse.ok(null);
    }

    // ── 종속 데이터 정리 (ACTIVE → PENDING) ──

    @PostMapping("/reviews/{userId}/cleanup")
    public BaseResponse<Void> cleanupReviews(@PathVariable Long userId) {
        reviewCleanupUseCase.markPendingByUserId(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/likes/{userId}/cleanup")
    public BaseResponse<Void> cleanupLikes(@PathVariable Long userId) {
        likeCleanupUseCase.markPendingByUserId(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/coupons/{userId}/cleanup")
    public BaseResponse<Void> cleanupCoupons(@PathVariable Long userId) {
        couponCleanupUseCase.markPendingByUserId(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/points/{userId}/cleanup")
    public BaseResponse<Void> cleanupPoints(@PathVariable Long userId) {
        pointCleanupUseCase.markPendingByUserId(userId);
        return BaseResponse.ok(null);
    }

    // ── 보상 트랜잭션 (PENDING → ACTIVE) ──

    @PostMapping("/reviews/{userId}/restore")
    public BaseResponse<Void> restoreReviews(@PathVariable Long userId) {
        reviewRestoreUseCase.restoreByUserId(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/likes/{userId}/restore")
    public BaseResponse<Void> restoreLikes(@PathVariable Long userId) {
        likeRestoreUseCase.restoreByUserId(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/coupons/{userId}/restore")
    public BaseResponse<Void> restoreCoupons(@PathVariable Long userId) {
        couponRestoreUseCase.restoreByUserId(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/points/{userId}/restore")
    public BaseResponse<Void> restorePoints(@PathVariable Long userId) {
        pointRestoreUseCase.restoreByUserId(userId);
        return BaseResponse.ok(null);
    }

    // ── 최종 정리 (PENDING 데이터 확정 삭제) ──

    @PostMapping("/reviews/{userId}/finalize")
    public BaseResponse<Void> finalizeReviews(@PathVariable Long userId) {
        reviewCleanupUseCase.finalizeByUserId(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/likes/{userId}/finalize")
    public BaseResponse<Void> finalizeLikes(@PathVariable Long userId) {
        likeCleanupUseCase.finalizeByUserId(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/coupons/{userId}/finalize")
    public BaseResponse<Void> finalizeCoupons(@PathVariable Long userId) {
        couponCleanupUseCase.finalizeByUserId(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/points/{userId}/finalize")
    public BaseResponse<Void> finalizePoints(@PathVariable Long userId) {
        pointCleanupUseCase.finalizeByUserId(userId);
        return BaseResponse.ok(null);
    }

    // ── 소셜 연동 해제 ──

    @PostMapping("/auth/{userId}/revoke-social")
    public BaseResponse<Void> revokeSocialAccount(@PathVariable Long userId) {
        Provider provider = getUserUseCase.getProvider(userId);
        String providerId = getUserUseCase.getProviderId(userId);
        revokeSocialAccountUseCase.revoke(provider, providerId);
        return BaseResponse.ok(null);
    }

    // ── 토큰 무효화 ──

    @PostMapping("/auth/invalidate-token")
    public BaseResponse<Void> invalidateToken(
            @RequestParam String accessToken,
            @RequestParam(required = false) String refreshToken
    ) {
        authTokenUseCase.invalidateAccessToken(accessToken);
        if (refreshToken != null) {
            authTokenUseCase.deleteRefreshToken(refreshToken);
        }
        return BaseResponse.ok(null);
    }
}
