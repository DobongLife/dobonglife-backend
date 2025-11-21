package com.umust.dobonglife.domain.schedule.dto.request;

import com.umust.dobonglife.domain.schedule.model.ScheduleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleRegisterRequest {

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String memo;

    private ScheduleType scheduleType;
}
