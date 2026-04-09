package com.umust.dobonglife.domain.schedule.application.port.in;

import com.umust.dobonglife.domain.schedule.application.dto.MonthlyScheduleResult;

public interface ScheduleQueryUseCase {

    MonthlyScheduleResult getMonthlyScheduleList(Long userId, int year, int month);
}
