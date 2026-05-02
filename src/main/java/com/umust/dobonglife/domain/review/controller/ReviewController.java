package com.umust.dobonglife.domain.review.controller;

import com.umust.dobonglife.domain.review.controller.dto.request.CreateReviewRequest;
import com.umust.dobonglife.domain.review.controller.dto.response.CourseReviewSummaryResponse;
import com.umust.dobonglife.domain.review.controller.dto.response.PlaceReviewSummaryResponse;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewDetailResponse;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewResponse;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "리뷰 API", description = "리뷰 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 등록하기
    @Operation(summary = "리뷰 등록", description = "리뷰를 등록합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @PostMapping
    public BaseResponse<ReviewResponse> registerReview(@CurrentUserId Long userId,
                                                       @RequestPart("request") @Valid CreateReviewRequest request, @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles){
        ReviewResponse response = reviewService.createReview(userId, request, imageFiles);
        return BaseResponse.ok(response);
    }

    // 리뷰 수정하기
    @Operation(summary = "리뷰 수정", description = "리뷰를 수정합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @PatchMapping("/{reviewId}")
    public BaseResponse<ReviewResponse> updateReview(@CurrentUserId Long userId,
                                                       @PathVariable("reviewId") Long reviewId,
                                                       @RequestPart("request") @Valid CreateReviewRequest request, @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles){
        ReviewResponse response = reviewService.updateReview(reviewId, userId, request, imageFiles);
        return BaseResponse.ok(response);
    }

    // 리뷰 전체보기
//    @Operation(summary = "리뷰 조회", description = "리뷰를 전체조회 합니다.")
//    @ApiResponse(
//            responseCode = "200",
//            description = "요청에 성공하였습니다."
//    )
//    @GetMapping
//    public BaseResponse<CursorResponse<ReviewSummaryResponse>> getReviews(@CurrentUserId Long userId,
//                                                                          @RequestParam(required = false) Long lastReviewId,
//                                                                          @RequestParam(defaultValue = "2") int size) {
//        CursorResponse<ReviewSummaryResponse> responses = reviewService.getReviews(userId, lastReviewId, size);
//        return BaseResponse.ok(responses);
//    }

    @Operation(summary = "코스 리뷰 조회", description = "코스의 리뷰를 전체조회 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping("/course/{courseId}")
    public BaseResponse<CursorResponse<ReviewSummaryResponse>> getCourseReviews(@CurrentUserId Long userId,
                                                                          @PathVariable Long courseId,
                                                                          @RequestParam(required = false) Long lastReviewId,
                                                                          @RequestParam(defaultValue = "3") int size) {
        CursorResponse<ReviewSummaryResponse> responses = reviewService.getCourseReviews(courseId, userId, lastReviewId, size);
        return BaseResponse.ok(responses);
    }

    @Operation(summary = "장소 리뷰 조회", description = "장소의 리뷰를 전체조회 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping("/place/{placeId}")
    public BaseResponse<CursorResponse<ReviewSummaryResponse>> getPlaceReviews(@CurrentUserId Long userId,
                                                                          @PathVariable Long placeId,
                                                                          @RequestParam(required = false) Long lastReviewId,
                                                                          @RequestParam(defaultValue = "3") int size) {
        CursorResponse<ReviewSummaryResponse> responses = reviewService.getPlaceReviews(placeId, userId, lastReviewId, size);
        return BaseResponse.ok(responses);
    }

    // 리뷰 상세보기 조회 (특정 리뷰)
//    @Operation(summary = "리뷰 상세 조회", description = "리뷰를 상세 조회합니다.")
//    @ApiResponse(
//            responseCode = "200",
//            description = "요청에 성공하였습니다."
//    )
//    @GetMapping("/{reviewId}")
//    public BaseResponse<ReviewDetailResponse> getReview(@PathVariable("reviewId") Long reviewId){
//        ReviewDetailResponse response = reviewService.getReview(reviewId);
//        return BaseResponse.ok(response);
//    }

    // 내후기 - 리뷰 조회하기
    @Operation(summary = "내 코스후기 조회", description = "내 리뷰를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping("/my/course")
    public BaseResponse<CursorResponse<CourseReviewSummaryResponse>> getMyCourseReviews(@CurrentUserId Long userId,
                                                                                  @RequestParam(required = false) Long lastReviewId,
                                                                                  @RequestParam(defaultValue = "2") int size) {
        CursorResponse<CourseReviewSummaryResponse> 111responses = reviewService.getMyCourseReviews(userId, lastReviewId, size);
        return BaseResponse.ok(responses);
    }

    @Operation(summary = "내 장소후기 조회", description = "내 리뷰를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping("/my/place")
    public BaseResponse<CursorResponse<PlaceReviewSummaryResponse>> getMyPlaceReviews(@CurrentUserId Long userId,
                                                                                 @RequestParam(required = false) Long lastReviewId,
                                                                                 @RequestParam(defaultValue = "2") int size) {
        CursorResponse<PlaceReviewSummaryResponse> responses = reviewService.getMyPlaceReviews(userId, lastReviewId, size);
        return BaseResponse.ok(responses);
    }

    // 리뷰 삭제하기
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @DeleteMapping("/{reviewId}")
    public BaseResponse<ReviewResponse> deleteReview(@CurrentUserId Long userId,
                                                     @PathVariable("reviewId") Long reviewId){
        ReviewResponse response = reviewService.deleteReview(reviewId, userId);
        return BaseResponse.ok(response);
    }
}
