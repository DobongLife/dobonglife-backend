package com.umust.dobonglife.domain.schedule.controller.dto.response;

import com.umust.dobonglife.domain.place.controller.dto.response.PlaceResponse;
import com.umust.dobonglife.domain.place.domain.constant.Amenity;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.schedule.domain.constant.ScheduleType;
import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Builder
public class ScheduleResponse {

    private Long id;

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String memo;

    private ScheduleType scheduleType;

    private String placeName;

    public static ScheduleResponse from(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .memo(schedule.getMemo())
                .scheduleType(schedule.getScheduleType())
                .placeName(Optional.ofNullable(schedule.getPlace())
                        .map(Place::getName)
                        .orElse(null))
                .build();
    }
}
