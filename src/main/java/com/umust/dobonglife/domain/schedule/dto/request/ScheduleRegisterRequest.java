package com.umust.dobonglife.domain.schedule.dto.request;

import com.umust.dobonglife.domain.schedule.model.ScheduleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleRegisterRequest {

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String memo;

    private String scheduleType;
}
