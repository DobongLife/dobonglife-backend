package com.umust.dobonglife.domain.mypage.service;

import com.umust.dobonglife.domain.business.service.BusinessService;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.domain.mypage.controller.dto.response.MyCourseLikeResponse;
import com.umust.dobonglife.domain.mypage.controller.dto.response.MyPageSummaryResponse;
import com.umust.dobonglife.domain.mypage.controller.dto.response.MyPlaceLikeResponse;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceSummaryResponse;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.domain.point.controller.dto.response.PointResponse;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.domain.user.controller.dto.response.MyPageResponse;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.slice.SliceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MyPageServiceTest {

    @Mock
    UserService userService;

    @Mock
    PointService pointService;

    @Mock
    CourseService courseService;

    @Mock
    PlaceService placeService;

    @Mock
    BusinessService businessService;

    @InjectMocks
    MyPageService myPageService;

    @Nested
    @DisplayName("getMyPageSummary - 마이페이지 요약")
    class GetMyPageSummary {

        @Test
        @DisplayName("사용자 정보와 포인트 반환")
        void 사용자_정보와_포인트_반환() {
            // given
            Long userId = 1L;
            MyPageResponse userInfo = mock(MyPageResponse.class);
            SliceResponse<PointResponse> pointList = SliceResponse.<PointResponse>builder()
                    .content(List.of())
                    .size(10)
                    .hasNext(false)
                    .build();

            given(userService.getUserInfo(userId)).willReturn(userInfo);
            given(pointService.getPointResponse(eq(userId), eq(10), isNull(), eq("DESC")))
                    .willReturn(pointList);

            // when
            MyPageSummaryResponse result = myPageService.getMyPageSummary(userId, 10);

            // then
            assertThat(result.userInfo()).isEqualTo(userInfo);
            assertThat(result.pointList()).isEqualTo(pointList);
        }
    }

    @Nested
    @DisplayName("getMyPlaceLike - 내 장소 좋아요")
    class GetMyPlaceLike {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            given(placeService.getLikedPlaceCount(userId)).willReturn(3L);
            given(placeService.getLikedPlace(userId, 10, null))
                    .willReturn(new CursorResponse<>(List.of(), false));

            // when
            MyPlaceLikeResponse result = myPageService.getMyPlaceLike(userId, 10, null);

            // then
            assertThat(result.totalCount()).isEqualTo(3L);
        }

        @Test
        @DisplayName("빈 결과")
        void 빈_결과() {
            // given
            Long userId = 1L;
            given(placeService.getLikedPlaceCount(userId)).willReturn(0L);
            given(placeService.getLikedPlace(userId, 10, null))
                    .willReturn(new CursorResponse<>(List.of(), false));

            // when
            MyPlaceLikeResponse result = myPageService.getMyPlaceLike(userId, 10, null);

            // then
            assertThat(result.totalCount()).isZero();
            assertThat(result.place().getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getMyCourseLike - 내 코스 좋아요")
    class GetMyCourseLike {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            given(courseService.getLikedCourseCount(userId)).willReturn(5L);
            given(courseService.getLikedCourse(userId, 10, null))
                    .willReturn(new CursorResponse<>(List.of(), false));

            // when
            MyCourseLikeResponse result = myPageService.getMyCourseLike(userId, 10, null);

            // then
            assertThat(result.totalCount()).isEqualTo(5L);
        }

        @Test
        @DisplayName("빈 결과")
        void 빈_결과() {
            // given
            Long userId = 1L;
            given(courseService.getLikedCourseCount(userId)).willReturn(0L);
            given(courseService.getLikedCourse(userId, 10, null))
                    .willReturn(new CursorResponse<>(List.of(), false));

            // when
            MyCourseLikeResponse result = myPageService.getMyCourseLike(userId, 10, null);

            // then
            assertThat(result.totalCount()).isZero();
        }
    }
}
