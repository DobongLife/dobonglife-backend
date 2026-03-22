package com.umust.dobonglife.domain.schedule.application.service;

import com.umust.dobonglife.domain.schedule.application.port.in.ScheduleCommandUseCase;
import com.umust.dobonglife.domain.schedule.application.port.out.LoadSchedulePort;
import com.umust.dobonglife.domain.schedule.application.port.out.SaveSchedulePort;
import com.umust.dobonglife.domain.schedule.domain.constant.Color;
import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;
import com.umust.dobonglife.domain.schedule.exception.ScheduleErrorCode;
import com.umust.dobonglife.domain.schedule.exception.ScheduleException;
import com.umust.dobonglife.domain.user.application.port.out.LoadUserPort;
import com.umust.dobonglife.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleCommandService implements ScheduleCommandUseCase {

    private final LoadSchedulePort loadSchedulePort;
    private final SaveSchedulePort saveSchedulePort;
    private final LoadUserPort loadUserPort;

    @Override
    public void registerSchedule(String title, LocalDateTime startTime, LocalDateTime endTime,
                                 String memo, String placeName, Boolean isEvent, Boolean isAllDay,
                                 String color, Long userId) {
        User user = loadUserPort.loadUser(userId);
        validateScheduleTime(startTime, endTime);

        Schedule schedule = Schedule.builder()
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .memo(memo)
                .user(user)
                .placeName(placeName)
                .isAllDay(isAllDay)
                .isEvent(isEvent)
                .color(Color.toEnum(color))
                .build();

        saveSchedulePort.save(schedule);
    }

    @Override
    public void updateSchedule(Long scheduleId, String title, LocalDateTime startTime, LocalDateTime endTime,
                               String memo, String placeName, Boolean isEvent, Boolean isAllDay,
                               String color, Long userId) {
        loadUserPort.loadUser(userId);
        Schedule schedule = loadSchedulePort.loadByIdAndUserId(scheduleId, userId);

        schedule.update(title, startTime, endTime, memo, isAllDay, Color.toEnum(color), placeName);
    }

    @Override
    public void deleteSchedule(Long scheduleId, Long userId) {
        loadUserPort.loadUser(userId);
        Schedule schedule = loadSchedulePort.loadByIdAndUserId(scheduleId, userId);

        saveSchedulePort.delete(schedule);
    }

    private void validateScheduleTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new ScheduleException(ScheduleErrorCode.END_TIME_BEFORE_START_TIME);
        }
    }
}
