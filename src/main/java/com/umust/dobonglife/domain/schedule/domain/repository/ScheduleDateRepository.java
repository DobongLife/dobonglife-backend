package com.umust.dobonglife.domain.schedule.domain.repository;

import com.umust.dobonglife.domain.schedule.domain.entity.ScheduleDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleDateRepository extends JpaRepository<ScheduleDate, Long> {
    void deleteBySchedule_Id(Long scheduleId);
}
