package com.umust.dobonglife.domain.schedule.application.port.out;

import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;

public interface SaveSchedulePort {

    Schedule save(Schedule schedule);

    void delete(Schedule schedule);
}
