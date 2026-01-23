package com.umust.dobonglife.domain.schedule.controller.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpcomingFestivalResponse {
    private Long festivalId;
    private String title;
    private String placeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String url;
    private String category;
}
