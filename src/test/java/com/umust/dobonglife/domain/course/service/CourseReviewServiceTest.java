package com.umust.dobonglife.domain.course.service;

import com.umust.dobonglife.domain.course.controller.dto.response.CourseDetailResponse;
import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.entity.CoursePlans;
import com.umust.dobonglife.domain.course.domain.repository.CoursePlansRepository;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.course.domain.vo.CourseBasicInfo;
import com.umust.dobonglife.domain.course.domain.vo.CourseReviewStats;
import com.umust.dobonglife.domain.courseLike.service.CourseLikeService;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CourseReviewServiceTest {

    @Mock
    CourseRepository courseRepository;

    @Mock
    UserService userService;

    @Mock
    CourseLikeService courseLikeService;

    @Mock
    ReviewService reviewService;

    @Mock
    CoursePlansRepository coursePlansRepository;

    @InjectMocks
    CourseReviewService courseReviewService;

    private Course createTestCourse(Long id) {
        CourseBasicInfo basicInfo = CourseBasicInfo.builder()
                .title("테스트 코스")
                .subTitle("부제")
                .duration(120L)
                .level(CourseLevel.BEGINNER)
                .build();

        Course course = Course.builder()
                .userId(1L)
                .basicInfo(basicInfo)
                .reviewStats(new CourseReviewStats(4.0, 5L))
                .themes(new ArrayList<>(List.of(CourseTheme.HISTORY)))
                .tags(new ArrayList<>(List.of("역사")))
                .imageUrls(new ArrayList<>(List.of("img.jpg")))
                .build();
        ReflectionTestUtils.setField(course, "id", id);
        return course;
    }

    @Nested
    @DisplayName("getCourse - 코스 상세 조회")
    class GetCourse {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            Long courseId = 10L;
            Course course = createTestCourse(courseId);

            given(courseRepository.findByIdWithDescription(courseId)).willReturn(Optional.of(course));
            given(userService.validateOwner(userId, 1L)).willReturn(true);
            given(courseLikeService.isCourseFavorite(userId, courseId)).willReturn(false);
            given(coursePlansRepository.findByCourseIdOrderByIsOrder(courseId)).willReturn(List.of());
            given(reviewService.getCourseReviews(eq(courseId), eq(userId), isNull(), eq(3)))
                    .willReturn(new CursorResponse<>(List.of(), false));

            // when
            CourseDetailResponse result = courseReviewService.getCourse(userId, courseId, null);

            // then
            assertThat(result.id()).isEqualTo(courseId);
        }

        @Test
        @DisplayName("코스 없음 예외")
        void 코스_없음_예외() {
            // given
            given(courseRepository.findByIdWithDescription(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> courseReviewService.getCourse(1L, 999L, null))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_COURSE_ID);
        }

        @Test
        @DisplayName("좋아요 상태 확인")
        void 좋아요_상태_확인() {
            // given
            Long userId = 1L;
            Long courseId = 10L;
            Course course = createTestCourse(courseId);

            given(courseRepository.findByIdWithDescription(courseId)).willReturn(Optional.of(course));
            given(userService.validateOwner(userId, 1L)).willReturn(false);
            given(courseLikeService.isCourseFavorite(userId, courseId)).willReturn(true);
            given(coursePlansRepository.findByCourseIdOrderByIsOrder(courseId)).willReturn(List.of());
            given(reviewService.getCourseReviews(eq(courseId), eq(userId), isNull(), eq(3)))
                    .willReturn(new CursorResponse<>(List.of(), false));

            // when
            CourseDetailResponse result = courseReviewService.getCourse(userId, courseId, null);

            // then
            assertThat(result.userInfo().liked()).isTrue();
        }

        @Test
        @DisplayName("소유자 확인")
        void 소유자_확인() {
            // given
            Long userId = 1L;
            Long courseId = 10L;
            Course course = createTestCourse(courseId);

            given(courseRepository.findByIdWithDescription(courseId)).willReturn(Optional.of(course));
            given(userService.validateOwner(userId, 1L)).willReturn(true);
            given(courseLikeService.isCourseFavorite(userId, courseId)).willReturn(false);
            given(coursePlansRepository.findByCourseIdOrderByIsOrder(courseId)).willReturn(List.of());
            given(reviewService.getCourseReviews(eq(courseId), eq(userId), isNull(), eq(3)))
                    .willReturn(new CursorResponse<>(List.of(), false));

            // when
            CourseDetailResponse result = courseReviewService.getCourse(userId, courseId, null);

            // then
            assertThat(result.userInfo().isRemoved()).isTrue();
        }
    }
}
