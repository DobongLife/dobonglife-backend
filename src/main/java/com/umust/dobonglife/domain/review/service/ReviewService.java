package com.umust.dobonglife.domain.review.service;

import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.review.domain.constant.ReviewStatus;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.domain.repository.ReviewRepository;
import com.umust.dobonglife.domain.review.presentation.dto.request.CreateReviewRequest;
import com.umust.dobonglife.domain.review.presentation.dto.response.*;
import com.umust.dobonglife.domain.review.service.dto.ReviewItemProjection;
import com.umust.dobonglife.domain.review.service.dto.ReviewStatsDto;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.ErrorCode;
import com.umust.dobonglife.global.common.s3.S3Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final PlaceRepository placeRepository;
    private final S3Utils s3Utils;
    private final UserService userService;

    public CursorResponse<ReviewSummaryResponse> getReviews(Long userId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Review> reviews = reviewRepository.findReviewsNoOffset(lastId, pageable);
        return convertToReviewResponse(userId, reviews);
    }

    public ReviewDetailResponse getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Review 엔티티를 찾을 수 없습니다: " + reviewId));

        return ReviewDetailResponse.from(review);
    }

    private CursorResponse<ReviewSummaryResponse> convertToReviewResponse(Long userId, Slice<Review> reviews) {
        List<ReviewSummaryResponse> content = reviews.getContent().stream()
                .map(review -> ReviewSummaryResponse.from(userId, review))
                .toList();
        return new CursorResponse<>(content, reviews.hasNext());
    }

    public CursorResponse<ReviewSummaryResponse> getMyReviews(Long userId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Review> reviews = reviewRepository.findMyReviewsNoOffset(userId, lastId, pageable);
        return convertToReviewResponse(userId, reviews);
    }

    // TODO: 생성과 수정 중복로직 공통인터페이스로 빼기
    @Transactional
    public ReviewResponse createReview(Long userId, CreateReviewRequest request, List<MultipartFile> imageFiles) {
        List<String> imageUrls = new ArrayList<>();
        if(imageFiles != null && !imageFiles.isEmpty() && !imageFiles.get(0).isEmpty()) {
            imageUrls = s3Utils.uploadImages(imageFiles);
        }

        if(request.courseId() != null){
            updateCourseRatingAndCount(request.courseId(), request.rating());
        }else {
            updatePlaceRatingAndCount(request.placeId(), request.rating());
        }

        Review review = Review.builder()
                .courseId(request.courseId())
                .placeId(request.placeId())
                .user(userService.findById(userId))
                .rating(request.rating())
                .content(request.content())
                .imageUrls(imageUrls)
                .build();
        reviewRepository.save(review);

        return ReviewResponse.from(review);
    }

    public ReviewResponse updateReview(Long reviewId, Long userId, CreateReviewRequest request, List<MultipartFile> imageFiles) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Review 엔티티가 존재하지 않습니다: " + reviewId));

        if(review.getId() != userId) throw new BusinessException(ErrorCode.NOT_REVIEW_OWNER);

        List<String> imageUrls = new ArrayList<>();
        if(imageFiles != null && !imageFiles.isEmpty() && !imageFiles.get(0).isEmpty()) {
            imageUrls = s3Utils.uploadImages(imageFiles);
        }

        if(request.courseId() != null){
            updateCourseRatingAndCount(request.courseId(), request.rating());
        }else {
            updatePlaceRatingAndCount(request.placeId(), request.rating());
        }

        review.update(request, imageUrls); // TODO: 여기서 넣을까, 안에서 넣을까
        reviewRepository.save(review);

        return ReviewResponse.from(review);
    }

    @Transactional
    public void updateCourseRatingAndCount(Long courseId, Double rating) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Course 엔티티를 찾을 수 없습니다: " + courseId));
        course.applyNewReview(rating);
        courseRepository.save(course);
    }


    @Transactional
    public void updatePlaceRatingAndCount(Long placeId, Double rating) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Place 엔티티를 찾을 수 없습니다: " + placeId));
        place.applyNewReview(rating);
        placeRepository.save(place);
    }


}
