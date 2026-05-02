package com.umust.dobonglife.domain.review.application;

import com.umust.dobonglife.domain.course.application.port.in.ManageCourseUseCase;
import com.umust.dobonglife.domain.place.application.port.in.ManagePlaceUseCase;
import com.umust.dobonglife.domain.review.application.dto.CreateReviewRequest;
import com.umust.dobonglife.domain.review.application.dto.ReviewRegisterResponse;
import com.umust.dobonglife.domain.review.application.dto.UpdateReviewRequest;
import com.umust.dobonglife.domain.review.application.port.in.ManageReviewUseCase;
import com.umust.dobonglife.domain.review.application.port.out.LoadReviewPort;
import com.umust.dobonglife.domain.review.application.port.out.SaveReviewPort;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.exception.ReviewErrorCode;
import com.umust.dobonglife.domain.review.exception.ReviewException;
import com.umust.dobonglife.global.common.constant.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandService implements ManageReviewUseCase {

    private final LoadReviewPort loadReviewPort;
    private final SaveReviewPort saveReviewPort;
    private final ManagePlaceUseCase managePlaceUseCase;
    private final ManageCourseUseCase manageCourseUseCase;

    @Override
    public ReviewRegisterResponse createReview(Long userId, CreateReviewRequest request) {
        Review review = Review.builder()
                .userId(userId)
                .targetId(request.targetId())
                .targetType(request.targetType())
                .rating(request.rating())
                .content(request.content())
                .build();

        review.attachImages(request.imageUrls());
        saveReviewPort.save(review);
        updateTargetRating(request.targetType(), request.targetId(), request.rating(), true);

        return ReviewRegisterResponse.from(review);
    }

    @Override
    public ReviewRegisterResponse updateReview(Long reviewId, Long userId, UpdateReviewRequest request) {
        Review review = loadReviewPort.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        review.validateOwner(userId);

        Double oldRating = review.getRating();
        review.update(request.rating(), request.content(), request.imageUrls());

        if (!oldRating.equals(request.rating())) {
            updateTargetRating(review.getTargetType(), review.getTargetId(), oldRating, false);
            updateTargetRating(review.getTargetType(), review.getTargetId(), request.rating(), true);
        }

        return ReviewRegisterResponse.from(review);
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) {
        Review review = loadReviewPort.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        review.validateOwner(userId);
        review.delete();
        updateTargetRating(review.getTargetType(), review.getTargetId(), review.getRating(), false);
    }

    private void updateTargetRating(TargetType targetType, Long targetId, Double rating, boolean isAdd) {
        if (targetType == TargetType.PLACE) {
            if (isAdd) managePlaceUseCase.addReview(targetId, rating);
            else managePlaceUseCase.removeReview(targetId, rating);
        } else {
            if (isAdd) manageCourseUseCase.addReview(targetId, rating);
            else manageCourseUseCase.removeReview(targetId, rating);
        }
    }
}
