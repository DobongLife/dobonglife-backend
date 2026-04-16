package com.umust.dobonglife.presentation.schedule.controller;

import com.umust.dobonglife.application.schedule.service.ScheduleFacade;
import com.umust.dobonglife.application.schedule.service.ScheduleFacade.MonthlyScheduleResponse;
import com.umust.dobonglife.application.schedule.service.ScheduleFacade.UpcomingFestivalResponse;
import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.presentation.schedule.dto.request.ScheduleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "일정 API", description = "일정 관련 API")
@Slf4j
@RequestMapping("/api/schedules")
@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleFacade scheduleFacade;

    @Operation(summary = "일정 등록", description = "일정을 등록합니다. 색 enum은 RED, ORANGE, YELLOW, GREEN, BLUE, BROWN, PINK")
    @ApiResponse(responseCode = "200", description = "일정 등록에 성공하였습니다.")
    @PostMapping
    public BaseResponse<Void> registerSchedule(@CurrentUserId Long userId,
                                               @Valid @RequestBody ScheduleRequest request) {
        scheduleFacade.registerSchedule(
                request.getTitle(), request.getStartTime(), request.getEndTime(),
                request.getMemo(), request.getPlaceName(), request.getIsEvent(),
                request.getIsAllDay(), request.getColor(), userId);
        return BaseResponse.ok(null);
    }

    @Operation(summary = "월간 일정 조회", description = "월간 일정 조회를 합니다.")
    @ApiResponse(responseCode = "200", description = "월간 일정 조회에 성공하였습니다.")
    @GetMapping("/monthly")
    public BaseResponse<MonthlyScheduleResponse> getMonthlySchedule(
            @CurrentUserId Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        return BaseResponse.ok(scheduleFacade.getMonthlySchedule(userId, year, month));
    }

    @Operation(summary = "일정 삭제", description = "일정 삭제를 합니다.")
    @ApiResponse(responseCode = "200", description = "일정 삭제에 성공하였습니다.")
    @DeleteMapping("/{scheduleId}")
    public BaseResponse<Void> deleteSchedule(@PathVariable Long scheduleId,
                                             @CurrentUserId Long userId) {
        scheduleFacade.deleteSchedule(scheduleId, userId);
        return BaseResponse.ok(null);
    }

    @Operation(summary = "일정 수정", description = "일정 수정을 합니다.")
    @ApiResponse(responseCode = "200", description = "일정 수정에 성공하였습니다.")
    @PatchMapping("/{scheduleId}")
    public BaseResponse<Void> updateSchedule(@PathVariable Long scheduleId,
                                             @CurrentUserId Long userId,
                                             @RequestBody ScheduleRequest request) {
        scheduleFacade.updateSchedule(
                scheduleId, request.getTitle(), request.getStartTime(), request.getEndTime(),
                request.getMemo(), request.getPlaceName(), request.getIsEvent(),
                request.getIsAllDay(), request.getColor(), userId);
        return BaseResponse.ok(null);
    }

    @Operation(summary = "다가오는 이벤트 조회", description = "다가오는 이벤트 조회를 합니다.")
    @ApiResponse(responseCode = "200", description = "다가오는 이벤트 조회에 성공하였습니다.")
    @GetMapping("/festivals/upcoming")
    public BaseResponse<List<UpcomingFestivalResponse>> getUpcomingFestivals() {
        return BaseResponse.ok(scheduleFacade.getUpcomingFestivals());
    }
}
