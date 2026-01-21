package com.umust.dobonglife.domain.schedule.service;

import com.umust.dobonglife.domain.schedule.controller.dto.request.ScheduleRequest;
import com.umust.dobonglife.domain.schedule.controller.dto.response.DailyScheduleResponse;
import com.umust.dobonglife.domain.schedule.controller.dto.response.MonthlyScheduleResponse;
import com.umust.dobonglife.domain.schedule.controller.dto.response.ScheduleListResponse;
import com.umust.dobonglife.domain.schedule.controller.dto.response.ScheduleResponse;

import com.umust.dobonglife.domain.schedule.domain.constant.Color;
import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;
import com.umust.dobonglife.domain.schedule.domain.repository.ScheduleRepository;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerSchedule(ScheduleRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));


        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .memo(request.getMemo())
                .user(user)
                .placeName(request.getPlaceName())
                .build();

        scheduleRepository.save(schedule);
    }

    @Transactional (readOnly = true)
    public ScheduleListResponse getTodaySchedule(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Schedule> schedules = scheduleRepository.findTodaySchedules(user.getId(), LocalDate.now());

        List<ScheduleResponse> responses = schedules.stream()
                .map(ScheduleResponse::from)
                .toList();

        return ScheduleListResponse.from(responses);
    }

    @Transactional(readOnly = true)
    public MonthlyScheduleResponse getMonthlySchedules(Long userId, int year, int month) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 9월 1일 부터 10월 1일
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = startDate.plusMonths(1).atStartOfDay();

        List<Schedule> schedules = scheduleRepository.findMonthlySchedules(user.getId(), start, end);
        Map<LocalDate, List<ScheduleResponse>> groupedByDate = schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.groupingBy(
                        r -> r.getStartTime().toLocalDate(),
                        TreeMap::new,
                        Collectors.toList()
                ));

        List<DailyScheduleResponse> dailySchedules = groupedByDate.entrySet().stream()
                .map(entry -> DailyScheduleResponse.of(entry.getKey(), entry.getValue()))
                .toList();

        return MonthlyScheduleResponse.from(dailySchedules);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId, Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Schedule schedule = scheduleRepository.findByIdAndUserId(scheduleId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        scheduleRepository.delete(schedule);
    }

    @Transactional
    public void updateSchedule(Long scheduleId, ScheduleRequest request, Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Schedule schedule = scheduleRepository.findByIdAndUserId(scheduleId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND)); // 없으면 에러코드 하나 추가 추천

        schedule.update(
                request.getTitle(),
                request.getStartTime(),
                request.getEndTime(),
                request.getMemo(),
                request.getIsAllDay(),
                Color.toEnum(request.getColor()),
                request.getPlaceName()
        );
    }
}
