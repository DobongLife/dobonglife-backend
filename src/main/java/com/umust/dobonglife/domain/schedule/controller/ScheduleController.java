package com.umust.dobonglife.domain.schedule.controller;

import com.umust.dobonglife.domain.schedule.controller.dto.request.ScheduleRegisterRequest;
import com.umust.dobonglife.domain.schedule.controller.dto.response.MonthlyScheduleResponse;
import com.umust.dobonglife.domain.schedule.controller.dto.response.ScheduleListResponse;
import com.umust.dobonglife.domain.schedule.service.ScheduleService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/schedule")
@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public BaseResponse<Void> registerSchedule(@RequestBody ScheduleRegisterRequest request) {
        scheduleService.registerSchedule(request, 1L);
        return BaseResponse.ok(null);
    }

    @GetMapping("/today")
    public BaseResponse<ScheduleListResponse> todaySchedule() {
        return BaseResponse.ok(scheduleService.getTodaySchedule(1L));
    }

    @GetMapping("/monthly")
    public BaseResponse<MonthlyScheduleResponse> getMonthlySchedule(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return BaseResponse.ok(scheduleService.getMonthlySchedules(1L, year, month));
    }
}
