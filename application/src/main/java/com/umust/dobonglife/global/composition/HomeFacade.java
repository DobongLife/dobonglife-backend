package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.composition.dto.response.HomeResponse;
import com.umust.dobonglife.global.port.content.BannerPort;
import com.umust.dobonglife.global.port.commerce.PromotionPort;
import com.umust.dobonglife.global.port.dto.content.BannerInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionSummaryInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HomeFacade {

    private final BannerPort bannerPort;
    private final PromotionPort promotionPort;

    public HomeResponse getHome(Long lastId, int size) {
        List<BannerInfo> banners = bannerPort.getActiveBanners();
        CursorResponse<PromotionSummaryInfo> promotions = promotionPort.getPromotions(lastId, size);

        return new HomeResponse(banners, promotions);
    }
}
