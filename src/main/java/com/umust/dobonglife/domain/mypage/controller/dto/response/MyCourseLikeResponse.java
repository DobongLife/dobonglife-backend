package com.umust.dobonglife.domain.mypage.controller.dto.response;

import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;

public record MyCourseLikeResponse(Long totalCount,
                                   CursorResponse<CourseSummaryResponse> courses) {
}
