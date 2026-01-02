package com.umust.dobonglife.domain.course.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "코스 일정 정보")
public class CoursePlanRequest {

        @Schema(
                description = "일정 날짜 및 시간",
                example = "2026-01-02T14:30:00",
                required = true,
                type = "string",
                format = "date-time"
        )
        @NotNull(message = "날짜는 필수입니다")
        private LocalDateTime dateTime;

        @Schema(
                description = "일정 제목",
                example = "도봉서원 방문",
                required = true,
                maxLength = 100
        )
        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
        private String title;

        @Schema(
                description = "일정 상세 내용",
                example = "조선시대 서원의 건축양식과 역사를 살펴봅니다",
                required = true,
                minLength = 10
        )
        @NotBlank(message = "내용은 필수입니다")
        @Size(min = 10, message = "내용은 최소 10자 이상이어야 합니다")
        private String content;
}