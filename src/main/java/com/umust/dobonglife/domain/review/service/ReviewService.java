package com.umust.dobonglife.domain.review.service;

import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.domain.repository.ReviewRepository;
import com.umust.dobonglife.domain.review.presentation.dto.request.CreateReviewRequest;
import com.umust.dobonglife.domain.review.presentation.dto.response.MyReviewsScreenResponse;
import com.umust.dobonglife.domain.review.presentation.dto.response.ReviewItem;
import com.umust.dobonglife.domain.review.presentation.dto.response.ReviewResponse;
import com.umust.dobonglife.domain.review.service.dto.ReviewItemProjection;
import com.umust.dobonglife.domain.review.service.dto.ReviewStatsDto;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.ErrorCode;
import com.umust.dobonglife.global.common.s3.S3Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
                .userId(userId)
                .rating(request.rating())
                .content(request.content())
                .imageUrls(imageUrls)
                .build();
        reviewRepository.save(review);

        return ReviewResponse.from(review);
    }

    public ReviewResponse updateReview(Long reviewId, Long userId, CreateReviewRequest request, List<MultipartFile> imageFiles) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰 엔티티가 존재하지 않습니다."));

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
