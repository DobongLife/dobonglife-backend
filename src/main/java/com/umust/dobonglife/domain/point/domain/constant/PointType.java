package com.umust.dobonglife.domain.point.domain.constant;

import com.umust.dobonglife.domain.schedule.domain.constant.ScheduleType;

public enum PointType {
    EARNED,
    USED,
    EXPIRED;

    public static ScheduleType toEnum(String value) {
        if (value == null) {
            throw new IllegalArgumentException("pointType 이 null 입니다");
        }

        String normalized = value.trim();

        for (ScheduleType scheduleType : ScheduleType.values()) {
            if (scheduleType.name().equalsIgnoreCase(normalized)) {
                return scheduleType;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 enum입니다: " + value);
    }

    public String toValue() {
        return name();
    }
}
