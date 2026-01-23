package com.umust.dobonglife.domain.schedule.service;

import com.umust.dobonglife.domain.schedule.controller.dto.request.ScheduleRequest;
import com.umust.dobonglife.domain.schedule.controller.dto.response.DailyScheduleResponse;
import com.umust.dobonglife.domain.schedule.controller.dto.response.MonthlyScheduleListResponse;
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
                .isAllDay(request.getIsAllDay())
                .isEvent(false)
                .color(Color.toEnum(request.getColor()))
                .build();

        scheduleRepository.save(schedule);
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

    @Transactional(readOnly = true)
    public MonthlyScheduleListResponse getMonthlyScheduleList(Long userId, int year, int month) {

        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

        LocalDateTime startInclusive = monthStart.atStartOfDay();
        LocalDateTime endExclusive = monthStart.plusMonths(1).atStartOfDay();

        // 월과 "겹치는" 일정만 조회 (멀티데이 포함)
        List<Schedule> schedules = scheduleRepository.findMonthlyOverlaps(userId, startInclusive, endExclusive);

        // 일정이 있는 날짜만 담는 Map (날짜 오름차순)
        Map<LocalDate, List<ScheduleResponse>> bucket = new TreeMap<>();

        for (Schedule s : schedules) {
            LocalDate sStart = s.getStartTime().toLocalDate();
            LocalDate sEnd = s.getEndTime().toLocalDate();

            // 월 범위로 클램프
            LocalDate from = sStart.isBefore(monthStart) ? monthStart : sStart;
            LocalDate to = sEnd.isAfter(monthEnd) ? monthEnd : sEnd;

            ScheduleResponse dto = ScheduleResponse.from(s);

            // 멀티데이 일정이면 걸친 날짜마다 넣기
            for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
                bucket.computeIfAbsent(d, k -> new ArrayList<>()).add(dto);
            }
        }

        // (선택) 같은 날짜 안에서 시간순 정렬
        for (List<ScheduleResponse> daySchedules : bucket.values()) {
            daySchedules.sort(
                    Comparator.comparing(ScheduleResponse::getStartTime)
                            .thenComparing(ScheduleResponse::getId)
            );
        }

        // Map -> List<DailyScheduleResponse>
        List<DailyScheduleResponse> result = new ArrayList<>(bucket.size());
        for (Map.Entry<LocalDate, List<ScheduleResponse>> e : bucket.entrySet()) {
            result.add(DailyScheduleResponse.of(e.getKey(), e.getValue()));
        }
        return MonthlyScheduleListResponse.from(result);
    }
}
