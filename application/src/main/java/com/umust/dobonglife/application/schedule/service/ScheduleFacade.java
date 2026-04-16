package com.umust.dobonglife.application.schedule.service;

import com.umust.dobonglife.application.schedule.client.ScheduleRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleFacade {

    private final ScheduleRestClient scheduleRestClient;

    public void registerSchedule(String title, LocalDateTime startTime, LocalDateTime endTime,
                                  String memo, String placeName, Boolean isEvent, Boolean isAllDay,
                                  String color, Long userId) {
        scheduleRestClient.registerSchedule(title, startTime, endTime, memo, placeName, isEvent, isAllDay, color, userId);
    }

    public void updateSchedule(Long scheduleId, String title, LocalDateTime startTime, LocalDateTime endTime,
                                String memo, String placeName, Boolean isEvent, Boolean isAllDay,
                                String color, Long userId) {
        scheduleRestClient.updateSchedule(scheduleId, title, startTime, endTime, memo, placeName, isEvent, isAllDay, color, userId);
    }

    public void deleteSchedule(Long scheduleId, Long userId) {
        scheduleRestClient.deleteSchedule(scheduleId, userId);
    }

    public MonthlyScheduleResponse getMonthlySchedule(Long userId, int year, int month) {
        return scheduleRestClient.getMonthlySchedule(userId, year, month);
    }

    public List<UpcomingFestivalResponse> getUpcomingFestivals() {
        return scheduleRestClient.getUpcomingFestivals();
    }

    public record MonthlyScheduleResponse(List<DailyScheduleResponse> scheduleList) {}

    public record DailyScheduleResponse(String date, List<ScheduleResponse> schedules) {}

    public record ScheduleResponse(
            Long id, String title, String startTime, String endTime,
            String memo, String placeName, Boolean isAllDay, Boolean isEvent, String color
    ) {}

    public record UpcomingFestivalResponse(
            Long festivalId, String title, String placeName,
            String startTime, String endTime, String url, String category
    ) {}
}
