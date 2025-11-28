package com.umust.dobonglife.domain.schedule.domain.constant;

public enum ScheduleType {
    PERSONAL,
    EVENT,
    ACTIVITY;

    public static ScheduleType toEnum(String value) {
        for (ScheduleType scheduleType : ScheduleType.values()) {
            if (scheduleType.name().equalsIgnoreCase(value)) {
                return scheduleType;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 enum입니다: " + value);
    }

    public String toValue() {
        return name();
    }
}