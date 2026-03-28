package com.umust.dobonglife.domain.course.application.dto;

import com.umust.dobonglife.global.common.response.CursorResponse;

public record MyCourseResponse(
        long totalCount,
        CursorResponse<CourseSummaryResponse> courses
) {

    public static MyCourseResponse of(long totalCount, CursorResponse<CourseSummaryResponse> courses) {
        return new MyCourseResponse(totalCount, courses);
    }
}
