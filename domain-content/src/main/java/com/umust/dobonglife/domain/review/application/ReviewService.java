package com.umust.dobonglife.domain.review.application;

import com.umust.dobonglife.domain.course.application.CourseService;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.place.application.PlaceService;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.review.application.dto.CreateReviewRequest;
import com.umust.dobonglife.domain.review.application.dto.MyReviewResponse;
import com.umust.dobonglife.domain.review.application.dto.ReviewRegisterResponse;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.application.dto.UpdateReviewRequest;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.domain.entity.ReviewImage;
import com.umust.dobonglife.domain.review.domain.repository.ReviewRepository;
import com.umust.dobonglife.domain.review.exception.ReviewErrorCode;
import com.umust.dobonglife.domain.review.exception.ReviewException;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PlaceService placeService;
    private final CourseService courseService;

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
        updateTargetRating(request.targetType(), request.targetId(), request.rating(), true);

        return ReviewRegisterResponse.from(review);
    }

    @Transactional
    public ReviewRegisterResponse updateReview(Long reviewId, Long userId, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
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

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        review.validateOwner(userId);
        review.delete();
        updateTargetRating(review.getTargetType(), review.getTargetId(), review.getRating(), false);
    }

    private void updateTargetRating(TargetType targetType, Long targetId, Double rating, boolean isAdd) {
        if (targetType == TargetType.PLACE) {
            if (isAdd) placeService.addReview(targetId, rating);
            else placeService.removeReview(targetId, rating);
        } else {
            if (isAdd) courseService.addReview(targetId, rating);
            else courseService.removeReview(targetId, rating);
        }
    }

    public CursorResponse<ReviewSummaryResponse> getReviews(TargetType targetType, Long targetId, Long lastId, int size) {
        List<ReviewSummaryResponse> content = reviewRepository.findReviews(targetType, targetId, lastId, size);
        return CursorUtils.toCursorResponse(content, size);
    }

    public CursorResponse<MyReviewResponse> getMyReviews(Long userId, TargetType targetType, Long lastId, int size) {
        List<MyReviewResponse> content = reviewRepository.findMyReviews(userId, targetType, lastId, size);
        CursorResponse<MyReviewResponse> response = CursorUtils.toCursorResponse(content, size);

        List<Long> targetIds = response.getContent().stream()
                .map(MyReviewResponse::targetId)
                .distinct()
                .toList();

        if (targetType == TargetType.PLACE) {
            Map<Long, Place> placeMap = placeService.getPlacesInBatch(targetIds);
            return CursorUtils.convert(response, r -> {
                Place p = placeMap.get(r.targetId());
                return new MyReviewResponse(
                        r.reviewId(), r.targetId(),
                        p != null ? p.getName() : null,
                        p != null ? p.getThumbnailUrl() : null,
                        r.rating(), r.content(), r.thumbnailUrl(),
                        List.of(), r.updatedAt());
            });
        } else {
            Map<Long, Course> courseMap = courseService.getCoursesInBatch(targetIds);
            return CursorUtils.convert(response, r -> {
                Course c = courseMap.get(r.targetId());
                return new MyReviewResponse(
                        r.reviewId(), r.targetId(),
                        c != null ? c.getTitle() : null,
                        c != null ? c.getThumbnailUrl() : null,
                        r.rating(), r.content(), r.thumbnailUrl(),
                        List.of(), r.updatedAt());
            });
        }
    }
}
