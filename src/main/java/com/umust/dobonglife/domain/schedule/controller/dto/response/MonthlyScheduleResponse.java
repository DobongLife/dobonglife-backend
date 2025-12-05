package com.umust.dobonglife.domain.schedule.controller.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MonthlyScheduleResponse {

    private int year;
    private int month;
    private List<DailyScheduleResponse> days;

    public static MonthlyScheduleResponse of(int year, int month, List<DailyScheduleResponse> days) {
        return MonthlyScheduleResponse.builder()
                .year(year)
                .month(month)
                .days(days)
                .build();
    }
}
