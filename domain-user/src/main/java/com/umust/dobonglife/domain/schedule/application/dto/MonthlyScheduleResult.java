package com.umust.dobonglife.domain.schedule.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MonthlyScheduleResult {

    private List<DailyScheduleDetail> scheduleList;

    public static MonthlyScheduleResult from(List<DailyScheduleDetail> scheduleList) {
        return MonthlyScheduleResult.builder().scheduleList(scheduleList).build();
    }
}
