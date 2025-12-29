package com.umust.dobonglife.domain.user.controller.dto.response;

public record MyPageResponse(
        Profile profile,
        Summary summary
) {

    public record Profile(
            String name,
            String email,
            String joinedAt,
            int balancePoint
    ) {}

    public record Summary(
            int participatedEventCount,
            int couponCount,
            int reviewCount,
            int totalEarnedPoint
    ) {}
}
