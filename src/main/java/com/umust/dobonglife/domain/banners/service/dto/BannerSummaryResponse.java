package com.umust.dobonglife.domain.banners.service.dto;

import com.umust.dobonglife.domain.banners.domain.entity.Banner;

public record BannerSummaryResponse (Long id, String title, String description, String link)
{
    public static BannerSummaryResponse from(Banner banner) {
        return new BannerSummaryResponse(
                banner.getId(),
                banner.getTitle(),
                banner.getDescription(),
                banner.getLink()
        );
    }
}
