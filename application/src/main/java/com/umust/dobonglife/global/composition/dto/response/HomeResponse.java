package com.umust.dobonglife.global.composition.dto.response;

import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.port.dto.content.BannerInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionSummaryInfo;

import java.util.List;

public record HomeResponse(
        List<BannerInfo> banners,
        CursorResponse<PromotionSummaryInfo> promotions
) {}
