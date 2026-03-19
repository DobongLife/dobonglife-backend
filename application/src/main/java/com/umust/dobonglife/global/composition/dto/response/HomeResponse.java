package com.umust.dobonglife.global.composition.dto.response;

import com.umust.dobonglife.domain.banner.application.dto.BannerResponse;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionSummary;
import com.umust.dobonglife.global.common.response.CursorResponse;

import java.util.List;

public record HomeResponse(
        List<BannerResponse> banners,
        CursorResponse<PromotionSummary> promotions
) {}
