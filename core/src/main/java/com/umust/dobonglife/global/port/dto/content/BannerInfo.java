package com.umust.dobonglife.global.port.dto.content;

import com.umust.dobonglife.global.common.Identifiable;

public record BannerInfo(
        Long bannerId,
        String title,
        String description,
        String imageUrl,
        String link
) implements Identifiable {
    @Override
    public Long getId() { return bannerId; }
}
