package com.umust.dobonglife.global.common.response.slice;

public enum SortOrder {
    ASC, DESC;

    public static SortOrder from(String value) {
        if (value == null) return DESC; // 기본값
        return SortOrder.valueOf(value.toUpperCase());
    }
}
