package com.umust.dobonglife.domain.review.presentation;

import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.port.content.ReviewPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewPort reviewPort;

    @PostMapping
    public ResponseEntity<BaseResponse<Map<String, Object>>> registerReview(
            @CurrentUserId Long userId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(BaseResponse.ok(reviewPort.createReview(userId, request)));
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<BaseResponse<Map<String, Object>>> updateReview(
            @CurrentUserId Long userId,
            @PathVariable Long reviewId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(BaseResponse.ok(reviewPort.updateReview(reviewId, userId, request)));
    }

    @GetMapping("/{targetType}/{targetId}")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getReviews(
            @PathVariable TargetType targetType,
            @PathVariable Long targetId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.REVIEW) int size) {
        return ResponseEntity.ok(BaseResponse.ok(reviewPort.getReviews(targetType, targetId, lastId, size)));
    }

    @GetMapping("/my/{targetType}")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getMyReviews(
            @CurrentUserId Long userId,
            @PathVariable TargetType targetType,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.REVIEW) int size) {
        return ResponseEntity.ok(BaseResponse.ok(reviewPort.getMyReviews(userId, targetType, lastId, size)));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<BaseResponse<Void>> deleteReview(
            @CurrentUserId Long userId,
            @PathVariable Long reviewId) {
        reviewPort.deleteReview(reviewId, userId);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}
