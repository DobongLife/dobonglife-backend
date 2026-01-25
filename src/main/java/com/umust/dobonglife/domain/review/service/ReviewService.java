package com.umust.dobonglife.domain.review.service;

import com.umust.dobonglife.domain.coupon.service.CouponService;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.domain.point.domain.vo.PointPolicy;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.domain.repository.ReviewRepository;
import com.umust.dobonglife.domain.review.controller.dto.request.CreateReviewRequest;
import com.umust.dobonglife.domain.review.controller.dto.response.*;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.external.s3.S3Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.umust.dobonglife.domain.point.domain.vo.PointPolicy.REVIEW_CREATE;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseService courseService;
    private final PlaceService placeService;
    private final UserService userService;
    private final PointService pointService;
    private final S3Utils s3Utils;

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

    @Transactional
    public ReviewResponse createReview(Long userId, CreateReviewRequest request, List<MultipartFile> imageFiles) {
        List<String> imageUrls = getStrings(imageFiles);

        if(request.courseId() != null){
            courseService.updateCourseRatingAndCount(request.courseId(), request.rating());
        }else {
            placeService.updatePlaceRatingAndCount(request.placeId(), request.rating());
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
        pointService.earnPoint(userService.findById(userId), REVIEW_CREATE.getTitle(), REVIEW_CREATE.getPoint());
        userService.updatePoint(userId, REVIEW_CREATE.getPoint());

        return ReviewResponse.from(review);
    }

    private List<String> getStrings(List<MultipartFile> imageFiles) {
        List<String> imageUrls = new ArrayList<>();
        if(imageFiles != null && !imageFiles.isEmpty() && !imageFiles.get(0).isEmpty()) {
            imageUrls = s3Utils.uploadImages(imageFiles);
        }
        return imageUrls;
    }

    public ReviewResponse updateReview(Long reviewId, Long userId, CreateReviewRequest request, List<MultipartFile> imageFiles) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Review 엔티티가 존재하지 않습니다: " + reviewId));

        if(!review.getUser().getId().equals(userId)) throw new BusinessException(ErrorCode.NOT_REVIEW_OWNER);
        List<String> imageUrls = getStrings(imageFiles);

        if(request.courseId() != null){
            courseService.updateCourseRatingAndCount(request.courseId(), request.rating());
        }else {
            placeService.updatePlaceRatingAndCount(request.placeId(), request.rating());
        }

        review.update(request.courseId(), request.placeId(), request.rating(), request.content(), imageUrls);
        reviewRepository.save(review);

        return ReviewResponse.from(review);
    }

    public int getWrittenReviewCount(Long userId) {
        return reviewRepository.countByUserId(userId);
    }

    public CursorResponse<ReviewSummaryResponse> getCourseReviews(Long courseId, Long userId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Review> reviews = reviewRepository.findCourseReviewsNoOffset(courseId, lastId, pageable);
        return convertToReviewResponse(userId, reviews);
    }

    public CursorResponse<ReviewSummaryResponse> getPlaceReviews(Long placeId, Long userId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Review> reviews = reviewRepository.findPlaceReviewsNoOffset(placeId, lastId, pageable);
        return convertToReviewResponse(userId, reviews);
    }

    public ReviewResponse deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Review 엔티티가 존재하지 않습니다: " + reviewId));

        if(!review.getUser().getId().equals(userId))
            throw new BusinessException(ErrorCode.SECURITY_ACCESS_DENIED);

        if(review.getCourseId() != null){
            courseService.deleteCourseReview(userId ,review.getCourseId(), review.getRating());
        }else{
            placeService.deletePlaceReview(review.getPlaceId(), review.getRating());
        }

        reviewRepository.delete(review);
        userService.handleDeletion(userId);
        return ReviewResponse.from(review);
    }
}
