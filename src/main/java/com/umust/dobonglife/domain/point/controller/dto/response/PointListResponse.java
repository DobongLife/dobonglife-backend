package com.umust.dobonglife.domain.point.controller.dto.response;

import com.umust.dobonglife.domain.schedule.controller.dto.response.ScheduleListResponse;
import com.umust.dobonglife.domain.schedule.controller.dto.response.ScheduleResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PointListResponse {

    List<PointResponse> pointList;

    public static PointListResponse build(List<PointResponse> pointList) {
        return PointListResponse.builder().pointList(pointList).build();
    }
}
