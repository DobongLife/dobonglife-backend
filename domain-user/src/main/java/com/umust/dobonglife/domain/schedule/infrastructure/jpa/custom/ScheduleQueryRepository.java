package com.umust.dobonglife.domain.schedule.infrastructure.jpa.custom;

import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleQueryRepository {

    List<Schedule> findMonthlyOverlaps(Long userId, LocalDateTime startInclusive, LocalDateTime endExclusive);
}
