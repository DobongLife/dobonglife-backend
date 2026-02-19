package com.umust.dobonglife.domain.coupon.service;

import com.umust.dobonglife.domain.coupon.controller.dto.request.CouponCodeRequest;
import com.umust.dobonglife.domain.coupon.controller.dto.response.MyCouponResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.UsedCouponResponse;
import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import com.umust.dobonglife.domain.coupon.domain.repository.CouponRepository;
import com.umust.dobonglife.domain.coupon.domain.repository.PromotionRepository;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.global.common.model.constant.Category;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    CouponRepository couponRepository;

    @Mock
    PromotionRepository promotionRepository;

    @InjectMocks
    CouponService couponService;

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

    private Coupon createTestCoupon(Long id, Promotion promotion, Long userId) {
        Coupon coupon = Coupon.builder()
                .userId(userId)
                .promotion(promotion)
                .issueStartDate(LocalDate.of(2020, 1, 1))
                .issueEndDate(LocalDate.of(2030, 12, 31))
                .couponStatus(CouponStatus.AVAILABLE)
                .build();
        ReflectionTestUtils.setField(coupon, "id", id);
        return coupon;
    }

    @Nested
    @DisplayName("getMyCoupon - 내 쿠폰 조회")
    class GetMyCoupon {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            Promotion promotion = createTestPromotion(1L);
            Coupon coupon = createTestCoupon(1L, promotion, userId);
            SliceImpl<Coupon> slice = new SliceImpl<>(List.of(coupon));

            given(couponRepository.findCouponsNoOffset(eq(userId), isNull(), any(Pageable.class)))
                    .willReturn(slice);
            given(couponRepository.countByUserIdAndStatus(userId, CouponStatus.AVAILABLE)).willReturn(1);
            given(couponRepository.countByUserIdAndStatus(userId, CouponStatus.USED)).willReturn(0);
            given(couponRepository.countByUserIdAndStatus(userId, CouponStatus.EXPIRED)).willReturn(0);

            // when
            MyCouponResponse result = couponService.getMyCoupon(userId, null, 10);

            // then
            assertThat(result.myCouponStatus().available()).isEqualTo(1);
            assertThat(result.myCouponList().getContent()).hasSize(1);
        }

        @Test
        @DisplayName("빈 목록")
        void 빈_목록() {
            // given
            Long userId = 1L;
            SliceImpl<Coupon> emptySlice = new SliceImpl<>(List.of());

            given(couponRepository.findCouponsNoOffset(eq(userId), isNull(), any(Pageable.class)))
                    .willReturn(emptySlice);
            given(couponRepository.countByUserIdAndStatus(userId, CouponStatus.AVAILABLE)).willReturn(0);
            given(couponRepository.countByUserIdAndStatus(userId, CouponStatus.USED)).willReturn(0);
            given(couponRepository.countByUserIdAndStatus(userId, CouponStatus.EXPIRED)).willReturn(0);

            // when
            MyCouponResponse result = couponService.getMyCoupon(userId, null, 10);

            // then
            assertThat(result.myCouponList().getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("useMyCoupon - 쿠폰 사용")
    class UseMyCoupon {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            Promotion promotion = createTestPromotion(1L);
            Coupon coupon = createTestCoupon(1L, promotion, userId);
            CouponCodeRequest request = new CouponCodeRequest(1L, 1L, "ABC123");

            given(promotionRepository.findById(1L)).willReturn(Optional.of(promotion));
            given(couponRepository.findByUserIdAndCouponId(userId, 1L)).willReturn(Optional.of(coupon));
            given(couponRepository.useIfUsable(userId, 1L)).willReturn(1);

            // when
            UsedCouponResponse result = couponService.useMyCoupon(userId, request);

            // then
            assertThat(result.couponId()).isEqualTo(1L);
            assertThat(result.couponStatus()).isEqualTo(CouponStatus.USED);
        }

        @Test
        @DisplayName("유효하지 않은 코드 예외")
        void 유효하지_않은_코드_예외() {
            // given
            Long userId = 1L;
            Promotion promotion = createTestPromotion(1L);
            CouponCodeRequest request = new CouponCodeRequest(1L, 1L, "WRONG1");

            given(promotionRepository.findById(1L)).willReturn(Optional.of(promotion));

            // when & then
            assertThatThrownBy(() -> couponService.useMyCoupon(userId, request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_CODE);
        }

        @Test
        @DisplayName("프로모션 없음 예외")
        void 프로모션_없음_예외() {
            // given
            Long userId = 1L;
            CouponCodeRequest request = new CouponCodeRequest(1L, 999L, "ABC123");

            given(promotionRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> couponService.useMyCoupon(userId, request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_PROMOTION_ID);
        }

        @Test
        @DisplayName("쿠폰 없음 예외")
        void 쿠폰_없음_예외() {
            // given
            Long userId = 1L;
            Promotion promotion = createTestPromotion(1L);
            CouponCodeRequest request = new CouponCodeRequest(999L, 1L, "ABC123");

            given(promotionRepository.findById(1L)).willReturn(Optional.of(promotion));
            given(couponRepository.findByUserIdAndCouponId(userId, 999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> couponService.useMyCoupon(userId, request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_COUPON_ID);
        }

        @Test
        @DisplayName("사용 불가 예외")
        void 사용_불가_예외() {
            // given
            Long userId = 1L;
            Promotion promotion = createTestPromotion(1L);
            Coupon coupon = createTestCoupon(1L, promotion, userId);
            CouponCodeRequest request = new CouponCodeRequest(1L, 1L, "ABC123");

            given(promotionRepository.findById(1L)).willReturn(Optional.of(promotion));
            given(couponRepository.findByUserIdAndCouponId(userId, 1L)).willReturn(Optional.of(coupon));
            given(couponRepository.useIfUsable(userId, 1L)).willReturn(0);

            // when & then
            assertThatThrownBy(() -> couponService.useMyCoupon(userId, request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.COUPON_CANNOT_USE);
        }
    }

    @Nested
    @DisplayName("createCoupon - 쿠폰 생성")
    class CreateCoupon {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Promotion promotion = createTestPromotion(1L);
            Long userId = 1L;
            LocalDate start = LocalDate.of(2025, 1, 1);

            given(promotionRepository.tryIssueCoupon(eq(1L), any(LocalDate.class))).willReturn(1);
            given(couponRepository.save(any(Coupon.class))).willAnswer(invocation -> {
                Coupon c = invocation.getArgument(0);
                ReflectionTestUtils.setField(c, "id", 1L);
                return c;
            });

            // when
            Long couponId = couponService.createCoupon(promotion, userId, start, 30L);

            // then
            assertThat(couponId).isEqualTo(1L);
            then(couponRepository).should().save(any(Coupon.class));
        }

        @Test
        @DisplayName("기간 외 예외")
        void 기간_외_예외() {
            // given
            Promotion promotion = createTestPromotion(1L);
            // startDate/endDate가 2020~2030이므로 far future로 현재일이 이 범위를 벗어나야 함
            // tryIssueCoupon이 0을 반환하고, today가 범위 내이면 COUPON_SOLD_OUT
            // today가 범위 외이면 PROMOTION_PERIOD_INVALID
            // 날짜를 좁혀서 만듬
            ReflectionTestUtils.setField(promotion, "startDate", LocalDate.of(2020, 1, 1));
            ReflectionTestUtils.setField(promotion, "endDate", LocalDate.of(2020, 1, 2));

            given(promotionRepository.tryIssueCoupon(eq(1L), any(LocalDate.class))).willReturn(0);

            // when & then
            assertThatThrownBy(() -> couponService.createCoupon(promotion, 1L, LocalDate.now(), 30L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.PROMOTION_PERIOD_INVALID);
        }

        @Test
        @DisplayName("재고 없음 예외")
        void 재고_없음_예외() {
            // given
            Promotion promotion = createTestPromotion(1L);
            // 날짜 범위가 넓으므로 today는 범위 안에 들어감 → COUPON_SOLD_OUT

            given(promotionRepository.tryIssueCoupon(eq(1L), any(LocalDate.class))).willReturn(0);

            // when & then
            assertThatThrownBy(() -> couponService.createCoupon(promotion, 1L, LocalDate.now(), 30L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.COUPON_SOLD_OUT);
        }

        @Test
        @DisplayName("종료일 계산 확인")
        void 종료일_계산_확인() {
            // given
            Promotion promotion = createTestPromotion(1L);
            LocalDate start = LocalDate.of(2025, 6, 1);
            long period = 30L;

            given(promotionRepository.tryIssueCoupon(eq(1L), any(LocalDate.class))).willReturn(1);
            given(couponRepository.save(any(Coupon.class))).willAnswer(invocation -> {
                Coupon c = invocation.getArgument(0);
                ReflectionTestUtils.setField(c, "id", 1L);
                // 종료일 확인
                assertThat(c.getIssueEndDate()).isEqualTo(start.plusDays(period));
                return c;
            });

            // when
            couponService.createCoupon(promotion, 1L, start, period);

            // then
            then(couponRepository).should().save(any(Coupon.class));
        }
    }

    @Nested
    @DisplayName("deleteByUserId - 사용자 쿠폰 삭제")
    class DeleteByUserId {

        @Test
        @DisplayName("호출 확인")
        void 호출_확인() {
            // when
            couponService.deleteByUserId(1L);

            // then
            then(couponRepository).should().deleteByUserId(1L);
        }
    }

    @Nested
    @DisplayName("deleteCoupons - 프로모션별 쿠폰 삭제")
    class DeleteCoupons {

        @Test
        @DisplayName("프로모션 있으면 삭제")
        void 프로모션_있으면_삭제() {
            // given
            Promotion promotion = createTestPromotion(1L);

            // when
            couponService.deleteCoupons(List.of(promotion));

            // then
            then(couponRepository).should().deleteAllByPromotionIn(List.of(promotion));
        }

        @Test
        @DisplayName("빈 목록이면 삭제 안함")
        void 빈_목록이면_삭제_안함() {
            // when
            couponService.deleteCoupons(List.of());

            // then
            then(couponRepository).should(never()).deleteAllByPromotionIn(anyList());
        }
    }
}
