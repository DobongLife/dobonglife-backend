package com.umust.dobonglife.domain.review.controller;

import com.umust.dobonglife.domain.review.controller.dto.request.CreateReviewRequest;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewDetailResponse;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewResponse;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.global.common.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public BaseResponse<ReviewResponse> registerReview(@CurrentUserId Long userId,
                                                       @RequestPart("request") @Valid CreateReviewRequest request, @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles){
        ReviewResponse response = reviewService.createReview(userId, request, imageFiles);
        return BaseResponse.ok(response);
    }

    // 리뷰 수정하기
    @PutMapping("/{reviewId}")
    public BaseResponse<ReviewResponse> updateReview(@CurrentUserId Long userId,
                                                       @PathVariable("reviewId") Long reviewId,
                                                       @RequestPart("request") @Valid CreateReviewRequest request, @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles){
        ReviewResponse response = reviewService.updateReview(reviewId, userId, request, imageFiles);
        return BaseResponse.ok(response);
    }

    // 리뷰 전체보기
    @GetMapping
    public BaseResponse<CursorResponse<ReviewSummaryResponse>> getReviews(@CurrentUserId Long userId,
                                                                          @RequestParam(required = false) Long lastReviewId,
                                                                          @RequestParam(defaultValue = "2") int size) {
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
    public BaseResponse<CursorResponse<ReviewSummaryResponse>> getMyReviews(@CurrentUserId Long userId,
                                                                          @RequestParam(required = false) Long lastReviewId,
                                                                          @RequestParam(defaultValue = "2") int size) {
        CursorResponse<ReviewSummaryResponse> responses = reviewService.getMyReviews(userId, lastReviewId, size);
        return BaseResponse.ok(responses);
    }
}
