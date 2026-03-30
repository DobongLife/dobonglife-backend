package com.umust.dobonglife.domain.banner.application.port.out;

import com.umust.dobonglife.domain.banner.domain.entity.Banner;

import java.util.List;

public interface LoadBannerPort {

    List<Banner> findActiveBanners();
}
