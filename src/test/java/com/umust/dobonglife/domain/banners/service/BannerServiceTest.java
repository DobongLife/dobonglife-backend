package com.umust.dobonglife.domain.banners.service;

import com.umust.dobonglife.domain.banners.domain.entity.Banner;
import com.umust.dobonglife.domain.banners.domain.repository.BannerRepository;
import com.umust.dobonglife.domain.banners.service.dto.BannerSummaryResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BannerServiceTest {

    @Mock
    BannerRepository bannerRepository;

    @InjectMocks
    BannerService bannerService;

    private Banner createTestBanner(Long id, String title) {
        Banner banner = Banner.builder()
                .title(title)
                .description("설명")
                .imageUrl("img.jpg")
                .link("https://example.com")
                .priority(1)
                .isActive(true)
                .build();
        ReflectionTestUtils.setField(banner, "id", id);
        return banner;
    }

    @Nested
    @DisplayName("getBanners - 배너 조회")
    class GetBanners {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Banner banner1 = createTestBanner(1L, "배너1");
            Banner banner2 = createTestBanner(2L, "배너2");

            given(bannerRepository.findTop3ByOrderByPriorityAsc())
                    .willReturn(List.of(banner1, banner2));

            // when
            CursorResponse<BannerSummaryResponse> result = bannerService.getBanners();

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.isHasNext()).isFalse();
        }

        @Test
        @DisplayName("빈 결과")
        void 빈_결과() {
            // given
            given(bannerRepository.findTop3ByOrderByPriorityAsc()).willReturn(List.of());

            // when
            CursorResponse<BannerSummaryResponse> result = bannerService.getBanners();

            // then
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("최대 3개 반환")
        void 최대_3개_반환() {
            // given
            Banner banner1 = createTestBanner(1L, "배너1");
            Banner banner2 = createTestBanner(2L, "배너2");
            Banner banner3 = createTestBanner(3L, "배너3");

            given(bannerRepository.findTop3ByOrderByPriorityAsc())
                    .willReturn(List.of(banner1, banner2, banner3));

            // when
            CursorResponse<BannerSummaryResponse> result = bannerService.getBanners();

            // then
            assertThat(result.getContent()).hasSize(3);
        }
    }
}
