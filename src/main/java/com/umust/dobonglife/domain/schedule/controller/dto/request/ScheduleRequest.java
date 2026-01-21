package com.umust.dobonglife.domain.schedule.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleRequest {

    @NotNull(message = "일정 제목은 필수입니다")
    @Schema(description = "일정 제목", example = "독서 회의")
    private String title;

    @NotNull(message = "시작 시간은 필수입니다")
    @Schema(description = "시작 시간", example = "2025-01-10T14:00:00")
    private LocalDateTime startTime;

    @NotNull(message = "종료 시간은 필수입니다")
    @Schema(description = "종료 시간", example = "2025-01-10T16:00:00")
    private LocalDateTime endTime;

    @Schema(description = "메모", example = "노트북 챙겨가기")
    private String memo;

    @Schema(description = "일정 타입", example = "PERSONAL, EVENT, ACTIVITY")
    private String scheduleType;

    @Schema(description = "장소 이름", example = "도봉구 도서관")
    private String placeName;

    @Schema(description = "하루 종일 여부", example = "false")
    private Boolean isAllDay;

    @Schema(description = "일정 색깔", example = "RED")
    private String color;
}
