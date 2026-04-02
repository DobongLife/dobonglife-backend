package com.umust.dobonglife.domain.schedule.infrastructure.jpa;

import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;
import com.umust.dobonglife.domain.schedule.infrastructure.jpa.custom.ScheduleQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleJpaRepository extends JpaRepository<Schedule, Long>, ScheduleQueryRepository {

    Optional<Schedule> findByIdAndUserId(Long scheduleId, Long userId);

    List<Schedule> findAllByStartTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
