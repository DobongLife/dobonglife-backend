package com.umust.dobonglife.domain.review.presentation;

<<<<<<< Updated upstream
import com.umust.dobonglife.domain.auth.model.UserPrincipal;
import com.umust.dobonglife.domain.review.presentation.dto.request.CreateReviewRequest;
import com.umust.dobonglife.domain.review.presentation.dto.response.MyReviewsScreenResponse;
=======
import com.umust.dobonglife.domain.review.presentation.dto.request.CreateReviewRequest;
>>>>>>> Stashed changes
import com.umust.dobonglife.domain.review.presentation.dto.response.ReviewResponse;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
<<<<<<< Updated upstream
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
=======
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
>>>>>>> Stashed changes

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 등록하기
    @PostMapping
<<<<<<< Updated upstream
    public BaseResponse<ReviewResponse> registerReview(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                       @RequestBody @Valid CreateReviewRequest request){
        Long userId = userPrincipal.getUserId();
        ReviewResponse response = reviewService.createReview(userId, request);
        return BaseResponse.ok(response);
    }

    // 내후기 - 리뷰 조회하기
    @GetMapping
    public BaseResponse<MyReviewsScreenResponse> getMyReview(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "2") int size){
        Long userId = userPrincipal.getUserId();
        MyReviewsScreenResponse response = reviewService.getMyReviewManagementData(userId, page, size);
=======
    public BaseResponse<ReviewResponse> registerCourse(@RequestBody @Valid CreateReviewRequest request){
        ReviewResponse response = reviewService.createReview(request);
>>>>>>> Stashed changes
        return BaseResponse.ok(response);
    }
}
