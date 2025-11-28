package com.umust.dobonglife.domain.schedule.service;

import com.umust.dobonglife.domain.place.model.Place;
import com.umust.dobonglife.domain.schedule.dto.request.ScheduleRegisterRequest;
import com.umust.dobonglife.domain.schedule.model.Schedule;
import com.umust.dobonglife.domain.schedule.model.ScheduleType;
import com.umust.dobonglife.domain.schedule.respository.ScheduleRepository;
import com.umust.dobonglife.domain.user.model.User;
import com.umust.dobonglife.domain.user.repository.UserRepository;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerSchedule(ScheduleRegisterRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .memo(request.getMemo())
                .scheduleType(ScheduleType.toEnum(request.getScheduleType()))
                .build();

        scheduleRepository.save(schedule);
    }




}
