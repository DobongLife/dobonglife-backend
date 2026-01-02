package com.umust.dobonglife.domain.user.controller.dto.response;

import java.time.LocalDateTime;

public record MyPageResponse(
        Profile profile,
        Summary summary
) {

    public record Profile(
            String name,
            String email,
            LocalDateTime joinedAt,
            int balancePoint
    ) {}

    public record Summary(
            int participatedEventCount,
            int couponCount,
            int reviewCount,
            int totalEarnedPoint
    ) {}
}
