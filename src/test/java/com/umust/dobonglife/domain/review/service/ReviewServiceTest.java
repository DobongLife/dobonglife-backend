package com.umust.dobonglife.domain.review.service;

import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.domain.courseLike.service.CourseLikeService;
import com.umust.dobonglife.domain.notification.service.NotificationService;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.domain.review.controller.dto.request.CreateReviewRequest;
import com.umust.dobonglife.domain.review.controller.dto.response.*;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.domain.repository.ReviewRepository;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.external.s3.S3Utils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    CourseService courseService;

    @Mock
    CourseLikeService courseLikeService;

    @Mock
    PlaceService placeService;

    @Mock
    UserService userService;

    @Mock
    PointService pointService;

    @Mock
    S3Utils s3Utils;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    ReviewService reviewService;

    private User createTestUser(Long id) {
        User user = User.builder()
                .id(id)
                .email("test@example.com")
                .name("테스트유저")
                .build();
        return user;
    }

    private Review createTestReview(Long id, Long courseId, Long placeId, User user) {
        Review review = Review.builder()
                .courseId(courseId)
                .placeId(placeId)
                .user(user)
                .rating(4.0)
                .content("좋은 코스입니다")
                .imageUrls(new ArrayList<>(List.of("img1.jpg")))
                .build();
        ReflectionTestUtils.setField(review, "id", id);
        return review;
    }

    @Nested
    @DisplayName("getMyCourseReviews - 내 코스 리뷰 조회")
    class GetMyCourseReviews {

        @Test
        @DisplayName("목록 반환 성공")
        void 목록_반환_성공() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Review review = createTestReview(1L, 10L, null, user);
            SliceImpl<Review> slice = new SliceImpl<>(List.of(review), PageRequest.of(0, 10), false);

            given(reviewRepository.findMyCourseReviewsNoOffset(eq(userId), isNull(), any(Pageable.class)))
                    .willReturn(slice);

            Course course = mock(Course.class);
            given(course.getId()).willReturn(10L);
            given(courseService.findAllById(anyList())).willReturn(List.of(course));
            given(courseLikeService.getFavoriteCourseIds(eq(userId), anyList())).willReturn(Set.of(10L));

            // when
            CursorResponse<CourseReviewSummaryResponse> result = reviewService.getMyCourseReviews(userId, null, 10);

            // then
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("빈 결과 반환")
        void 빈_결과_반환() {
            // given
            Long userId = 1L;
            SliceImpl<Review> emptySlice = new SliceImpl<>(List.of(), PageRequest.of(0, 10), false);

            given(reviewRepository.findMyCourseReviewsNoOffset(eq(userId), isNull(), any(Pageable.class)))
                    .willReturn(emptySlice);

            // when
            CursorResponse<CourseReviewSummaryResponse> result = reviewService.getMyCourseReviews(userId, null, 10);

            // then
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("다음 페이지 있음")
        void 다음_페이지_있음() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Review review = createTestReview(1L, 10L, null, user);
            SliceImpl<Review> slice = new SliceImpl<>(List.of(review), PageRequest.of(0, 1), true);

            given(reviewRepository.findMyCourseReviewsNoOffset(eq(userId), isNull(), any(Pageable.class)))
                    .willReturn(slice);

            Course course = mock(Course.class);
            given(course.getId()).willReturn(10L);
            given(courseService.findAllById(anyList())).willReturn(List.of(course));
            given(courseLikeService.getFavoriteCourseIds(eq(userId), anyList())).willReturn(Collections.emptySet());

            // when
            CursorResponse<CourseReviewSummaryResponse> result = reviewService.getMyCourseReviews(userId, null, 1);

            // then
            assertThat(result.isHasNext()).isTrue();
        }
    }

    @Nested
    @DisplayName("getMyPlaceReviews - 내 장소 리뷰 조회")
    class GetMyPlaceReviews {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Review review = createTestReview(1L, null, 20L, user);
            SliceImpl<Review> slice = new SliceImpl<>(List.of(review), PageRequest.of(0, 10), false);

            given(reviewRepository.findMyPlaceReviewsNoOffset(eq(userId), isNull(), any(Pageable.class)))
                    .willReturn(slice);

            Place place = mock(Place.class);
            given(place.getId()).willReturn(20L);
            given(place.getName()).willReturn("테스트장소");
            given(place.getCategory()).willReturn(com.umust.dobonglife.global.common.model.constant.Category.RESTAURANT);
            given(place.getThumbnailUrl()).willReturn("thumb.jpg");
            given(place.getAverageRating()).willReturn(4.0);
            given(place.getReviewCount()).willReturn(5L);
            given(place.getLatitude()).willReturn(37.0);
            given(place.getLongitude()).willReturn(127.0);
            given(place.getThemes()).willReturn(List.of());
            given(placeService.findAllById(anyList())).willReturn(List.of(place));
            given(placeService.getFavoritePlaceIds(eq(userId), anyList())).willReturn(Collections.emptySet());

            // when
            CursorResponse<PlaceReviewSummaryResponse> result = reviewService.getMyPlaceReviews(userId, null, 10);

            // then
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("빈 결과")
        void 빈_결과() {
            // given
            Long userId = 1L;
            SliceImpl<Review> emptySlice = new SliceImpl<>(List.of(), PageRequest.of(0, 10), false);

            given(reviewRepository.findMyPlaceReviewsNoOffset(eq(userId), isNull(), any(Pageable.class)))
                    .willReturn(emptySlice);

            // when
            CursorResponse<PlaceReviewSummaryResponse> result = reviewService.getMyPlaceReviews(userId, null, 10);

            // then
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("createReview - 리뷰 생성")
    class CreateReview {

        @Test
        @DisplayName("코스 리뷰 생성 성공")
        void 코스_리뷰_생성_성공() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            CreateReviewRequest request = new CreateReviewRequest(10L, null, 4.5, "좋아요");

            given(userService.findById(userId)).willReturn(user);
            given(reviewRepository.save(any(Review.class))).willAnswer(invocation -> {
                Review r = invocation.getArgument(0);
                ReflectionTestUtils.setField(r, "id", 1L);
                return r;
            });

            // when
            ReviewResponse result = reviewService.createReview(userId, request, null);

            // then
            assertThat(result.reviewId()).isEqualTo(1L);
            then(courseService).should().updateCourseRatingAndCount(10L, 4.5, "create");
            then(placeService).should(never()).updatePlaceRatingAndCount(anyLong(), anyDouble(), anyString());
        }

        @Test
        @DisplayName("장소 리뷰 생성 성공")
        void 장소_리뷰_생성_성공() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            CreateReviewRequest request = new CreateReviewRequest(null, 20L, 3.5, "괜찮아요");

            given(userService.findById(userId)).willReturn(user);
            given(reviewRepository.save(any(Review.class))).willAnswer(invocation -> {
                Review r = invocation.getArgument(0);
                ReflectionTestUtils.setField(r, "id", 2L);
                return r;
            });

            // when
            ReviewResponse result = reviewService.createReview(userId, request, null);

            // then
            assertThat(result.reviewId()).isEqualTo(2L);
            then(placeService).should().updatePlaceRatingAndCount(20L, 3.5, "create");
            then(courseService).should(never()).updateCourseRatingAndCount(anyLong(), anyDouble(), anyString());
        }

        @Test
        @DisplayName("이미지 없이 생성")
        void 이미지_없이_생성() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            CreateReviewRequest request = new CreateReviewRequest(10L, null, 4.0, "내용");

            given(userService.findById(userId)).willReturn(user);
            given(reviewRepository.save(any(Review.class))).willAnswer(invocation -> {
                Review r = invocation.getArgument(0);
                ReflectionTestUtils.setField(r, "id", 3L);
                return r;
            });

            // when
            reviewService.createReview(userId, request, null);

            // then
            then(s3Utils).should(never()).uploadImages(any());
        }

        @Test
        @DisplayName("포인트 적립 확인")
        void 포인트_적립_확인() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            CreateReviewRequest request = new CreateReviewRequest(10L, null, 4.0, "좋아요");

            given(userService.findById(userId)).willReturn(user);
            given(reviewRepository.save(any(Review.class))).willAnswer(invocation -> {
                Review r = invocation.getArgument(0);
                ReflectionTestUtils.setField(r, "id", 4L);
                return r;
            });

            // when
            reviewService.createReview(userId, request, null);

            // then
            then(pointService).should().earnPoint(eq(userId), anyString(), eq(10L));
        }
    }

    @Nested
    @DisplayName("updateReview - 리뷰 수정")
    class UpdateReview {

        @Test
        @DisplayName("수정 성공")
        void 수정_성공() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Review review = createTestReview(1L, 10L, null, user);
            CreateReviewRequest request = new CreateReviewRequest(10L, null, 5.0, "수정된 내용");

            given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
            given(reviewRepository.save(any(Review.class))).willReturn(review);

            // when
            ReviewResponse result = reviewService.updateReview(1L, userId, request, null);

            // then
            assertThat(result.reviewId()).isEqualTo(1L);
            then(courseService).should().updateCourseRatingAndCount(10L, 5.0, "modify");
        }

        @Test
        @DisplayName("리뷰 없음 예외")
        void 리뷰_없음_예외() {
            // given
            given(reviewRepository.findById(999L)).willReturn(Optional.empty());
            CreateReviewRequest request = new CreateReviewRequest(10L, null, 4.0, "내용");

            // when & then
            assertThatThrownBy(() -> reviewService.updateReview(999L, 1L, request, null))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("소유자 아님 예외")
        void 소유자_아님_예외() {
            // given
            User owner = createTestUser(1L);
            Review review = createTestReview(1L, 10L, null, owner);
            CreateReviewRequest request = new CreateReviewRequest(10L, null, 4.0, "내용");

            given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

            // when & then
            assertThatThrownBy(() -> reviewService.updateReview(1L, 2L, request, null))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.NOT_REVIEW_OWNER);
        }

        @Test
        @DisplayName("코스 평점 업데이트 확인")
        void 코스_평점_업데이트_확인() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Review review = createTestReview(1L, 10L, null, user);
            CreateReviewRequest request = new CreateReviewRequest(10L, null, 3.0, "수정");

            given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
            given(reviewRepository.save(any(Review.class))).willReturn(review);

            // when
            reviewService.updateReview(1L, userId, request, null);

            // then
            then(courseService).should().updateCourseRatingAndCount(10L, 3.0, "modify");
        }
    }

    @Nested
    @DisplayName("deleteReview - 리뷰 삭제")
    class DeleteReview {

        @Test
        @DisplayName("코스 리뷰 삭제 성공")
        void 코스_리뷰_삭제_성공() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Review review = createTestReview(1L, 10L, null, user);

            given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

            // when
            ReviewResponse result = reviewService.deleteReview(1L, userId);

            // then
            assertThat(result.reviewId()).isEqualTo(1L);
            then(courseService).should().deleteCourseReview(userId, 10L, 4.0);
            then(reviewRepository).should().delete(review);
            then(userService).should().handleDeletion(userId);
        }

        @Test
        @DisplayName("장소 리뷰 삭제 성공")
        void 장소_리뷰_삭제_성공() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Review review = createTestReview(1L, null, 20L, user);

            given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

            // when
            ReviewResponse result = reviewService.deleteReview(1L, userId);

            // then
            assertThat(result.reviewId()).isEqualTo(1L);
            then(placeService).should().deletePlaceReview(20L, 4.0);
            then(reviewRepository).should().delete(review);
        }

        @Test
        @DisplayName("리뷰 없음 예외")
        void 리뷰_없음_예외() {
            // given
            given(reviewRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reviewService.deleteReview(999L, 1L))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("소유자 아님 예외")
        void 소유자_아님_예외() {
            // given
            User owner = createTestUser(1L);
            Review review = createTestReview(1L, 10L, null, owner);

            given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

            // when & then
            assertThatThrownBy(() -> reviewService.deleteReview(1L, 2L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.SECURITY_ACCESS_DENIED);
        }
    }

    @Nested
    @DisplayName("getCourseReviews - 코스 리뷰 목록 조회")
    class GetCourseReviews {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Review review = createTestReview(1L, 10L, null, user);
            SliceImpl<Review> slice = new SliceImpl<>(List.of(review), PageRequest.of(0, 10), false);

            given(reviewRepository.findCourseReviewsNoOffset(eq(10L), isNull(), any(Pageable.class)))
                    .willReturn(slice);

            // when
            CursorResponse<ReviewSummaryResponse> result = reviewService.getCourseReviews(10L, userId, null, 10);

            // then
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("빈 결과")
        void 빈_결과() {
            // given
            SliceImpl<Review> emptySlice = new SliceImpl<>(List.of(), PageRequest.of(0, 10), false);

            given(reviewRepository.findCourseReviewsNoOffset(eq(10L), isNull(), any(Pageable.class)))
                    .willReturn(emptySlice);

            // when
            CursorResponse<ReviewSummaryResponse> result = reviewService.getCourseReviews(10L, 1L, null, 10);

            // then
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getPlaceReviews - 장소 리뷰 목록 조회")
    class GetPlaceReviews {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Review review = createTestReview(1L, null, 20L, user);
            SliceImpl<Review> slice = new SliceImpl<>(List.of(review), PageRequest.of(0, 10), false);

            given(reviewRepository.findPlaceReviewsNoOffset(eq(20L), isNull(), any(Pageable.class)))
                    .willReturn(slice);

            // when
            CursorResponse<ReviewSummaryResponse> result = reviewService.getPlaceReviews(20L, userId, null, 10);

            // then
            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getWrittenReviewCount - 작성 리뷰 수 조회")
    class GetWrittenReviewCount {

        @Test
        @DisplayName("카운트 반환")
        void 카운트_반환() {
            // given
            given(reviewRepository.countByUserId(1L)).willReturn(5);

            // when
            int result = reviewService.getWrittenReviewCount(1L);

            // then
            assertThat(result).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("deleteByUserId - 사용자 리뷰 전체 삭제")
    class DeleteByUserId {

        @Test
        @DisplayName("호출 확인")
        void 호출_확인() {
            // when
            reviewService.deleteByUserId(1L);

            // then
            then(reviewRepository).should().deleteByUserId(1L);
        }
    }
}
