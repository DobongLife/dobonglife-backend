package com.umust.dobonglife.domain.banner.application.port.in;

import com.umust.dobonglife.domain.banner.application.dto.BannerResponse;

import java.util.List;

public interface GetBannerUseCase {

    List<BannerResponse> getActiveBanners();
}
