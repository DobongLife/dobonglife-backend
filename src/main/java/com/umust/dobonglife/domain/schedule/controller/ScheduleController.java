package com.umust.dobonglife.domain.schedule.controller;

import com.umust.dobonglife.domain.schedule.controller.dto.request.ScheduleRegisterRequest;
import com.umust.dobonglife.domain.schedule.controller.dto.response.MonthlyScheduleResponse;
import com.umust.dobonglife.domain.schedule.controller.dto.response.ScheduleListResponse;
import com.umust.dobonglife.domain.schedule.service.ScheduleService;
import com.umust.dobonglife.global.common.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "일정 API", description = "일정 관련 API")
@Slf4j
@RequestMapping("/api/schedule")
@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public BaseResponse<Void> registerSchedule(@CurrentUserId Long userId,
                                               @RequestBody ScheduleRegisterRequest request) {
        scheduleService.registerSchedule(request, userId);
        return BaseResponse.ok(null);
    }

    @GetMapping("/today")
    public BaseResponse<ScheduleListResponse> todaySchedule(@CurrentUserId Long userId) {
        return BaseResponse.ok(scheduleService.getTodaySchedule(userId));
    }

    @GetMapping("/monthly")
    public BaseResponse<MonthlyScheduleResponse> getMonthlySchedule(
            @CurrentUserId Long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return BaseResponse.ok(scheduleService.getMonthlySchedules(userId, year, month));
    }
}
