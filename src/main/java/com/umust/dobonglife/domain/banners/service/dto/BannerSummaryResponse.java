package com.umust.dobonglife.domain.banners.service.dto;

import com.umust.dobonglife.domain.banners.domain.entity.Banner;
import com.umust.dobonglife.global.common.Identifiable;

public record BannerSummaryResponse (Long id, String title, String description, String link) implements Identifiable
{
    public static BannerSummaryResponse from(Banner banner) {
        return new BannerSummaryResponse(
                banner.getId(),
                banner.getTitle(),
                banner.getDescription(),
                banner.getLink()
        );
    }

    @Override
    public Long getId() {
        return id;
    }
}
