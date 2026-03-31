package com.umust.dobonglife.content.internal;

import com.umust.dobonglife.domain.like.application.port.in.LikeCleanupUseCase;
import com.umust.dobonglife.domain.like.application.port.in.LikeRestoreUseCase;
import com.umust.dobonglife.domain.review.application.port.in.ReviewCleanupUseCase;
import com.umust.dobonglife.domain.review.application.port.in.ReviewRestoreUseCase;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/withdraw")
public class ContentInternalController {

    private final ReviewCleanupUseCase reviewCleanupUseCase;
    private final ReviewRestoreUseCase reviewRestoreUseCase;
    private final LikeCleanupUseCase likeCleanupUseCase;
    private final LikeRestoreUseCase likeRestoreUseCase;

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
}
