package com.umust.dobonglife.domain.reviewLike.controller;

import com.umust.dobonglife.domain.auth.domain.entity.UserPrincipal;
import com.umust.dobonglife.domain.reviewLike.controller.dto.response.ReviewLikeResponse;
import com.umust.dobonglife.domain.reviewLike.service.ReviewLikeService;
import com.umust.dobonglife.global.common.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review/like")
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    @PostMapping("/{reviewId}")
    public BaseResponse<ReviewLikeResponse> updateReviewLike(
            @PathVariable("reviewId") Long reviewId,
            @CurrentUserId Long userId) {
        ReviewLikeResponse responses = reviewLikeService.updateReviewLike(reviewId, userId);
        return BaseResponse.ok(responses);
    }
}