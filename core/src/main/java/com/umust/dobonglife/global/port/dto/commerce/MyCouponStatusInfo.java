package com.umust.dobonglife.global.port.dto.commerce;

public record MyCouponStatusInfo(
        Long available,
        Long used,
        Long expired
) {}
