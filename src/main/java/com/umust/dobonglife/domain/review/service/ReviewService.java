package com.umust.dobonglife.domain.review.service;

import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
<<<<<<< Updated upstream
import com.umust.dobonglife.domain.place.model.Place;
import com.umust.dobonglife.domain.place.model.repository.PlaceRepository;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.domain.repository.ReviewRepository;
import com.umust.dobonglife.domain.review.presentation.dto.request.CreateReviewRequest;
import com.umust.dobonglife.domain.review.presentation.dto.response.MyReviewsScreenResponse;
import com.umust.dobonglife.domain.review.presentation.dto.response.ReviewItem;
import com.umust.dobonglife.domain.review.presentation.dto.response.ReviewResponse;
import com.umust.dobonglife.domain.review.service.dto.ReviewItemProjection;
import com.umust.dobonglife.domain.review.service.dto.ReviewStatsDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

=======
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

>>>>>>> Stashed changes
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final PlaceRepository placeRepository;

<<<<<<< Updated upstream
    public MyReviewsScreenResponse getMyReviewManagementData(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        long reviewNum = reviewRepository.findByUserId(userId).stream().count();
        List<ReviewItem> reviewList = getMockReviewItems(userId, pageable);

        return new MyReviewsScreenResponse(reviewNum, reviewList);
    }
    private List<ReviewItem> getMockReviewItems(Long userId, Pageable pageable) {
        // Fetch Join을 사용한 N+1 문제 방지
        Page<ReviewItemProjection> reviewPage = reviewRepository.findReviewItemsByUserId(userId, pageable);

        return reviewPage.getContent().stream()
                .map(projection -> new ReviewItem(
                        projection.getReviewId(),
                        projection.getCourseName(),
                        projection.getPlaceName(),
                        projection.getRating(),
                        projection.getContentSummary(),
                        projection.getWrittenDate(),
                        projection.getLikeCount(),
                        projection.getImageCount(),
                        projection.getThumbnailUrl()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse createReview(Long userId, CreateReviewRequest request) {
=======
    @Transactional
    public Long createReview(CreateReviewRequest request) {
>>>>>>> Stashed changes
        Long courseId = request.courseId();
        Long placeId = request.placeId();

        validateRating(request.rating());
        validateTargetId(courseId, placeId);

        Review review = Review.create(
                courseId,
                placeId,
<<<<<<< Updated upstream
                userId,
=======
>>>>>>> Stashed changes
                request.rating(),
                request.title(),
                request.content(),
                request.imageUrls()
        );
        review = reviewRepository.save(review);
<<<<<<< Updated upstream
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
=======

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
>>>>>>> Stashed changes

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Place 엔티티를 찾을 수 없습니다: " + placeId));

<<<<<<< Updated upstream
        Long reviewCount = place.getReviewCount() + stats.reviewCount();
        Double totalRatingSum = place.getAverageRating() + stats.totalRatingSum();

        place.updateRatingInfo(totalRatingSum, reviewCount);
=======
        place.updateRatingInfo(averageRating, reviewCount);
>>>>>>> Stashed changes
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
