package com.umust.dobonglife.domain.course.domain.constant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "코스 테마")
public enum CourseTheme {
    @Schema(description = "역사 여행")
    HISTORY,

    @Schema(description = "문화 체험")
    CULTURE,

    @Schema(description = "자연 힐링")
    NATURE,

    @Schema(description = "맛집 탐방")
    RESTAURANT,

    @Schema(description = "가족 나들이")
    FAMILY,

    @Schema(description = "액티비티")
    ACTIVITY;

    public static CourseTheme toEnum(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("값이 비어 있습니다.");
        }

        for (CourseTheme theme : CourseTheme.values()) {
            if (theme.name().equalsIgnoreCase(value)) {
                return theme;
            }
        }

        throw new IllegalArgumentException("존재하지 않는 CourseTheme 값입니다: " + value);
    }
}
