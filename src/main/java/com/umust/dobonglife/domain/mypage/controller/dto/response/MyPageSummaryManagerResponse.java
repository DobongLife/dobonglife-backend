package com.umust.dobonglife.domain.mypage.controller.dto.response;

import com.umust.dobonglife.domain.point.controller.dto.response.PointResponse;
import com.umust.dobonglife.domain.user.controller.dto.response.MyPageResponse;
import com.umust.dobonglife.global.common.response.slice.SliceResponse;

public record MyPageSummaryManagerResponse(
        MyPageResponse userInfo,
        SliceResponse<PointResponse> pointList,
        boolean isAuthenticated,
        Long businessId
) {
    public static MyPageSummaryManagerResponse of(
            MyPageResponse userInfo,
            SliceResponse<PointResponse> pointList,
            boolean isAuthenticated,
            Long businessId
    ) {
        return new MyPageSummaryManagerResponse(
                userInfo,
                pointList,
                isAuthenticated,
                businessId
        );
    }
}
