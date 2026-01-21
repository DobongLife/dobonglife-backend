package com.umust.dobonglife.domain.schedule.controller;

import com.umust.dobonglife.domain.schedule.controller.dto.request.ScheduleRegisterRequest;
import com.umust.dobonglife.domain.schedule.controller.dto.response.MonthlyScheduleResponse;
import com.umust.dobonglife.domain.schedule.controller.dto.response.ScheduleListResponse;
import com.umust.dobonglife.domain.schedule.service.ScheduleService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "일정 등록", description = "일정을 등록합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "일정 등록에 성공하였습니다."
    )
    @PostMapping
    public BaseResponse<Void> registerSchedule(@CurrentUserId Long userId,
                                               @RequestBody ScheduleRegisterRequest request) {
        scheduleService.registerSchedule(request, userId);
        return BaseResponse.ok(null);
    }
//
//    @Operation(summary = "오늘 일정 조회", description = "오늘 일정 조회를 합니다.")
//    @ApiResponse(
//            responseCode = "200",
//            description = "오늘 일정 조회에 성공하였습니다."
//    )
//    @GetMapping("/today")
//    public BaseResponse<ScheduleListResponse> todaySchedule(@CurrentUserId Long userId) {
//        return BaseResponse.ok(scheduleService.getTodaySchedule(userId));
//    }
//
//    @Operation(summary = "월간 일정 조회", description = "월간 일정 조회를 합니다.")
//    @ApiResponse(
//            responseCode = "200",
//            description = "월간 일정 조회에 성공하였습니다."
//    )
//    @GetMapping("/monthly")
//    public BaseResponse<MonthlyScheduleResponse> getMonthlySchedule(
//            @CurrentUserId Long userId,
//            @RequestParam int year,
//            @RequestParam int month
//    ) {
//        return BaseResponse.ok(scheduleService.getMonthlySchedules(userId, year, month));
//    }
}
