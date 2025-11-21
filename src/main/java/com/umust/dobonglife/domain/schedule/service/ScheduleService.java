package com.umust.dobonglife.domain.schedule.service;

import com.umust.dobonglife.domain.schedule.dto.request.ScheduleRegisterRequest;
import com.umust.dobonglife.domain.schedule.model.Schedule;
import com.umust.dobonglife.domain.schedule.model.ScheduleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void registerSchedule(ScheduleRegisterRequest request, Long userId) {
        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .memo(request.getMemo())
                .scheduleType(ScheduleType.toEnum(request.getScheduleType()))
                .build();

        sch
    }




}
