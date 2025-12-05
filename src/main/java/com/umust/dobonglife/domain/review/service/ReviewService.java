package com.umust.dobonglife.domain.review.service;

import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.place.model.Place;
import com.umust.dobonglife.domain.place.model.repository.PlaceRepository;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.infrastructure.repository.ReviewRepository;
import com.umust.dobonglife.domain.review.presentation.dto.request.CreateReviewRequest;
import com.umust.dobonglife.domain.review.presentation.dto.response.ReviewResponse;
import com.umust.dobonglife.domain.review.service.dto.ReviewStatsDto;
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
    public ReviewResponse createReview(CreateReviewRequest request) {
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
        ReviewStatsDto stats = new ReviewStatsDto(request.rating(), 1L);

        if(courseId != null){
            updateCourseRatingAndCount(courseId, stats);
            return new ReviewResponse(review.getId(), request.title(), request.content());
        }

        updatePlaceRatingAndCount(placeId, stats);
        return new ReviewResponse(review.getId(), request.title(), request.content());
    }

    @Transactional
    public void updateCourseRatingAndCount(Long courseId, ReviewStatsDto stats) {

        Course course = courseRepository.findById(courseId) // TODO: 조회 방식 고민
                .orElseThrow(() -> new EntityNotFoundException("해당 Course 엔티티를 찾을 수 없습니다: " + courseId));


        Long reviewCount = course.getReviewCount() + stats.reviewCount();
        Double totalRatingSum = course.getAverageRating() + stats.totalRatingSum();

        course.updateRatingInfo(totalRatingSum, reviewCount);
    }

    @Transactional
    public void updatePlaceRatingAndCount(Long placeId, ReviewStatsDto stats) {

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Place 엔티티를 찾을 수 없습니다: " + placeId));

        Long reviewCount = place.getReviewCount() + stats.reviewCount();
        Double totalRatingSum = place.getAverageRating() + stats.totalRatingSum();

        place.updateRatingInfo(totalRatingSum, reviewCount);
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
