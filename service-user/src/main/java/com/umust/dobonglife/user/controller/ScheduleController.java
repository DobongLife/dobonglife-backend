package com.umust.dobonglife.user.controller;

import com.umust.dobonglife.domain.schedule.application.dto.MonthlyScheduleResult;
import com.umust.dobonglife.domain.schedule.application.dto.UpcomingFestivalDetail;
import com.umust.dobonglife.domain.schedule.application.port.in.FestivalUseCase;
import com.umust.dobonglife.domain.schedule.application.port.in.ScheduleCommandUseCase;
import com.umust.dobonglife.domain.schedule.application.port.in.ScheduleQueryUseCase;
import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleCommandUseCase scheduleCommandUseCase;
    private final ScheduleQueryUseCase scheduleQueryUseCase;
    private final FestivalUseCase festivalUseCase;

    @PostMapping
    public BaseResponse<Void> registerSchedule(@CurrentUserId Long userId, @RequestBody ScheduleRequest request) {
        scheduleCommandUseCase.registerSchedule(
                request.title(), request.startTime(), request.endTime(),
                request.memo(), request.placeName(), request.isEvent(),
                request.isAllDay(), request.color(), userId);
        return BaseResponse.ok(null);
    }

    @GetMapping("/monthly")
    public BaseResponse<MonthlyScheduleResult> getMonthlySchedule(
            @CurrentUserId Long userId, @RequestParam int year, @RequestParam int month) {
        return BaseResponse.ok(scheduleQueryUseCase.getMonthlyScheduleList(userId, year, month));
    }

    @DeleteMapping("/{scheduleId}")
    public BaseResponse<Void> deleteSchedule(@PathVariable Long scheduleId, @CurrentUserId Long userId) {
        scheduleCommandUseCase.deleteSchedule(scheduleId, userId);
        return BaseResponse.ok(null);
    }

    @PatchMapping("/{scheduleId}")
    public BaseResponse<Void> updateSchedule(@PathVariable Long scheduleId, @CurrentUserId Long userId,
                                             @RequestBody ScheduleRequest request) {
        scheduleCommandUseCase.updateSchedule(
                scheduleId, request.title(), request.startTime(), request.endTime(),
                request.memo(), request.placeName(), request.isEvent(),
                request.isAllDay(), request.color(), userId);
        return BaseResponse.ok(null);
    }

    @GetMapping("/festivals/upcoming")
    public BaseResponse<List<UpcomingFestivalDetail>> getUpcomingFestivals() {
        return BaseResponse.ok(festivalUseCase.getUpcomingTop3());
    }

    public record ScheduleRequest(
            String title, LocalDateTime startTime, LocalDateTime endTime,
            String memo, String placeName, Boolean isEvent,
            Boolean isAllDay, String color
    ) {}
}
