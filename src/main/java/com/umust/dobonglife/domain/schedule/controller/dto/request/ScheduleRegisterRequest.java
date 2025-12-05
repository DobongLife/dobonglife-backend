package com.umust.dobonglife.domain.schedule.controller.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleRegisterRequest {

    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String memo;
    private String scheduleType;
    private Long placeId;
}
