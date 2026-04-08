package com.umust.dobonglife.domain.schedule.application.port.in;

import java.time.LocalDateTime;

public interface ScheduleCommandUseCase {

    void registerSchedule(String title, LocalDateTime startTime, LocalDateTime endTime,
                          String memo, String placeName, Boolean isEvent, Boolean isAllDay,
                          String color, Long userId);

    void updateSchedule(Long scheduleId, String title, LocalDateTime startTime, LocalDateTime endTime,
                        String memo, String placeName, Boolean isEvent, Boolean isAllDay,
                        String color, Long userId);

    void deleteSchedule(Long scheduleId, Long userId);
}
