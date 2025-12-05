package com.umust.dobonglife.domain.review.presentation;

import com.umust.dobonglife.domain.review.presentation.dto.request.CreateReviewRequest;
import com.umust.dobonglife.domain.review.presentation.dto.response.ReviewResponse;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 등록하기
    @PostMapping
    public BaseResponse<ReviewResponse> registerReview(@RequestBody @Valid CreateReviewRequest request){
        ReviewResponse response = reviewService.createReview(request);
        return BaseResponse.ok(response);
    }
}
