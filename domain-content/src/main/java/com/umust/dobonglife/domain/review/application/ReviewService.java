package com.umust.dobonglife.domain.review.application;

import com.umust.dobonglife.domain.review.application.dto.CreateReviewRequest;
import com.umust.dobonglife.domain.review.application.dto.ReviewRegisterResponse;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.application.dto.UpdateReviewRequest;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.domain.repository.ReviewRepository;
import com.umust.dobonglife.domain.review.exception.ReviewErrorCode;
import com.umust.dobonglife.domain.review.exception.ReviewException;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public ReviewRegisterResponse createReview(Long userId, CreateReviewRequest request) {
        Review review = Review.builder()
                .userId(userId)
                .targetId(request.targetId())
                .targetType(request.targetType())
                .rating(request.rating())
                .content(request.content())
                .build();

        review.attachImages(request.imageUrls());
        reviewRepository.save(review);

        return ReviewRegisterResponse.from(review);
    }

    @Transactional
    public ReviewRegisterResponse updateReview(Long reviewId, Long userId, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        review.validateOwner(userId);
        review.update(request.rating(), request.content(), request.imageUrls());

        return ReviewRegisterResponse.from(review);
    }

    public CursorResponse<ReviewSummaryResponse> getPlaceReviews(Long placeId, Long lastId, int size) {
        Slice<ReviewSummaryResponse> slice = reviewRepository.findReviewsByPlaceId(placeId, lastId, size);
        return CursorUtils.toCursorResponse(slice, r -> r);
    }

    public CursorResponse<ReviewSummaryResponse> getCourseReviews(Long courseId, Long lastId, int size) {
        Slice<ReviewSummaryResponse> slice = reviewRepository.findReviewsByCourseId(courseId, lastId, size);
        return CursorUtils.toCursorResponse(slice, r -> r);
    }
}
