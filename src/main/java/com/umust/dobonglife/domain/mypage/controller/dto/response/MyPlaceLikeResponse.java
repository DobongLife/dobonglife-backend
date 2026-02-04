package com.umust.dobonglife.domain.mypage.controller.dto.response;

import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceSummaryResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;

public record MyPlaceLikeResponse(Long totalCount,
                                  CursorResponse<PlaceSummaryResponse> place) {
}