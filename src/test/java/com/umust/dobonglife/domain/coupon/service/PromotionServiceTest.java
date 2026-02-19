package com.umust.dobonglife.domain.coupon.service;

import com.umust.dobonglife.domain.business.service.BusinessService;
import com.umust.dobonglife.domain.coupon.controller.dto.request.PromotionUpdateRequest;
import com.umust.dobonglife.domain.coupon.controller.dto.response.*;
import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import com.umust.dobonglife.domain.coupon.domain.repository.PromotionRepository;
import com.umust.dobonglife.domain.notification.service.NotificationService;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.model.constant.Category;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.external.s3.S3Utils;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    PromotionRepository promotionRepository;

    @Mock
    PointService pointService;

    @Mock
    PlaceService placeService;

    @Mock
    UserService userService;

    @Mock
    BusinessService businessService;

    @Mock
    CouponService couponService;

    @Mock
    S3Utils s3Utils;

    @Mock
    NotificationService notificationService;

    @Mock
    EntityManager entityManager;

    @InjectMocks
    PromotionService promotionService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(promotionService, "entityManager", entityManager);
    }

    private Promotion createTestPromotion(Long id) {
        Place place = mock(Place.class);
        lenient().when(place.getId()).thenReturn(1L);
        lenient().when(place.getName()).thenReturn("테스트장소");
        lenient().when(place.getOperatingHour()).thenReturn("09:00~18:00");

        Promotion promotion = Promotion.builder()
                .category(Category.RESTAURANT)
                .place(place)
                .title("테스트 프로모션")
                .description("설명")
                .discountType(DiscountType.PERCENT)
                .discountValue(BigDecimal.TEN)
                .minPrice(1000L)
                .maxPrice(5000L)
                .code("ABC123")
                .point(100L)
                .totalQuantity(100L)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2030, 12, 31))
                .validPeriod(30L)
                .userId(1L)
                .build();
        ReflectionTestUtils.setField(promotion, "id", id);
        return promotion;
    }

    @Nested
    @DisplayName("registerPromotion - 프로모션 등록")
    class RegisterPromotion {

        @Test
        @DisplayName("저장 확인")
        void 저장_확인() {
            // given
            Promotion promotion = createTestPromotion(1L);

            // when
            promotionService.registerPromotion(promotion);

            // then
            then(promotionRepository).should().save(promotion);
        }
    }

    @Nested
    @DisplayName("getPromotionWithBlocked - 차단 여부 포함 조회")
    class GetPromotionWithBlocked {

        @Test
        @DisplayName("차단 사용자")
        void 차단_사용자() {
            // given
            Long userId = 1L;
            Promotion promotion = createTestPromotion(1L);
            SliceImpl<Promotion> slice = new SliceImpl<>(List.of(promotion));

            given(promotionRepository.findPromotionWithPlaceNoOffset(isNull(), any(Pageable.class)))
                    .willReturn(slice);
            given(userService.isBlockedUser(userId)).willReturn(true);

            // when
            PromotionGetResponse result = promotionService.getPromotionWithBlocked(userId, null, 10);

            // then
            assertThat(result.isBlockedUser()).isTrue();
            assertThat(result.promotions().getContent()).hasSize(1);
        }

        @Test
        @DisplayName("비차단 사용자")
        void 비차단_사용자() {
            // given
            Long userId = 1L;
            SliceImpl<Promotion> slice = new SliceImpl<>(List.of());

            given(promotionRepository.findPromotionWithPlaceNoOffset(isNull(), any(Pageable.class)))
                    .willReturn(slice);
            given(userService.isBlockedUser(userId)).willReturn(false);

            // when
            PromotionGetResponse result = promotionService.getPromotionWithBlocked(userId, null, 10);

            // then
            assertThat(result.isBlockedUser()).isFalse();
        }
    }

    @Nested
    @DisplayName("getPromotion - 프로모션 목록 조회")
    class GetPromotion {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Promotion promotion = createTestPromotion(1L);
            SliceImpl<Promotion> slice = new SliceImpl<>(List.of(promotion));

            given(promotionRepository.findPromotionWithPlaceNoOffset(isNull(), any(Pageable.class)))
                    .willReturn(slice);

            // when
            CursorResponse<PromotionItem> result = promotionService.getPromotion(null, 10);

            // then
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("빈 결과")
        void 빈_결과() {
            // given
            SliceImpl<Promotion> emptySlice = new SliceImpl<>(List.of());

            given(promotionRepository.findPromotionWithPlaceNoOffset(isNull(), any(Pageable.class)))
                    .willReturn(emptySlice);

            // when
            CursorResponse<PromotionItem> result = promotionService.getPromotion(null, 10);

            // then
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getPromotionSummary - 프로모션 요약 조회")
    class GetPromotionSummary {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Promotion promotion = createTestPromotion(1L);
            SliceImpl<Promotion> slice = new SliceImpl<>(List.of(promotion));

            given(promotionRepository.findPromotionWithPlaceNoOffset(isNull(), any(Pageable.class)))
                    .willReturn(slice);

            // when
            CursorResponse<PromotionSummaryItem> result = promotionService.getPromotionSummary(null, 10);

            // then
            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("changePointToCoupon - 포인트로 쿠폰 교환")
    class ChangePointToCoupon {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            Promotion promotion = createTestPromotion(1L);
            User user = User.builder().id(userId).name("테스트").fcmToken("token").build();

            given(userService.findById(userId)).willReturn(user);
            given(promotionRepository.findById(1L)).willReturn(Optional.of(promotion));
            given(pointService.processUserPoint(userId, 100L)).willReturn(true);
            given(couponService.createCoupon(eq(promotion), eq(userId), any(), anyLong())).willReturn(10L);

            // when
            UsedCouponResponse result = promotionService.changePointToCoupon(userId, 1L);

            // then
            assertThat(result.couponId()).isEqualTo(10L);
            assertThat(result.couponStatus()).isEqualTo(CouponStatus.AVAILABLE);
            then(pointService).should().usePoint(eq(userId), anyString(), eq(100L));
        }

        @Test
        @DisplayName("프로모션 없음 예외")
        void 프로모션_없음_예외() {
            // given
            Long userId = 1L;
            User user = User.builder().id(userId).name("테스트").build();

            given(userService.findById(userId)).willReturn(user);
            given(promotionRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> promotionService.changePointToCoupon(userId, 999L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.PROMOTION_NOT_FOUND);
        }

        @Test
        @DisplayName("포인트 부족 예외")
        void 포인트_부족_예외() {
            // given
            Long userId = 1L;
            Promotion promotion = createTestPromotion(1L);
            User user = User.builder().id(userId).name("테스트").build();

            given(userService.findById(userId)).willReturn(user);
            given(promotionRepository.findById(1L)).willReturn(Optional.of(promotion));
            given(pointService.processUserPoint(userId, 100L)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> promotionService.changePointToCoupon(userId, 1L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_POINT);
        }

        @Test
        @DisplayName("교환 제한 예외")
        void 교환_제한_예외() {
            // given
            Long userId = 1L;
            User user = User.builder().id(userId).name("테스트").build();

            given(userService.findById(userId)).willReturn(user);
            willThrow(new BusinessException(ErrorCode.COUPON_EXCHANGE_RESTRICTED))
                    .given(userService).canExchangeCoupon(userId);

            // when & then
            assertThatThrownBy(() -> promotionService.changePointToCoupon(userId, 1L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.COUPON_EXCHANGE_RESTRICTED);
        }
    }

    @Nested
    @DisplayName("updateCoupon - 프로모션 수정")
    class UpdateCoupon {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            Promotion promotion = createTestPromotion(1L);
            PromotionUpdateRequest request = new PromotionUpdateRequest("수정된 이름", "수정 설명", 200L);

            given(promotionRepository.findById(1L)).willReturn(Optional.of(promotion));

            // when
            PromotionUpdateResponse result = promotionService.updateCoupon(request, 1L, userId);

            // then
            assertThat(result.couponName()).isEqualTo("수정된 이름");
            assertThat(result.totalQuantity()).isEqualTo(200L);
        }

        @Test
        @DisplayName("프로모션 없음 예외")
        void 프로모션_없음_예외() {
            // given
            PromotionUpdateRequest request = new PromotionUpdateRequest("이름", "설명", 100L);

            given(promotionRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> promotionService.updateCoupon(request, 999L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_PROMOTION_ID);
        }

        @Test
        @DisplayName("소유자 아님 예외")
        void 소유자_아님_예외() {
            // given
            Promotion promotion = createTestPromotion(1L); // userId = 1L
            PromotionUpdateRequest request = new PromotionUpdateRequest("이름", "설명", 200L);

            given(promotionRepository.findById(1L)).willReturn(Optional.of(promotion));

            // when & then
            assertThatThrownBy(() -> promotionService.updateCoupon(request, 1L, 2L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.BUSINESS_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("getPromotionBanner - 프로모션 배너 조회")
    class GetPromotionBanner {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Promotion promotion = createTestPromotion(1L);
            SliceImpl<Promotion> slice = new SliceImpl<>(List.of(promotion));

            given(promotionRepository.findPromotionBannersNoOffset(isNull(), any(Pageable.class)))
                    .willReturn(slice);

            // when
            CursorResponse<PromotionBannerItem> result = promotionService.getPromotionBanner(null, 10);

            // then
            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("deleteByUserId - 사용자 프로모션 삭제")
    class DeleteByUserId {

        @Test
        @DisplayName("프로모션 있으면 삭제")
        void 프로모션_있으면_삭제() {
            // given
            Long userId = 1L;
            Promotion promotion = createTestPromotion(1L);

            given(promotionRepository.findAllByUserId(userId)).willReturn(List.of(promotion));

            // when
            promotionService.deleteByUserId(userId);

            // then
            then(couponService).should().deleteCoupons(List.of(promotion));
            then(promotionRepository).should().clearPlaceByUserId(userId);
            then(promotionRepository).should().deleteAllInBatch(List.of(promotion));
        }

        @Test
        @DisplayName("빈 목록이면 삭제 안함")
        void 빈_목록이면_삭제_안함() {
            // given
            Long userId = 1L;

            given(promotionRepository.findAllByUserId(userId)).willReturn(Collections.emptyList());

            // when
            promotionService.deleteByUserId(userId);

            // then
            then(couponService).should(never()).deleteCoupons(anyList());
            then(promotionRepository).should(never()).deleteAllInBatch(anyList());
        }
    }
}
