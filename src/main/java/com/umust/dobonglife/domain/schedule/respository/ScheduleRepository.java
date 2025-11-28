package com.umust.dobonglife.domain.schedule.respository;

import com.umust.dobonglife.domain.schedule.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
