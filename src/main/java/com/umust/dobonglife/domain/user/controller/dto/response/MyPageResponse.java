package com.umust.dobonglife.domain.user.controller.dto.response;

import java.util.List;

public record MyPageResponse(
        String name,
        String email,
        String joinDate,
        ActivitySummary activity,
        Long currentPoint,
        List<PointHistoryDto> recentHistories
) {
    public record ActivitySummary(
            int eventCount,
            int couponCount,
            int reviewCount,
            Long totalEarnedPoint
    ) {}

    public record PointHistoryDto(
            String title,
            String date,
            Long amount,
            String type
    ) {}
}
