package com.umust.dobonglife.global.port.content;

import com.umust.dobonglife.global.port.dto.content.BannerInfo;

import java.util.List;

public interface BannerPort {
    List<BannerInfo> getActiveBanners();
}
