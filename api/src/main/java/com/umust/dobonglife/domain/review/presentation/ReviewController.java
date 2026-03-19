package com.umust.dobonglife.domain.review.presentation;

import com.umust.dobonglife.domain.review.application.ReviewService;
import com.umust.dobonglife.domain.review.application.dto.CreateReviewRequest;
import com.umust.dobonglife.domain.review.application.dto.ReviewRegisterResponse;
import com.umust.dobonglife.domain.review.application.dto.UpdateReviewRequest;
import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<BaseResponse<ReviewRegisterResponse>> registerReview(
            @CurrentUserId Long userId,
            @RequestBody @Valid CreateReviewRequest request) {
        return ResponseEntity.ok(BaseResponse.ok(reviewService.createReview(userId, request)));
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<BaseResponse<ReviewRegisterResponse>> updateReview(
            @CurrentUserId Long userId,
            @PathVariable Long reviewId,
            @RequestBody @Valid UpdateReviewRequest request) {
        return ResponseEntity.ok(BaseResponse.ok(reviewService.updateReview(reviewId, userId, request)));
    }
}
