package com.umust.dobonglife.domain.banner.presentation;

import com.umust.dobonglife.domain.banner.application.port.in.GetBannerUseCase;
import com.umust.dobonglife.domain.banner.application.dto.BannerResponse;
import com.umust.dobonglife.global.port.dto.content.BannerInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/banner")
@RequiredArgsConstructor
public class InternalBannerController {

    private final GetBannerUseCase getBannerUseCase;

    @GetMapping("/active")
    public List<BannerInfo> getActiveBanners() {
        return getBannerUseCase.getActiveBanners().stream()
                .map(b -> new BannerInfo(b.bannerId(), b.title(), b.description(), b.imageUrl(), b.link()))
                .toList();
    }
}
