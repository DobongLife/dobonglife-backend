package com.umust.dobonglife.domain.course.service;

import com.umust.dobonglife.domain.course.controller.dto.request.CoursePlanRequest;
import com.umust.dobonglife.domain.course.controller.dto.request.CreateCourseRequest;
import com.umust.dobonglife.domain.course.controller.dto.request.UpdateCourseRequest;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseDeleteResponse;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseMyResponse;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseRegisterResponse;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.entity.CourseDescription;
import com.umust.dobonglife.domain.course.domain.entity.CoursePlans;
import com.umust.dobonglife.domain.course.domain.repository.CoursePlansRepository;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.course.domain.vo.CourseBasicInfo;
import com.umust.dobonglife.domain.course.domain.vo.CourseReviewStats;
import com.umust.dobonglife.domain.courseLike.service.CourseLikeService;
import com.umust.dobonglife.domain.notification.service.NotificationService;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.external.s3.S3Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CoursePlansRepository coursePlansRepository;

    @Mock
    private S3Utils s3Utils;

    @Mock
    private UserService userService;

    @Mock
    private PointService pointService;

    @Mock
    private CourseLikeService courseLikeService;

    @Mock
    private NotificationService notificationService;

    private Course createTestCourse(Long id) {
        CourseBasicInfo basicInfo = CourseBasicInfo.builder()
                .title("테스트 코스")
                .subTitle("테스트 부제목")
                .duration(180L)
                .level(CourseLevel.BEGINNER)
                .build();

        Course course = Course.builder()
                .userId(1L)
                .basicInfo(basicInfo)
                .reviewStats(new CourseReviewStats(4.0, 5L))
                .themes(List.of(CourseTheme.HISTORY))
                .tags(List.of("역사"))
                .imageUrls(new ArrayList<>(List.of("img1.jpg", "img2.jpg")))
                .build();

        ReflectionTestUtils.setField(course, "id", id);
        return course;
    }

    private Course createTestCourseWithDescription(Long id) {
        CourseBasicInfo basicInfo = CourseBasicInfo.builder()
                .title("테스트 코스")
                .subTitle("테스트 부제목")
                .duration(180L)
                .level(CourseLevel.BEGINNER)
                .build();

        CourseDescription description = CourseDescription.builder()
                .content("상세 설명 내용입니다")
                .highlights(List.of("하이라이트1"))
                .build();

        Course course = Course.builder()
                .userId(1L)
                .basicInfo(basicInfo)
                .reviewStats(new CourseReviewStats(4.0, 5L))
                .themes(new ArrayList<>(List.of(CourseTheme.HISTORY)))
                .tags(new ArrayList<>(List.of("역사")))
                .imageUrls(new ArrayList<>(List.of("img1.jpg", "img2.jpg")))
                .description(description)
                .build();

        ReflectionTestUtils.setField(course, "id", id);
        return course;
    }

    private CreateCourseRequest createCourseRequest() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("도봉구 역사 탐방 코스");
        request.setSubTitle("숨겨진 역사를 찾아서");
        request.setThemes(List.of(CourseTheme.HISTORY));
        request.setDuration(180L);
        request.setLevel("BEGINNER");
        request.setTags(List.of("역사", "문화"));
        request.setContent("도봉구의 역사적 장소를 둘러보는 코스입니다. 조선시대부터 현대까지의 흔적을 따라가며...");
        request.setHighlights(List.of("조선시대 유적 탐방"));

        CoursePlanRequest plan = new CoursePlanRequest();
        plan.setPlaceId(1L);
        plan.setOrder(1L);
        plan.setTitle("도봉서원 방문");
        plan.setContent("조선시대 서원의 건축양식과 역사를 살펴봅니다");
        request.setPlans(List.of(plan));

        return request;
    }

    @Nested
    @DisplayName("getCourses - 코스 목록 조회")
    class GetCourses {

        @Test
        @DisplayName("로그인 사용자 코스 목록 조회 성공")
        void success() {
            // given
            Long userId = 1L;
            Course course = createTestCourse(1L);
            SliceImpl<Course> slice = new SliceImpl<>(List.of(course));

            given(courseRepository.findCoursesNoOffset(isNull(), any(Pageable.class)))
                    .willReturn(slice);
            given(courseLikeService.getFavoriteCourseIds(eq(userId), anyList()))
                    .willReturn(Set.of(1L));

            // when
            CursorResponse<CourseSummaryResponse> result = courseService.getCourses(userId, null, 10);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).courseId()).isEqualTo(1L);
            assertThat(result.getContent().get(0).liked()).isTrue();
        }

        @Test
        @DisplayName("빈 결과 반환")
        void emptyResult() {
            // given
            SliceImpl<Course> emptySlice = new SliceImpl<>(List.of());

            given(courseRepository.findCoursesNoOffset(isNull(), any(Pageable.class)))
                    .willReturn(emptySlice);

            // when
            CursorResponse<CourseSummaryResponse> result = courseService.getCourses(1L, null, 10);

            // then
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("비로그인 사용자 (userId=null) 조회")
        void anonymousUser() {
            // given
            Course course = createTestCourse(1L);
            SliceImpl<Course> slice = new SliceImpl<>(List.of(course));

            given(courseRepository.findCoursesNoOffset(isNull(), any(Pageable.class)))
                    .willReturn(slice);

            // when
            CursorResponse<CourseSummaryResponse> result = courseService.getCourses(null, null, 10);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).liked()).isFalse();
            then(courseLikeService).should(never()).getFavoriteCourseIds(any(), anyList());
        }
    }

    @Nested
    @DisplayName("getCourses(theme) - 테마별 코스 조회")
    class GetCoursesByTheme {

        @Test
        @DisplayName("테마별 코스 조회 성공")
        void success() {
            // given
            Course course = createTestCourse(1L);
            SliceImpl<Course> slice = new SliceImpl<>(List.of(course));

            given(courseRepository.findByThemeNoOffset(eq(CourseTheme.HISTORY), isNull(), any(Pageable.class)))
                    .willReturn(slice);
            given(courseLikeService.getFavoriteCourseIds(eq(1L), anyList()))
                    .willReturn(Collections.emptySet());

            // when
            CursorResponse<CourseSummaryResponse> result = courseService.getCourses(1L, CourseTheme.HISTORY, null, 10);

            // then
            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getMyCourses - 내 코스 조회")
    class GetMyCourses {

        @Test
        @DisplayName("내 코스 조회 성공")
        void success() {
            // given
            Long userId = 1L;
            Course course = createTestCourse(1L);
            SliceImpl<Course> slice = new SliceImpl<>(List.of(course));

            given(courseRepository.findMyCoursesNoOffset(eq(userId), isNull(), any(Pageable.class)))
                    .willReturn(slice);
            given(courseRepository.countByUserId(userId)).willReturn(1L);
            given(courseLikeService.getFavoriteCourseIds(eq(userId), anyList()))
                    .willReturn(Collections.emptySet());

            // when
            CourseMyResponse result = courseService.getMyCourses(null, 10, userId);

            // then
            assertThat(result.totalCount()).isEqualTo(1L);
            assertThat(result.course().getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("createCourse - 코스 생성")
    class CreateCourse {

        @Test
        @DisplayName("이미지 포함 코스 생성 성공")
        void successWithImages() {
            // given
            Long userId = 1L;
            CreateCourseRequest request = createCourseRequest();
            MultipartFile mockFile = mock(MultipartFile.class);
            given(mockFile.isEmpty()).willReturn(false);
            List<MultipartFile> files = List.of(mockFile);

            given(s3Utils.uploadImages(files)).willReturn(List.of("uploaded.jpg"));
            given(courseRepository.save(any(Course.class))).willAnswer(invocation -> {
                Course c = invocation.getArgument(0);
                ReflectionTestUtils.setField(c, "id", 1L);
                return c;
            });
            given(coursePlansRepository.saveAll(anyList())).willReturn(List.of());
            given(userService.findById(userId)).willReturn(mock(User.class));

            // when
            CourseRegisterResponse result = courseService.createCourse(userId, request, files);

            // then
            assertThat(result.courseId()).isEqualTo(1L);
            assertThat(result.title()).isEqualTo("도봉구 역사 탐방 코스");
            then(s3Utils).should().uploadImages(files);
            then(pointService).should().earnPoint(eq(userId), anyString(), anyLong());
        }

        @Test
        @DisplayName("이미지 없이 코스 생성 성공")
        void successWithoutImages() {
            // given
            Long userId = 1L;
            CreateCourseRequest request = createCourseRequest();

            given(courseRepository.save(any(Course.class))).willAnswer(invocation -> {
                Course c = invocation.getArgument(0);
                ReflectionTestUtils.setField(c, "id", 2L);
                return c;
            });
            given(coursePlansRepository.saveAll(anyList())).willReturn(List.of());
            given(userService.findById(userId)).willReturn(mock(User.class));

            // when
            CourseRegisterResponse result = courseService.createCourse(userId, request, null);

            // then
            assertThat(result.courseId()).isEqualTo(2L);
            then(s3Utils).should(never()).uploadImages(any());
        }

        @Test
        @DisplayName("코스 생성 실패 시 업로드된 S3 이미지 정리")
        void cleanupS3OnFailure() {
            // given
            Long userId = 1L;
            CreateCourseRequest request = createCourseRequest();
            MultipartFile mockFile = mock(MultipartFile.class);
            given(mockFile.isEmpty()).willReturn(false);
            List<MultipartFile> files = List.of(mockFile);

            given(s3Utils.uploadImages(files)).willReturn(new ArrayList<>(List.of("uploaded.jpg")));
            given(courseRepository.save(any(Course.class))).willThrow(new RuntimeException("DB 오류"));

            // when & then
            assertThatThrownBy(() -> courseService.createCourse(userId, request, files))
                    .isInstanceOf(BusinessException.class);
            then(s3Utils).should().deleteImages(anyList());
        }
    }

    @Nested
    @DisplayName("updateCourse - 코스 수정")
    class UpdateCourse {

        @Test
        @DisplayName("코스 수정 성공")
        void success() {
            try (MockedStatic<TransactionSynchronizationManager> mockedTxManager =
                         mockStatic(TransactionSynchronizationManager.class)) {
                // given
                Long userId = 1L;
                Long courseId = 1L;
                Course course = createTestCourseWithDescription(courseId);

                UpdateCourseRequest request = new UpdateCourseRequest(
                        "수정된 제목", "수정된 부제목", List.of("img1.jpg"),
                        List.of(CourseTheme.CULTURE), 120L, "INTERMEDIATE",
                        List.of("새태그"), "수정된 내용입니다. 최소 10자 이상.", List.of("새 하이라이트"), null
                );

                given(courseRepository.findByIdWithDescription(courseId)).willReturn(Optional.of(course));
                given(userService.validateOwner(userId, course.getUserId())).willReturn(true);

                // when
                CourseRegisterResponse result = courseService.updateCourse(userId, courseId, request, null);

                // then
                assertThat(result.courseId()).isEqualTo(courseId);
            }
        }

        @Test
        @DisplayName("소유자가 아니면 NOT_OWNER 예외")
        void notOwner() {
            // given
            Long userId = 2L;
            Long courseId = 1L;
            Course course = createTestCourseWithDescription(courseId);

            given(courseRepository.findByIdWithDescription(courseId)).willReturn(Optional.of(course));
            given(userService.validateOwner(userId, course.getUserId())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> courseService.updateCourse(userId, courseId,
                    new UpdateCourseRequest("제목", null, null, List.of(CourseTheme.HISTORY), 180L, "BEGINNER", null, "내용입니다. 최소 10자 이상.", null, null),
                    null))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.NOT_OWNER);
        }

        @Test
        @DisplayName("존재하지 않는 코스 수정 시 INVALID_COURSE_ID 예외")
        void courseNotFound() {
            // given
            given(courseRepository.findByIdWithDescription(99L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> courseService.updateCourse(1L, 99L,
                    new UpdateCourseRequest("제목", null, null, List.of(CourseTheme.HISTORY), 180L, "BEGINNER", null, "내용입니다. 최소 10자 이상.", null, null),
                    null))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_COURSE_ID);
        }
    }

    @Nested
    @DisplayName("deleteCourse - 코스 삭제")
    class DeleteCourse {

        @Test
        @DisplayName("코스 삭제 성공")
        void success() {
            try (MockedStatic<TransactionSynchronizationManager> mockedTxManager =
                         mockStatic(TransactionSynchronizationManager.class)) {
                // given
                Long userId = 1L;
                Long courseId = 1L;
                Course course = createTestCourse(courseId);

                given(courseRepository.findById(courseId)).willReturn(Optional.of(course));
                given(userService.validateOwner(userId, course.getUserId())).willReturn(true);

                // when
                CourseDeleteResponse result = courseService.deleteCourse(userId, courseId);

                // then
                assertThat(result.courseId()).isEqualTo(courseId);
                then(coursePlansRepository).should().deleteByCourseId(courseId);
                then(courseRepository).should().delete(course);
                then(userService).should().handleDeletion(userId);
            }
        }

        @Test
        @DisplayName("소유자가 아니면 NOT_OWNER 예외")
        void notOwner() {
            // given
            Long userId = 2L;
            Long courseId = 1L;
            Course course = createTestCourse(courseId);

            given(courseRepository.findById(courseId)).willReturn(Optional.of(course));
            given(userService.validateOwner(userId, course.getUserId())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> courseService.deleteCourse(userId, courseId))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.NOT_OWNER);
        }

        @Test
        @DisplayName("존재하지 않는 코스 삭제 시 INVALID_COURSE_ID 예외")
        void courseNotFound() {
            // given
            given(courseRepository.findById(99L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> courseService.deleteCourse(1L, 99L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_COURSE_ID);
        }
    }

    @Nested
    @DisplayName("updateCourseRatingAndCount - 리뷰 반영")
    class UpdateCourseRatingAndCount {

        @Test
        @DisplayName("create 모드 - 새 리뷰 반영")
        void createMode() {
            // given
            Long courseId = 1L;
            Course course = createTestCourse(courseId);

            given(courseRepository.findById(courseId)).willReturn(Optional.of(course));

            // when
            courseService.updateCourseRatingAndCount(courseId, 5.0, "create");

            // then
            assertThat(course.getReviewCount()).isEqualTo(6L);
        }

        @Test
        @DisplayName("modify 모드 - 평점 업데이트")
        void modifyMode() {
            // given
            Long courseId = 1L;
            Course course = createTestCourse(courseId);

            given(courseRepository.findById(courseId)).willReturn(Optional.of(course));

            // when
            courseService.updateCourseRatingAndCount(courseId, 3.0, "modify");

            // then
            assertThat(course.getReviewCount()).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("deleteCourseReview - 코스 리뷰 삭제")
    class DeleteCourseReview {

        @Test
        @DisplayName("코스 리뷰 삭제 성공")
        void success() {
            // given
            Long courseId = 1L;
            Course course = createTestCourse(courseId);

            given(courseRepository.findById(courseId)).willReturn(Optional.of(course));

            // when
            courseService.deleteCourseReview(1L, courseId, 4.0);

            // then
            assertThat(course.getReviewCount()).isEqualTo(4L);
        }
    }

    @Nested
    @DisplayName("getLikedCourse - 좋아요한 코스 조회")
    class GetLikedCourse {

        @Test
        @DisplayName("좋아요한 코스 조회 성공")
        void success() {
            // given
            Long userId = 1L;
            Course course = createTestCourse(1L);
            SliceImpl<Course> slice = new SliceImpl<>(List.of(course));

            given(courseRepository.findLikedCourses(eq(userId), isNull(), any(Pageable.class)))
                    .willReturn(slice);
            given(courseLikeService.getFavoriteCourseIds(eq(userId), anyList()))
                    .willReturn(Set.of(1L));

            // when
            CursorResponse<CourseSummaryResponse> result = courseService.getLikedCourse(userId, 10, null);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).liked()).isTrue();
        }
    }
}
