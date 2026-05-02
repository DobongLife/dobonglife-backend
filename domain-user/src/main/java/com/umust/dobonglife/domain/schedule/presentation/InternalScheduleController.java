package com.umust.dobonglife.domain.schedule.presentation;

import com.umust.dobonglife.domain.schedule.application.dto.MonthlyScheduleResult;
import com.umust.dobonglife.domain.schedule.application.dto.UpcomingFestivalDetail;
import com.umust.dobonglife.domain.schedule.application.port.in.FestivalUseCase;
import com.umust.dobonglife.domain.schedule.application.port.in.ScheduleCommandUseCase;
import com.umust.dobonglife.domain.schedule.application.port.in.ScheduleQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/internal/schedule")
@RequiredArgsConstructor
public class InternalScheduleController {

    private final ScheduleCommandUseCase scheduleCommandUseCase;
    private final ScheduleQueryUseCase scheduleQueryUseCase;
    private final FestivalUseCase festivalUseCase;

    @PostMapping
    public void registerSchedule(
            @RequestParam String title,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime,
            @RequestParam(required = false) String memo,
            @RequestParam(required = false) String placeName,
            @RequestParam(required = false) Boolean isEvent,
            @RequestParam(required = false) Boolean isAllDay,
            @RequestParam(required = false) String color,
            @RequestParam Long userId) {
        scheduleCommandUseCase.registerSchedule(title, startTime, endTime, memo, placeName, isEvent, isAllDay, color, userId);
    }

    @PatchMapping("/{scheduleId}")
    public void updateSchedule(
            @PathVariable Long scheduleId,
            @RequestParam String title,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime,
            @RequestParam(required = false) String memo,
            @RequestParam(required = false) String placeName,
            @RequestParam(required = false) Boolean isEvent,
            @RequestParam(required = false) Boolean isAllDay,
            @RequestParam(required = false) String color,
            @RequestParam Long userId) {
        scheduleCommandUseCase.updateSchedule(scheduleId, title, startTime, endTime, memo, placeName, isEvent, isAllDay, color, userId);
    }

    @DeleteMapping("/{scheduleId}")
    public void deleteSchedule(@PathVariable Long scheduleId, @RequestParam Long userId) {
        scheduleCommandUseCase.deleteSchedule(scheduleId, userId);
    }

    @GetMapping("/monthly")
    public MonthlyScheduleResult getMonthlySchedule(
            @RequestParam Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        return scheduleQueryUseCase.getMonthlyScheduleList(userId, year, month);
    }

    @GetMapping("/festivals/upcoming")
    public List<UpcomingFestivalDetail> getUpcomingFestivals() {
        return festivalUseCase.getUpcomingTop3();
    }
}
