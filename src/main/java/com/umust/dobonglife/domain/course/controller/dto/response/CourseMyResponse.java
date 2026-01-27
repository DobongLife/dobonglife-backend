package com.umust.dobonglife.domain.course.controller.dto.response;

import com.umust.dobonglife.global.common.response.CursorResponse;

public record CourseMyResponse(
        Long totalCount,
        CursorResponse<CourseSummaryResponse> course
) {
    public static CourseMyResponse from(Long totalCount, CursorResponse<CourseSummaryResponse> course) {
        return new CourseMyResponse(totalCount, course);
    }
}
