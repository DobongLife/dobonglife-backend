package com.umust.dobonglife.domain.banners.service;

import com.umust.dobonglife.domain.banners.domain.entity.Banner;
import com.umust.dobonglife.domain.banners.domain.repository.BannerRepository;
import com.umust.dobonglife.domain.banners.service.dto.BannerSummaryResponse;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    @Transactional(readOnly = true)
    public CursorResponse<BannerSummaryResponse> getBanners() {
        List<Banner> banners = bannerRepository.findTop3ByOrderByPriorityAsc();

        return convertToBannerResponse(banners);
    }

    private CursorResponse<BannerSummaryResponse> convertToBannerResponse(List<Banner> banners) {
        List<BannerSummaryResponse> content = banners.stream()
                .map(BannerSummaryResponse::from)
                .toList();
        return new CursorResponse<>(content, false);
    }
}
