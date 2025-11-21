package com.umust.dobonglife.domain.review.controller;

import com.umust.dobonglife.domain.review.dto.request.ReviewRegisterRequest;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public BaseResponse<Void> registerReview(ReviewRegisterRequest request) {
        reviewService.registerReview(request);
        return BaseResponse.ok(null);
    }
}
