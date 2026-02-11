package com.umust.dobonglife.domain.review.service;

import com.umust.dobonglife.domain.coupon.service.CouponService;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.domain.courseLike.service.CourseLikeService;
import com.umust.dobonglife.domain.notification.domain.constant.NotificationType;
import com.umust.dobonglife.domain.notification.service.NotificationService;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceSummaryResponse;
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
import com.umust.dobonglife.global.common.response.CursorUtils;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.external.s3.S3Utils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.umust.dobonglife.domain.point.domain.vo.PointPolicy.COURSE_CREATE;
import static com.umust.dobonglife.domain.point.domain.vo.PointPolicy.REVIEW_CREATE;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseService courseService;
    private final CourseLikeService courseLikeService;
    private final PlaceService placeService;
    private final UserService userService;
    private final PointService pointService;
    private final S3Utils s3Utils;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public CursorResponse<CourseReviewSummaryResponse> getMyCourseReviews(Long userId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Review> reviews = reviewRepository.findMyCourseReviewsNoOffset(userId, lastId, pageable);

        return convertToCourseReviewResponse(userId, reviews);
    }

    @Transactional(readOnly = true)
    public CursorResponse<PlaceReviewSummaryResponse> getMyPlaceReviews(Long userId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Review> reviews = reviewRepository.findMyPlaceReviewsNoOffset(userId, lastId, pageable);

        return convertToPlaceReviewResponse(userId, reviews);
    }

    private CursorResponse<CourseReviewSummaryResponse> convertToCourseReviewResponse(Long userId, Slice<Review> reviews) {
        // 코스 ID 추출
        List<Long> courseIds = reviews.getContent().stream()
                .map(Review::getCourseId)
                .filter(id -> id != null)
                .distinct()
                .toList();

        // 코스 정보 일괄 조회
        Map<Long, Course> courseMap = courseIds.isEmpty()
                ? Collections.emptyMap()
                : courseService.findAllById(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, course -> course));

        // 좋아요 정보 조회
        Set<Long> favoriteCourseIds = !courseIds.isEmpty()
                ? courseLikeService.getFavoriteCourseIds(userId, courseIds)
                : Collections.emptySet();

        return CursorUtils.toCursorResponse(
                reviews,
                review -> {
                    Course course = courseMap.get(review.getCourseId());
                    CourseSummaryResponse courseInfo = course != null
                            ? CourseSummaryResponse.of(course, favoriteCourseIds.contains(course.getId()))
                            : null;
                    return CourseReviewSummaryResponse.from(userId, review, courseInfo);
                }
        );
    }

    private CursorResponse<PlaceReviewSummaryResponse> convertToPlaceReviewResponse(Long userId, Slice<Review> reviews) {
        // 장소 ID 추출
        List<Long> placeIds = reviews.getContent().stream()
                .map(Review::getPlaceId)
                .filter(id -> id != null)
                .distinct()
                .toList();

        // 장소 정보 일괄 조회
        Map<Long, Place> placeMap = placeIds.isEmpty()
                ? Collections.emptyMap()
                : placeService.findAllById(placeIds).stream()
                .collect(Collectors.toMap(Place::getId, place -> place));

        // 좋아요 정보 조회
        Set<Long> favoritePlaceIds = !placeIds.isEmpty()
                ? placeService.getFavoritePlaceIds(userId, placeIds)
                : Collections.emptySet();

        return CursorUtils.toCursorResponse(
                reviews,
                review -> {
                    Place place = placeMap.get(review.getPlaceId());

                    PlaceSummaryResponse placeInfo = null;
                    if (place != null) {
                        boolean isLiked = favoritePlaceIds.contains(place.getId());
                        placeInfo = PlaceSummaryResponse.from(place, isLiked, place.getThemes());
                    }

                    return PlaceReviewSummaryResponse.from(userId, review, placeInfo);
                }
        );
    }

    @Transactional
    public ReviewResponse createReview(Long userId, CreateReviewRequest request, List<MultipartFile> imageFiles) {
        List<String> imageUrls = getStrings(imageFiles);

        if(request.courseId() != null){
            courseService.updateCourseRatingAndCount(request.courseId(), request.rating(), "create");
        }else {
            placeService.updatePlaceRatingAndCount(request.placeId(), request.rating(), "create");
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
        pointService.earnPoint(userId, REVIEW_CREATE.getTitle(), REVIEW_CREATE.getPoint());
        notificationService.createNotification(userService.findById(userId),
                NotificationType.POINT,
                "포인트 적립 안내",
                REVIEW_CREATE.getPoint() + "포인트가 적립되었습니다! (리뷰등록)",
                null);

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
            courseService.updateCourseRatingAndCount(request.courseId(), request.rating(), "modify");
        }else {
            placeService.updatePlaceRatingAndCount(request.placeId(), request.rating(), "modify");
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

    private CursorResponse<ReviewSummaryResponse> convertToReviewResponse(Long userId, Slice<Review> reviews) {
        List<ReviewSummaryResponse> content = reviews.getContent().stream()
                .map(review -> ReviewSummaryResponse.from(userId, review))
                .toList();
        return new CursorResponse<>(content, reviews.hasNext());
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

    @Transactional
    public void deleteByPlaceId(Long placeId) {
        reviewRepository.deleteByPlaceId(placeId);
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        reviewRepository.deleteByUserId(userId);
    }
}
