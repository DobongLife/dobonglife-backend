package com.umust.dobonglife.domain.home.service;

import com.umust.dobonglife.domain.banners.service.BannerService;
import com.umust.dobonglife.domain.banners.service.dto.BannerSummaryResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionSummaryItem;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.domain.home.controller.dto.response.HomeSummaryResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class HomeServiceTest {

    @Mock
    CourseService courseService;

    @Mock
    PromotionService promotionService;

    @Mock
    BannerService bannerService;

    @InjectMocks
    HomeService homeService;

    @Nested
    @DisplayName("getHomeSummary - 홈 요약 조회")
    class GetHomeSummary {

        @Test
        @DisplayName("프로모션과 배너 포함 성공")
        void 프로모션과_배너_포함_성공() {
            // given
            CursorResponse<PromotionSummaryItem> promotions = new CursorResponse<>(List.of(), false);
            CursorResponse<BannerSummaryResponse> banners = new CursorResponse<>(List.of(), false);

            given(promotionService.getPromotionSummary(null, 10)).willReturn(promotions);
            given(bannerService.getBanners()).willReturn(banners);

            // when
            HomeSummaryResponse result = homeService.getHomeSummary(null, 10);

            // then
            assertThat(result.promotions()).isEqualTo(promotions);
            assertThat(result.banners()).isEqualTo(banners);
        }

        @Test
        @DisplayName("빈 프로모션 결과")
        void 빈_프로모션_결과() {
            // given
            CursorResponse<PromotionSummaryItem> emptyPromotions = new CursorResponse<>(List.of(), false);
            CursorResponse<BannerSummaryResponse> banners = new CursorResponse<>(List.of(), false);

            given(promotionService.getPromotionSummary(null, 10)).willReturn(emptyPromotions);
            given(bannerService.getBanners()).willReturn(banners);

            // when
            HomeSummaryResponse result = homeService.getHomeSummary(null, 10);

            // then
            assertThat(result.promotions().getContent()).isEmpty();
        }

        @Test
        @DisplayName("lastId 전달 확인")
        void lastId_전달_확인() {
            // given
            CursorResponse<PromotionSummaryItem> promotions = new CursorResponse<>(List.of(), false);
            CursorResponse<BannerSummaryResponse> banners = new CursorResponse<>(List.of(), false);

            given(promotionService.getPromotionSummary(5L, 10)).willReturn(promotions);
            given(bannerService.getBanners()).willReturn(banners);

            // when
            homeService.getHomeSummary(5L, 10);

            // then
            then(promotionService).should().getPromotionSummary(5L, 10);
        }
    }
}
