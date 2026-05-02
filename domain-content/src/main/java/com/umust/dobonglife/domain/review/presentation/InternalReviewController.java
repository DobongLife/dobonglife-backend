package com.umust.dobonglife.domain.review.presentation;

import com.umust.dobonglife.domain.review.application.port.in.GetReviewUseCase;
import com.umust.dobonglife.domain.review.application.port.in.ManageReviewUseCase;
import com.umust.dobonglife.domain.review.application.port.in.ReviewCleanupUseCase;
import com.umust.dobonglife.domain.review.application.port.in.ReviewRestoreUseCase;
import com.umust.dobonglife.domain.review.application.dto.CreateReviewRequest;
import com.umust.dobonglife.domain.review.application.dto.MyReviewResponse;
import com.umust.dobonglife.domain.review.application.dto.ReviewRegisterResponse;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.application.dto.UpdateReviewRequest;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/review")
@RequiredArgsConstructor
public class InternalReviewController {

    private final ManageReviewUseCase manageReviewUseCase;
    private final GetReviewUseCase getReviewUseCase;
    private final ReviewCleanupUseCase reviewCleanupUseCase;
    private final ReviewRestoreUseCase reviewRestoreUseCase;

    @PostMapping
    public ReviewRegisterResponse createReview(
            @RequestParam Long userId,
            @RequestBody CreateReviewRequest request) {
        return manageReviewUseCase.createReview(userId, request);
    }

    @PatchMapping("/{reviewId}")
    public ReviewRegisterResponse updateReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId,
            @RequestBody UpdateReviewRequest request) {
        return manageReviewUseCase.updateReview(reviewId, userId, request);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId, @RequestParam Long userId) {
        manageReviewUseCase.deleteReview(reviewId, userId);
    }

    @GetMapping("/{targetType}/{targetId}")
    public CursorResponse<ReviewSummaryResponse> getReviews(
            @PathVariable TargetType targetType,
            @PathVariable Long targetId,
            @RequestParam(required = false) Long lastId,
            @RequestParam int size) {
        return getReviewUseCase.getReviews(targetType, targetId, lastId, size);
    }

    @PostMapping("/withdraw/{userId}/cleanup")
    public void cleanupReviews(@PathVariable Long userId) {
        reviewCleanupUseCase.markPendingByUserId(userId);
    }

    @PostMapping("/withdraw/{userId}/restore")
    public void restoreReviews(@PathVariable Long userId) {
        reviewRestoreUseCase.restoreByUserId(userId);
    }

    @PostMapping("/withdraw/{userId}/finalize")
    public void finalizeReviews(@PathVariable Long userId) {
        reviewCleanupUseCase.finalizeByUserId(userId);
    }

    @GetMapping("/my/{targetType}")
    public CursorResponse<MyReviewResponse> getMyReviews(
            @RequestParam Long userId,
            @PathVariable TargetType targetType,
            @RequestParam(required = false) Long lastId,
            @RequestParam int size) {
        return getReviewUseCase.getMyReviews(userId, targetType, lastId, size);
    }
}
