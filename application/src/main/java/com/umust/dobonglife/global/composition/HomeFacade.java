package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.domain.banner.application.BannerService;
import com.umust.dobonglife.domain.banner.application.dto.BannerResponse;
import com.umust.dobonglife.domain.promotion.application.PromotionService;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionSummary;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.composition.dto.response.HomeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HomeFacade {

    private final BannerService bannerService;
    private final PromotionService promotionService;

    public HomeResponse getHome(Long lastId, int size) {
        List<BannerResponse> banners = bannerService.getActiveBanners();
        CursorResponse<PromotionSummary> promotions = promotionService.getPromotions(lastId, size);

        return new HomeResponse(banners, promotions);
    }
}
