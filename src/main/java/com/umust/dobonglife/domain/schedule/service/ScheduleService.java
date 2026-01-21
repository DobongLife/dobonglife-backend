package com.umust.dobonglife.domain.schedule.service;

import com.umust.dobonglife.domain.schedule.controller.dto.request.ScheduleRegisterRequest;
import com.umust.dobonglife.domain.schedule.controller.dto.request.ScheduleUpdateRequest;
import com.umust.dobonglife.domain.schedule.controller.dto.response.DailyScheduleResponse;
import com.umust.dobonglife.domain.schedule.controller.dto.response.MonthlyScheduleResponse;
import com.umust.dobonglife.domain.schedule.controller.dto.response.ScheduleListResponse;
import com.umust.dobonglife.domain.schedule.controller.dto.response.ScheduleResponse;
import com.umust.dobonglife.domain.schedule.domain.constant.Color;
import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;
import com.umust.dobonglife.domain.schedule.domain.entity.ScheduleDate;
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
    public void registerSchedule(ScheduleRegisterRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .memo(request.getMemo())
                .isEvent(false)
                .isAllDay(request.isAllDay())
                .color(Color.toEnum(request.getColor()))
                .user(user)
                .placeName(request.getPlaceName())
                .build();

        scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public ScheduleListResponse getTodaySchedule(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        LocalDate today = LocalDate.now();

        List<ScheduleDate> scheduleDates = scheduleDateRepository.findTodayScheduleDates(userId, today);

        List<ScheduleResponse> responses = scheduleDates.stream()
                .map(this::toScheduleResponse) // ScheduleDate -> ScheduleResponse
                .toList();

        return ScheduleListResponse.from(responses);
    }

    @Transactional(readOnly = true)
    public MonthlyScheduleResponse getMonthlySchedules(Long userId, int year, int month) {

        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDateExclusive = startDate.plusMonths(1);

        List<ScheduleDate> scheduleDates =
                scheduleDateRepository.findMonthlyScheduleDates(userId, startDate, endDateExclusive);

        Map<LocalDate, List<ScheduleResponse>> groupedByDate = scheduleDates.stream()
                .collect(Collectors.groupingBy(
                        ScheduleDate::getDate,
                        TreeMap::new,
                        Collectors.mapping(this::toScheduleResponse, Collectors.toList())
                ));

        List<DailyScheduleResponse> dailySchedules = groupedByDate.entrySet().stream()
                .map(e -> DailyScheduleResponse.of(e.getKey(), e.getValue()))
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
    public void updateSchedule(Long scheduleId, ScheduleUpdateRequest request, Long userId) {

        // 유저 존재 확인(선택) - 권한만 체크하면 생략 가능
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 내 스케줄인지까지 한 번에 검증
        Schedule schedule = scheduleRepository.findByIdAndUserId(scheduleId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND)); // 없으면 에러코드 하나 추가 추천

        // 값 갱신
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
