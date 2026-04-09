package com.umust.dobonglife.domain.schedule.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DailyScheduleDetail {

    private LocalDate date;
    private List<ScheduleDetail> schedules;

    public static DailyScheduleDetail of(LocalDate date, List<ScheduleDetail> schedules) {
        return DailyScheduleDetail.builder()
                .date(date)
                .schedules(schedules)
                .build();
    }
}
