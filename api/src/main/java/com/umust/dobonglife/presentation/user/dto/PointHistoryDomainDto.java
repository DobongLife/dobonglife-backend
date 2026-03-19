package com.umust.dobonglife.presentation.user.dto;

import java.time.LocalDateTime;

public record PointHistoryDomainDto(
        String title,
        Long amount,
        LocalDateTime createdAt
) {
}
