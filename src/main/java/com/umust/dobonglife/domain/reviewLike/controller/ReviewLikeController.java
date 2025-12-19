package com.umust.dobonglife.domain.reviewLike.controller;

import com.umust.dobonglife.domain.auth.domain.entity.UserPrincipal;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.domain.courseLike.controller.dto.request.CourseLikeResponse;
import com.umust.dobonglife.domain.courseLike.service.CourseLikeService;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.domain.reviewLike.controller.dto.request.ReviewLikeResponse;
import com.umust.dobonglife.domain.reviewLike.service.ReviewLikeService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
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
            @PathVariable("courseId") Long courseId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        ReviewLikeResponse responses = reviewLikeService.updateReviewLike(courseId, userId);
        return BaseResponse.ok(responses);
    }
}