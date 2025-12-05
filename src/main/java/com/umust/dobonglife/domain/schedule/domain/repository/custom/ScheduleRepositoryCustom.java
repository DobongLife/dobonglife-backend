package com.umust.dobonglife.domain.schedule.domain.repository.custom;

import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepositoryCustom {
    List<Schedule> findTodaySchedules(Long userId, LocalDate date);
}
