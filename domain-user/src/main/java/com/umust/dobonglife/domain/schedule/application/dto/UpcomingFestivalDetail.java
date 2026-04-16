package com.umust.dobonglife.domain.schedule.application.dto;

import com.umust.dobonglife.domain.schedule.domain.entity.Festival;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpcomingFestivalDetail {

    private Long festivalId;
    private String title;
    private String placeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String url;
    private String category;

    public static UpcomingFestivalDetail from(Festival festival) {
        return UpcomingFestivalDetail.builder()
                .festivalId(festival.getId())
                .title(festival.getTitle())
                .placeName(festival.getPlaceName())
                .startTime(festival.getStartDateTime())
                .endTime(festival.getEndDateTime())
                .url(festival.getUrl())
                .category(festival.getCategory())
                .build();
    }
}
