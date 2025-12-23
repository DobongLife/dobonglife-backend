package com.umust.dobonglife.domain.place.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThemeRequest {

    @NotNull(message = "주간 테마는 필수입니다")
    @Schema(description = "주간 테마", example = "NATURE, CULTURE, RESTAURANT, HISTORY, FAMILY, ACTIVITY")
    private String theme;
}
