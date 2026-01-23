package com.umust.dobonglife.domain.schedule.controller.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MonthlyScheduleListResponse {

    List<DailyScheduleResponse> scheduleList;

    public static MonthlyScheduleListResponse from(final List<DailyScheduleResponse> scheduleList) {
        return MonthlyScheduleListResponse.builder().scheduleList(scheduleList).build();
    }
}
