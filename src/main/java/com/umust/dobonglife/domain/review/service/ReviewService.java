package com.umust.dobonglife.domain.review.service;

import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.course.controller.dto.ReviewStatsDto;
import com.umust.dobonglife.domain.place.model.Place;
import com.umust.dobonglife.domain.place.model.repository.PlaceRepository;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.infrastructure.repository.ReviewRepository;
import com.umust.dobonglife.domain.review.presentation.dto.request.CreateReviewRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public Long createReview(CreateReviewRequest request) {
        Long courseId = request.courseId();
        Long placeId = request.placeId();

        validateRating(request.rating());
        validateTargetId(courseId, placeId);

        Review review = Review.create(
                courseId,
                placeId,
                request.rating(),
                request.title(),
                request.content(),
                request.imageUrls()
        );
        review = reviewRepository.save(review);

        if(courseId != null){
            updateCourseRatingAndCount(courseId);
            return review.getId();
        }

        updatePlaceRatingAndCount(placeId);
        return review.getId();
    }

    @Transactional
    public void updateCourseRatingAndCount(Long courseId) {

        ReviewStatsDto stats = reviewRepository.getReviewStatsByCourseId(courseId);

        Long reviewCount = stats.reviewCount();
        Double totalRatingSum = stats.totalRatingSum();

        Double averageRating = 0.0;
        if (reviewCount > 0) {
            averageRating = totalRatingSum / reviewCount;
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Course 엔티티를 찾을 수 없습니다: " + courseId));

        course.updateRatingInfo(averageRating, reviewCount);
    }

    @Transactional
    public void updatePlaceRatingAndCount(Long placeId) {

        ReviewStatsDto stats = reviewRepository.getReviewStatsByPlaceId(placeId);

        Long reviewCount = stats.reviewCount();
        Double totalRatingSum = stats.totalRatingSum();

        Double averageRating = 0.0;
        if (reviewCount > 0) {
            averageRating = totalRatingSum / reviewCount;
        }

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Place 엔티티를 찾을 수 없습니다: " + placeId));

        place.updateRatingInfo(averageRating, reviewCount);
    }

    private void validateRating(Double rating) {
        if (rating != null && (rating < 0.0 || rating > 5.0)) {
            throw new IllegalArgumentException("평점은 0.0 이상 5.0 이하여야 합니다.");
        }
    }

    private void validateTargetId(Long courseId, Long placeId) {
        if (courseId == null && placeId == null) {
            throw new IllegalArgumentException("리뷰 작성 대상인 Course ID 또는 Place ID 중 하나는 필수입니다.");
        }
    }
}
