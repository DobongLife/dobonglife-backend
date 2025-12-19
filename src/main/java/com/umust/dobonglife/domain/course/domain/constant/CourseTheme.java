package com.umust.dobonglife.domain.course.domain.constant;

import lombok.Getter;

@Getter
public enum CourseTheme {
    NATURE, CULTURE, RESTAURANT, HISTORY, FAMILY, ACTIVITY;

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
