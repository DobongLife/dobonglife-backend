package com.umust.dobonglife.domain.banner.application.dto;

import com.umust.dobonglife.domain.banner.domain.entity.Banner;
import com.umust.dobonglife.global.common.Identifiable;

public record BannerResponse(
        Long bannerId,
        String title,
        String description,
        String imageUrl,
        String link
) implements Identifiable {

    public static BannerResponse from(Banner banner) {
        return new BannerResponse(
                banner.getId(),
                banner.getTitle(),
                banner.getDescription(),
                banner.getImageUrl(),
                banner.getLink()
        );
    }

    @Override
    public Long getId() {
        return bannerId;
    }
}
