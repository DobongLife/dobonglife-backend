package com.umust.dobonglife.domain.schedule.domain.constant;

import lombok.Getter;

@Getter
public enum Color {

    RED,
    ORANGE,
    YELLOW,
    GREEN,
    BLUE,
    BROWN;

    public static Color toEnum(String value) {
        if (value == null) {
            throw new IllegalArgumentException("color 이 null 입니다");
        }

        String normalized = value.trim(); // 앞뒤 공백 제거

        for (Color color : Color.values()) {
            if (color.name().equalsIgnoreCase(normalized)) {
                return color;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 enum입니다: " + value);
    }
}