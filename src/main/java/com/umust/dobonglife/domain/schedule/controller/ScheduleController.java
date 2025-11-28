package com.umust.dobonglife.domain.schedule.controller;

import com.umust.dobonglife.domain.schedule.dto.request.ScheduleRegisterRequest;
import com.umust.dobonglife.domain.schedule.service.ScheduleService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/schedule")
@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public BaseResponse<Void> registerSchedule(ScheduleRegisterRequest request) {
        scheduleService.registerSchedule(request, 1L);
        return BaseResponse.ok(null);
    }





}
