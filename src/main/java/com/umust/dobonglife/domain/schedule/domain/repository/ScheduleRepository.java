package com.umust.dobonglife.domain.schedule.domain.repository;

import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;
import com.umust.dobonglife.domain.schedule.domain.repository.custom.ScheduleRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepositoryCustom {
    Optional<Schedule> findByIdAndUserId(Long scheduleId, Long userId);
}
