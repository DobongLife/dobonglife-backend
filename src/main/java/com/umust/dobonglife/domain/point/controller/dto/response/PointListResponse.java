package com.umust.dobonglife.domain.point.controller.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PointListResponse {

    List<PointResponse> pointList;

    public static PointListResponse from(List<PointResponse> pointList) {
        return PointListResponse.builder().pointList(pointList).build();
    }
}
