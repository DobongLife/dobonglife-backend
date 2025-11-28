package com.umust.dobonglife.domain.schedule.controller;

import com.umust.dobonglife.domain.schedule.controller.dto.request.ScheduleRegisterRequest;
import com.umust.dobonglife.domain.schedule.service.ScheduleService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/schedule")
@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public BaseResponse<Void> registerSchedule(@RequestBody ScheduleRegisterRequest request) {
        log.info("대체 왜 null인가?: {}", request.getScheduleType());
        scheduleService.registerSchedule(request, 1L);
        return BaseResponse.ok(null);
    }
}
