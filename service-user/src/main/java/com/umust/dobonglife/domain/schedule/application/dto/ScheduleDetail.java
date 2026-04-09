package com.umust.dobonglife.domain.schedule.application.dto;

import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleDetail {

    private Long id;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String memo;
    private String placeName;
    private Boolean isAllDay;
    private Boolean isEvent;
    private String color;

    public static ScheduleDetail from(Schedule schedule) {
        return ScheduleDetail.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .memo(schedule.getMemo())
                .placeName(schedule.getPlaceName())
                .isAllDay(schedule.getIsAllDay())
                .isEvent(schedule.getIsEvent())
                .color(schedule.getColor().name())
                .build();
    }
}
