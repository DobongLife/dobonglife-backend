package com.umust.dobonglife.global.common.response.slice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class Cursor {

    private final LocalDateTime createdAt;
    private final Long id;

    /** response용 */
    public String encode() {
        return createdAt.toString() + "|" + id;
    }

    /** request용 */
    public static Cursor decode(String cursor) {
        if (cursor == null || cursor.isBlank()) return null;

        String[] parts = cursor.split("\\|");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid cursor format");
        }

        return new Cursor(
                LocalDateTime.parse(parts[0]),
                Long.parseLong(parts[1])
        );
    }
}
