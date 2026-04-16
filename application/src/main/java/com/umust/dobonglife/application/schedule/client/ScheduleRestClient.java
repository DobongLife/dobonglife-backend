package com.umust.dobonglife.application.schedule.client;

import com.umust.dobonglife.application.schedule.service.ScheduleFacade.MonthlyScheduleResponse;
import com.umust.dobonglife.application.schedule.service.ScheduleFacade.UpcomingFestivalResponse;
import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduleRestClient {

    private final RestClient restClient;

    public ScheduleRestClient(@Value("${service.user.url}") String baseUrl) {
        this.restClient = InternalRestClientFactory.create(baseUrl, "user-service");
    }

    public void registerSchedule(String title, LocalDateTime startTime, LocalDateTime endTime,
                                  String memo, String placeName, Boolean isEvent, Boolean isAllDay,
                                  String color, Long userId) {
        restClient.post()
                .uri(uriBuilder -> uriBuilder.path("/internal/schedule")
                        .queryParam("title", title)
                        .queryParam("startTime", startTime)
                        .queryParam("endTime", endTime)
                        .queryParam("memo", memo)
                        .queryParam("placeName", placeName)
                        .queryParam("isEvent", isEvent)
                        .queryParam("isAllDay", isAllDay)
                        .queryParam("color", color)
                        .queryParam("userId", userId)
                        .build())
                .retrieve()
                .toBodilessEntity();
    }

    public void updateSchedule(Long scheduleId, String title, LocalDateTime startTime, LocalDateTime endTime,
                                String memo, String placeName, Boolean isEvent, Boolean isAllDay,
                                String color, Long userId) {
        restClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/internal/schedule/{scheduleId}")
                        .queryParam("title", title)
                        .queryParam("startTime", startTime)
                        .queryParam("endTime", endTime)
                        .queryParam("memo", memo)
                        .queryParam("placeName", placeName)
                        .queryParam("isEvent", isEvent)
                        .queryParam("isAllDay", isAllDay)
                        .queryParam("color", color)
                        .queryParam("userId", userId)
                        .build(scheduleId))
                .retrieve()
                .toBodilessEntity();
    }

    public void deleteSchedule(Long scheduleId, Long userId) {
        restClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/internal/schedule/{scheduleId}")
                        .queryParam("userId", userId)
                        .build(scheduleId))
                .retrieve()
                .toBodilessEntity();
    }

    public MonthlyScheduleResponse getMonthlySchedule(Long userId, int year, int month) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/internal/schedule/monthly")
                        .queryParam("userId", userId)
                        .queryParam("year", year)
                        .queryParam("month", month)
                        .build())
                .retrieve()
                .body(MonthlyScheduleResponse.class);
    }

    public List<UpcomingFestivalResponse> getUpcomingFestivals() {
        return restClient.get()
                .uri("/internal/schedule/festivals/upcoming")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
