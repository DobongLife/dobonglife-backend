package com.umust.dobonglife.global.common.response.slice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Cursor {
    private final Long lastId;
    public static Cursor from(String raw) {
        if (raw == null || raw.isBlank()) return null;
        return new Cursor(Long.parseLong(raw));
    }

    public String encode() {
        return String.valueOf(lastId);
    }
}
