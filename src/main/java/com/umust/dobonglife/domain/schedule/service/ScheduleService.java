package com.umust.dobonglife.domain.schedule.service;

import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.schedule.controller.dto.request.ScheduleRegisterRequest;
import com.umust.dobonglife.domain.schedule.controller.dto.response.ScheduleResponse;
import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;
import com.umust.dobonglife.domain.schedule.domain.constant.ScheduleType;
import com.umust.dobonglife.domain.schedule.domain.repository.ScheduleRepository;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

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
                .user(user)
                .build();

        if(request.getPlaceId()!=null) {
            Place place = placeRepository.findById(request.getPlaceId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));
            schedule.setPlace(place);
        }

        scheduleRepository.save(schedule);
    }

    @Transactional (readOnly = true)
    public List<Schedule> getTodaySchedule(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Schedule> schedules = scheduleRepository.findTodaySchedules(user.getId(), LocalDate.now());

        List<ScheduleResponse> responses = schedules.stream()
                .map(ScheduleResponse::from)
                .toList();

        return schedules;
    }

}
