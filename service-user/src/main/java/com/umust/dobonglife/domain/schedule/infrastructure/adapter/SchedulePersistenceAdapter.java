package com.umust.dobonglife.domain.schedule.infrastructure.adapter;

import com.umust.dobonglife.domain.schedule.application.port.out.LoadSchedulePort;
import com.umust.dobonglife.domain.schedule.application.port.out.SaveSchedulePort;
import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;
import com.umust.dobonglife.domain.schedule.exception.ScheduleErrorCode;
import com.umust.dobonglife.domain.schedule.exception.ScheduleException;
import com.umust.dobonglife.domain.schedule.infrastructure.jpa.ScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SchedulePersistenceAdapter implements LoadSchedulePort, SaveSchedulePort {

    private final ScheduleJpaRepository scheduleJpaRepository;

    // ── LoadSchedulePort ──

    @Override
    public Schedule loadByIdAndUserId(Long scheduleId, Long userId) {
        return scheduleJpaRepository.findByIdAndUserId(scheduleId, userId)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));
    }

    @Override
    public List<Schedule> findMonthlyOverlaps(Long userId, LocalDateTime startInclusive, LocalDateTime endExclusive) {
        return scheduleJpaRepository.findMonthlyOverlaps(userId, startInclusive, endExclusive);
    }

    // ── SaveSchedulePort ──

    @Override
    public Schedule save(Schedule schedule) {
        return scheduleJpaRepository.save(schedule);
    }

    @Override
    public void delete(Schedule schedule) {
        scheduleJpaRepository.delete(schedule);
    }
}
