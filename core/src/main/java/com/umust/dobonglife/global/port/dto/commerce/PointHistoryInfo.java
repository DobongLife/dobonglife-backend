package com.umust.dobonglife.global.port.dto.commerce;

import com.umust.dobonglife.global.common.Identifiable;

import java.time.LocalDateTime;

public record PointHistoryInfo(
        Long pointHistoryId,
        String title,
        Long amount,
        Long afterBalance,
        boolean isUsed,
        LocalDateTime createdAt
) implements Identifiable {
    @Override
    public Long getId() { return pointHistoryId; }
}
