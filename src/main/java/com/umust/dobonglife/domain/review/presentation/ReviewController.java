package com.umust.dobonglife.domain.review.presentation;

import com.umust.dobonglife.domain.auth.domain.entity.UserPrincipal;
import com.umust.dobonglife.domain.course.controller.dto.request.CreateCourseRequest;
import com.umust.dobonglife.domain.review.presentation.dto.request.CreateReviewRequest;
import com.umust.dobonglife.domain.review.presentation.dto.response.MyReviewsScreenResponse;
import com.umust.dobonglife.domain.review.presentation.dto.response.ReviewResponse;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.global.common.response.BaseResponse;
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

    // 전체 리뷰 조회하기

    // 특정 리뷰 조회하기

    // 내후기 - 리뷰 조회하기
    @GetMapping
    public BaseResponse<MyReviewsScreenResponse> getMyReview(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "2") int size){
        Long userId = userPrincipal.getUserId();
        MyReviewsScreenResponse response = reviewService.getMyReviewManagementData(userId, page, size);

        return BaseResponse.ok(response);
    }
}
