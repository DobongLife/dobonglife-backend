package com.umust.dobonglife.domain.course.model;

public enum Theme {
    NATURE, CULTURE, RESTAURANT, HISTORY, FAMILY, ACTIVITY;

    public static Theme toEnum(String value) {
        for (Theme theme : Theme.values()) {
            if (theme.name().equalsIgnoreCase(value)) {
                return theme;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 enum입니다: " + value);
    }
}