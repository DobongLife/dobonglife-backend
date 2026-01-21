package com.umust.dobonglife.domain.schedule.controller.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleUpdateRequest {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String memo;
    private Boolean isAllDay;
    private String color;
    private String placeName;
}
