package com.umust.dobonglife.domain.banner.application;

import com.umust.dobonglife.domain.banner.application.dto.BannerResponse;
import com.umust.dobonglife.domain.banner.application.port.in.GetBannerUseCase;
import com.umust.dobonglife.domain.banner.application.port.out.LoadBannerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService implements GetBannerUseCase {

    private final LoadBannerPort loadBannerPort;

    @Override
    public List<BannerResponse> getActiveBanners() {
        return loadBannerPort.findActiveBanners().stream()
                .map(BannerResponse::from)
                .toList();
    }
}
