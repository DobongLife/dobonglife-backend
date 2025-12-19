package com.umust.dobonglife.domain.schedule.domain.constant;

public enum ScheduleType {
    PERSONAL,
    EVENT,
    ACTIVITY;

    public static ScheduleType toEnum(String value) {
        if (value == null) {
            throw new IllegalArgumentException("scheduleType 이 null 입니다");
        }

        String normalized = value.trim(); // 앞뒤 공백 제거

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