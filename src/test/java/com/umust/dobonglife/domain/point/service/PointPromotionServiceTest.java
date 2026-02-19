package com.umust.dobonglife.domain.point.service;

import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionBannerItem;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
import com.umust.dobonglife.domain.point.controller.dto.response.PointPageResponse;
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
class PointPromotionServiceTest {

    @Mock
    PromotionService promotionService;

    @Mock
    PointService pointService;

    @InjectMocks
    PointPromotionService pointPromotionService;

    @Nested
    @DisplayName("getMyPoint - 포인트 페이지 조회")
    class GetMyPoint {

        @Test
        @DisplayName("포인트와 프로모션 반환")
        void 포인트와_프로모션_반환() {
            // given
            Long userId = 1L;
            CursorResponse<PromotionBannerItem> promotions = new CursorResponse<>(List.of(), false);

            given(pointService.getUserPoint(userId)).willReturn(500L);
            given(promotionService.getPromotionBanner(null, 10)).willReturn(promotions);

            // when
            PointPageResponse result = pointPromotionService.getMyPoint(userId, null, 10);

            // then
            assertThat(result.totalPoint()).isEqualTo(500L);
            assertThat(result.promotionList()).isEqualTo(promotions);
        }

        @Test
        @DisplayName("빈 프로모션 결과")
        void 빈_프로모션_결과() {
            // given
            Long userId = 1L;
            CursorResponse<PromotionBannerItem> emptyPromotions = new CursorResponse<>(List.of(), false);

            given(pointService.getUserPoint(userId)).willReturn(0L);
            given(promotionService.getPromotionBanner(null, 10)).willReturn(emptyPromotions);

            // when
            PointPageResponse result = pointPromotionService.getMyPoint(userId, null, 10);

            // then
            assertThat(result.totalPoint()).isZero();
            assertThat(result.promotionList().getContent()).isEmpty();
        }
    }
}
