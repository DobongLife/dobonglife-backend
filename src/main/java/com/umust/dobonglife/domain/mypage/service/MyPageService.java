package com.umust.dobonglife.domain.mypage.service;

import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.business.service.BusinessService;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.domain.mypage.controller.dto.response.MyCourseLikeResponse;
import com.umust.dobonglife.domain.mypage.controller.dto.response.MyPageSummaryManagerResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.umust.dobonglife.global.common.response.slice.SortOrder.DESC;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserService userService;
    private final PointService pointService;
    private final CourseService courseService;
    private final PlaceService placeService;
    private final BusinessService businessService;

    public MyPageSummaryResponse getMyPageSummary(Long userId, int size) {
        MyPageResponse userInfo = userService.getUserInfo(userId);
        SliceResponse<PointResponse> pointList = pointService.getPointResponse(userId, size, null, DESC.name());
        return new MyPageSummaryResponse(userInfo, pointList);
    }

    public MyPlaceLikeResponse getMyPlaceLike(Long userId, int size, Long lastId) {
        Long totalCount = placeService.getLikedPlaceCount(userId);
        CursorResponse<PlaceSummaryResponse> likedPlace = placeService.getLikedPlace(userId, size, lastId);

        return new MyPlaceLikeResponse(totalCount, likedPlace);
    }

    public MyCourseLikeResponse getMyCourseLike(Long userId, int size, Long lastId) {
        Long totalCount = courseService.getLikedCourseCount(userId);
        CursorResponse<CourseSummaryResponse> likedCourse = courseService.getLikedCourse(userId, size, lastId);
        return new MyCourseLikeResponse(totalCount, likedCourse);
    }

    public MyPageSummaryManagerResponse getMyPageSummaryManager(Long userId, int size) {
        MyPageResponse userInfo = userService.getUserInfo(userId);
        SliceResponse<PointResponse> pointList = pointService.getPointResponse(userId, size, null, DESC.name());

        Business business = businessService.getBusinessByUser(userId);
        return MyPageSummaryManagerResponse.of(userInfo, pointList, business.isAuthenticated(), business.getId());
    }
}
