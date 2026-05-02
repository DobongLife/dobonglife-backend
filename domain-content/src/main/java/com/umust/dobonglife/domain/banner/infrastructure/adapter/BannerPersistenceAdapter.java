package com.umust.dobonglife.domain.banner.infrastructure.adapter;

import com.umust.dobonglife.domain.banner.application.port.out.LoadBannerPort;
import com.umust.dobonglife.domain.banner.domain.entity.Banner;
import com.umust.dobonglife.domain.banner.infrastructure.jpa.BannerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BannerPersistenceAdapter implements LoadBannerPort {

    private final BannerJpaRepository bannerJpaRepository;

    @Override
    public List<Banner> findActiveBanners() {
        return bannerJpaRepository.findByIsActiveTrueOrderByPriorityAsc();
    }
}
