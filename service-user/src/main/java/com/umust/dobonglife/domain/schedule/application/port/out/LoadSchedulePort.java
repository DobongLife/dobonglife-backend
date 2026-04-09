package com.umust.dobonglife.domain.schedule.application.port.out;

import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public interface LoadSchedulePort {

    Schedule loadByIdAndUserId(Long scheduleId, Long userId);

    List<Schedule> findMonthlyOverlaps(Long userId, LocalDateTime startInclusive, LocalDateTime endExclusive);
}
