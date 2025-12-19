package com.umust.dobonglife.domain.review.presentation;

import com.umust.dobonglife.domain.auth.domain.entity.UserPrincipal;
import com.umust.dobonglife.domain.review.presentation.dto.request.CreateReviewRequest;
import com.umust.dobonglife.domain.review.presentation.dto.response.ReviewDetailResponse;
import com.umust.dobonglife.domain.review.presentation.dto.response.ReviewResponse;
import com.umust.dobonglife.domain.review.presentation.dto.response.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 등록하기
    @PostMapping
    public BaseResponse<ReviewResponse> registerReview(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                       @RequestPart("request") @Valid CreateReviewRequest request, @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles){
        Long userId = userPrincipal.getUserId();
        ReviewResponse response = reviewService.createReview(userId, request, imageFiles);
        return BaseResponse.ok(response);
    }

    // 리뷰 수정하기
    @PutMapping("/{reviewId}")
    public BaseResponse<ReviewResponse> updateReview(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                       @PathVariable("reviewId") Long reviewId,
                                                       @RequestPart("request") @Valid CreateReviewRequest request, @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles){
        Long userId = userPrincipal.getUserId();
        ReviewResponse response = reviewService.updateReview(reviewId, userId, request, imageFiles);
        return BaseResponse.ok(response);
    }

    // 리뷰 전체보기
    @GetMapping
    public BaseResponse<CursorResponse<ReviewSummaryResponse>> getReviews(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                          @RequestParam(required = false) Long lastReviewId,
                                                                          @RequestParam(defaultValue = "2") int size) {
        Long userId = userPrincipal.getUserId();
        CursorResponse<ReviewSummaryResponse> responses = reviewService.getReviews(userId, lastReviewId, size);
        return BaseResponse.ok(responses);
    }

    // 코스 상세보기 조회 (특정 리뷰)
    @GetMapping("/{reviewId}")
    public BaseResponse<ReviewDetailResponse> getReview(@PathVariable("reviewId") Long reviewId){
        ReviewDetailResponse response = reviewService.getReview(reviewId);
        return BaseResponse.ok(response);
    }

    // 내후기 - 리뷰 조회하기
    @GetMapping("/my")
    public BaseResponse<CursorResponse<ReviewSummaryResponse>> getMyReviews(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                          @RequestParam(required = false) Long lastReviewId,
                                                                          @RequestParam(defaultValue = "2") int size) {
        Long userId = userPrincipal.getUserId();
        CursorResponse<ReviewSummaryResponse> responses = reviewService.getMyReviews(userId, lastReviewId, size);
        return BaseResponse.ok(responses);
    }
}
