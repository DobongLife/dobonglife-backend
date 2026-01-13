package com.umust.dobonglife.domain.place.controller.dto.response;

import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceAndCourseListResponse {
    List<PlaceSummaryResponse> placeResponseList;
    CursorResponse<CourseSummaryResponse> courseResponseList;

    public static PlaceAndCourseListResponse from(final List<PlaceSummaryResponse> placeResponseList,
                                         final CursorResponse<CourseSummaryResponse> courseResponseList) {
        return PlaceAndCourseListResponse.builder()
                .placeResponseList(placeResponseList)
                .courseResponseList(courseResponseList)
                .build();
    }
}
