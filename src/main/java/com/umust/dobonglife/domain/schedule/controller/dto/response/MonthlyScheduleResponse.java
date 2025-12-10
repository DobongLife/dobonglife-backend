package com.umust.dobonglife.domain.schedule.controller.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MonthlyScheduleResponse {

    private List<DailyScheduleResponse> days;

    public static MonthlyScheduleResponse from(List<DailyScheduleResponse> days) {
        return MonthlyScheduleResponse.builder()
                .days(days)
                .build();
    }
}
