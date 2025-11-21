package com.umust.dobonglife.course.presentation.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CoursePlanRequest(
        @NotNull(message = "일정 시간은 필수입니다")
        @Future(message = "일정 시간은 현재 시간 이후여야 합니다")
        LocalDateTime dateTime,

        @NotBlank(message = "일정 제목은 필수입니다")
        @Size(max = 100, message = "일정 제목은 100자를 초과할 수 없습니다")
        String title,

        @Size(max = 500, message = "일정 내용은 500자를 초과할 수 없습니다")
        String content
) {}
