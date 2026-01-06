package com.umust.dobonglife.domain.place.controller.dto.response;

import com.umust.dobonglife.domain.course.controller.dto.response.CourseResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceAndCourseListResponse {
    List<PlaceResponse> placeResponseList;
    List<CourseResponse> courseResponseList;

    public static PlaceAndCourseListResponse from(final List<PlaceResponse> placeResponseList,
                                         final List<CourseResponse> courseResponseList) {
        return PlaceAndCourseListResponse.builder()
                .placeResponseList(placeResponseList)
                .courseResponseList(courseResponseList)
                .build();
    }
}
