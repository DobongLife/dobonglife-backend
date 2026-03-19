package com.umust.dobonglife.domain.banner.application;

import com.umust.dobonglife.domain.banner.application.dto.BannerResponse;
import com.umust.dobonglife.domain.banner.domain.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService {

    private final BannerRepository bannerRepository;

    public List<BannerResponse> getActiveBanners() {
        return bannerRepository.findByIsActiveTrueOrderByPriorityAsc().stream()
                .map(BannerResponse::from)
                .toList();
    }
}
