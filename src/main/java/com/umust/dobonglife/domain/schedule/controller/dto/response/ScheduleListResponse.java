package com.umust.dobonglife.domain.schedule.controller.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ScheduleListResponse {

    List<ScheduleResponse> scheduleList;

    public static ScheduleListResponse from(final List<ScheduleResponse> scheduleList) {
        return ScheduleListResponse.builder().scheduleList(scheduleList).build();
    }
}
