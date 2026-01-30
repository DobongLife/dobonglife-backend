package com.umust.dobonglife.domain.mypage.service;

import com.umust.dobonglife.domain.banners.service.BannerService;
import com.umust.dobonglife.domain.banners.service.dto.BannerSummaryResponse;
import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.business.service.BusinessService;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionSummaryItem;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.domain.courseLike.domain.entity.CourseLike;
import com.umust.dobonglife.domain.courseLike.service.CourseLikeService;
import com.umust.dobonglife.domain.mypage.controller.dto.response.MyLikeResponse;
import com.umust.dobonglife.domain.mypage.controller.dto.response.MyPageSummaryManagerResponse;
import com.umust.dobonglife.domain.mypage.controller.dto.response.MyPageSummaryResponse;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceSummaryListResponse;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceSummaryResponse;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.domain.point.controller.dto.response.PointResponse;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.domain.user.controller.dto.response.MyPageResponse;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.slice.SliceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public MyLikeResponse getMyLike(Long userId, int size, Long lastId) {
        CursorResponse<CourseSummaryResponse> likedCourse = courseService.getLikedCourse(userId, size, lastId);
        CursorResponse<PlaceSummaryResponse> likedPlace = placeService.getLikedPlace(userId, size, lastId);

        return new MyLikeResponse(likedCourse, likedPlace);
    }

    public MyPageSummaryManagerResponse getMyPageSummaryManager(Long userId, int size) {
        MyPageResponse userInfo = userService.getUserInfo(userId);
        SliceResponse<PointResponse> pointList = pointService.getPointResponse(userId, size, null, DESC.name());

        Business business = businessService.getBusinessByUser(userId);
        return MyPageSummaryManagerResponse.of(userInfo, pointList, business.isAuthenticated(), business.getId());
    }
}
