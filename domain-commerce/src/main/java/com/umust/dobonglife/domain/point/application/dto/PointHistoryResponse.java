package com.umust.dobonglife.domain.point.application.dto;

import com.umust.dobonglife.domain.point.domain.entity.PointHistory;
import com.umust.dobonglife.global.common.Identifiable;

import java.time.LocalDateTime;

public record PointHistoryResponse(
        Long pointHistoryId,
        String title,
        Long amount,
        Long afterBalance,
        boolean isUsed,
        LocalDateTime createdAt
) implements Identifiable {

    public static PointHistoryResponse from(PointHistory history) {
        return new PointHistoryResponse(
                history.getId(),
                history.getTitle(),
                history.getAmount(),
                history.getAfterBalance(),
                history.getIsUsed(),
                history.getCreatedAt()
        );
    }

    @Override
    public Long getId() {
        return pointHistoryId;
    }
}
