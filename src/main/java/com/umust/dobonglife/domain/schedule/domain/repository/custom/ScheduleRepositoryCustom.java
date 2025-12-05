package com.umust.dobonglife.domain.schedule.domain.repository.custom;

import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepositoryCustom {
    List<Schedule> findTodaySchedules(Long userId, LocalDate date);
    List<Schedule> findMonthlySchedules(Long userId, LocalDateTime start, LocalDateTime end);
}
