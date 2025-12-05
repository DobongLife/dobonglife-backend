package com.umust.dobonglife.domain.schedule.service;

import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.schedule.controller.dto.request.ScheduleRegisterRequest;
import com.umust.dobonglife.domain.schedule.controller.dto.response.DailyScheduleResponse;
import com.umust.dobonglife.domain.schedule.controller.dto.response.ScheduleListResponse;
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
import java.util.Map;
import java.util.stream.Collectors;

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
    public ScheduleMonthResponse getMonthlySchedules(Long userId, int year, int month) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 9월이면: [9/1 00:00:00 ~ 10/1 00:00:00)
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = startDate.plusMonths(1).atStartOfDay();

        List<Schedule> schedules = scheduleRepository.findMonthlySchedules(user.getId(), start, end);

        // 엔티티 → ScheduleResponse 변환
        List<ScheduleResponse> responses = schedules.stream()
                .map(ScheduleResponse::from)
                .toList();

        // 날짜별 그룹핑 (TreeMap으로 날짜 순 정렬)
        Map<LocalDate, List<ScheduleResponse>> groupedByDate =
                responses.stream()
                        .collect(Collectors.groupingBy(
                                r -> r.getStartTime().toLocalDate(),
                                TreeMap::new,
                                Collectors.toList()
                        ));

        // Map → DailyScheduleResponse 리스트로 변환
        List<DailyScheduleResponse> dailySchedules = groupedByDate.entrySet().stream()
                .map(entry -> DailyScheduleResponse.of(entry.getKey(), entry.getValue()))
                .toList();

        return ScheduleMonthResponse.of(year, month, dailySchedules);
    }

}
