package com.umust.dobonglife.domain.schedule.controller.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DailyScheduleResponse {

    private LocalDate date;
    private List<ScheduleResponse> schedules;

    public static DailyScheduleResponse of(LocalDate date, List<ScheduleResponse> schedules) {
        return DailyScheduleResponse.builder()
                .date(date)
                .schedules(schedules)
                .build();
    }
}
