package com.umust.dobonglife.domain.business.service;

import com.umust.dobonglife.domain.business.controller.dto.request.BusinessRequest;
import com.umust.dobonglife.domain.business.controller.dto.request.BusinessUpdateRequest;
import com.umust.dobonglife.domain.business.controller.dto.response.BusinessPromotionResponse;
import com.umust.dobonglife.domain.business.controller.dto.response.BusinessResponse;
import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.business.domain.repository.BusinessRepository;
import com.umust.dobonglife.domain.business.service.dto.CouponUsageCount;
import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import com.umust.dobonglife.domain.coupon.domain.repository.CouponRepository;
import com.umust.dobonglife.domain.coupon.domain.repository.PromotionRepository;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.common.model.constant.Category;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.webclient.business.parser.BusinessStatusParser;
import com.umust.dobonglife.global.common.webclient.service.WebClientService;
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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    WebClientService webClientService;

    @Mock
    BusinessStatusParser parser;

    @Mock
    BusinessRepository businessRepository;

    @Mock
    PlaceRepository placeRepository;

    @Mock
    CouponRepository couponRepository;

    @Mock
    PromotionRepository promotionRepository;

    @Mock
    PlaceService placeService;

    @InjectMocks
    BusinessService businessService;

    private User createTestUser(Long id) {
        return User.builder()
                .id(id)
                .email("test@example.com")
                .name("테스트")
                .role(Role.MEMBER)
                .build();
    }

    private Place createTestPlace(Long id) {
        Place place = mock(Place.class);
        lenient().when(place.getId()).thenReturn(id);
        lenient().when(place.getName()).thenReturn("테스트장소");
        lenient().when(place.getSubName()).thenReturn("부제");
        lenient().when(place.getContent()).thenReturn("소개");
        lenient().when(place.getAddress()).thenReturn("서울특별시 도봉구");
        lenient().when(place.getContact()).thenReturn("02-123-4567");
        lenient().when(place.getOperatingHour()).thenReturn("09:00~18:00");
        lenient().when(place.getLatitude()).thenReturn(37.0);
        lenient().when(place.getLongitude()).thenReturn(127.0);
        lenient().when(place.getThumbnailUrl()).thenReturn("thumb.jpg");
        lenient().when(place.getImageUrls()).thenReturn(List.of("img.jpg"));
        lenient().when(place.getCategory()).thenReturn(Category.RESTAURANT);
        lenient().when(place.getThemes()).thenReturn(List.of());
        return place;
    }

    private Business createTestBusiness(Long id, User user, Place place) {
        Business business = Business.builder()
                .businessNumber("1234567890")
                .email("biz@test.com")
                .managerName("매니저")
                .user(user)
                .place(place)
                .build();
        ReflectionTestUtils.setField(business, "id", id);
        return business;
    }

    @Nested
    @DisplayName("registerBusiness - 사업장 등록")
    class RegisterBusiness {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Place place = createTestPlace(10L);

            BusinessRequest request = BusinessRequest.builder()
                    .placeId(10L)
                    .businessName("테스트사업장")
                    .email("biz@test.com")
                    .managerName("매니저")
                    .businessNumber("1234567890")
                    .build();

            Map<String, Object> response = Map.of("status", "valid");
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(webClientService.getCompanyStatus("1234567890")).willReturn(response);
            given(parser.extractStatusCode(response)).willReturn("01");
            given(placeRepository.findById(10L)).willReturn(Optional.of(place));

            // when
            businessService.registerBusiness(request, userId, null);

            // then
            then(businessRepository).should().save(any(Business.class));
            assertThat(user.getRole()).isEqualTo(Role.MANAGER);
        }

        @Test
        @DisplayName("사용자 없음 예외")
        void 사용자_없음_예외() {
            // given
            BusinessRequest request = BusinessRequest.builder()
                    .businessNumber("1234567890")
                    .businessName("사업장")
                    .email("biz@test.com")
                    .managerName("매니저")
                    .build();

            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> businessService.registerBusiness(request, 999L, null))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("유효하지 않은 사업자번호 예외")
        void 유효하지_않은_사업자번호_예외() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);

            BusinessRequest request = BusinessRequest.builder()
                    .placeId(10L)
                    .businessName("사업장")
                    .email("biz@test.com")
                    .managerName("매니저")
                    .businessNumber("0000000000")
                    .build();

            Map<String, Object> response = Map.of("status", "invalid");
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(webClientService.getCompanyStatus("0000000000")).willReturn(response);
            given(parser.extractStatusCode(response)).willReturn("02");

            // when & then
            assertThatThrownBy(() -> businessService.registerBusiness(request, userId, null))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("placeId가 null이면 장소 생성")
        void placeId가_null이면_장소_생성() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Place place = createTestPlace(20L);

            BusinessRequest request = BusinessRequest.builder()
                    .placeId(null)
                    .businessName("사업장")
                    .email("biz@test.com")
                    .managerName("매니저")
                    .businessNumber("1234567890")
                    .build();

            Map<String, Object> response = Map.of("status", "valid");
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(webClientService.getCompanyStatus("1234567890")).willReturn(response);
            given(parser.extractStatusCode(response)).willReturn("01");
            given(placeService.createPlaceForBusiness(eq(request), isNull())).willReturn(20L);
            given(placeRepository.findById(20L)).willReturn(Optional.of(place));

            // when
            businessService.registerBusiness(request, userId, null);

            // then
            then(placeService).should().createPlaceForBusiness(eq(request), isNull());
            then(businessRepository).should().save(any(Business.class));
        }
    }

    @Nested
    @DisplayName("checkBusinessStatus - 사업자 상태 확인")
    class CheckBusinessStatus {

        @Test
        @DisplayName("유효한 사업자번호")
        void 유효한_사업자번호() {
            // given
            Map<String, Object> response = Map.of("status", "valid");
            given(webClientService.getCompanyStatus("1234567890")).willReturn(response);
            given(parser.extractStatusCode(response)).willReturn("01");

            // when & then (예외 없이 통과)
            businessService.checkBusinessStatus("1234567890");
        }

        @Test
        @DisplayName("유효하지 않은 사업자번호")
        void 유효하지_않은_사업자번호() {
            // given
            Map<String, Object> response = Map.of("status", "invalid");
            given(webClientService.getCompanyStatus("0000000000")).willReturn(response);
            given(parser.extractStatusCode(response)).willReturn("02");

            // when & then
            assertThatThrownBy(() -> businessService.checkBusinessStatus("0000000000"))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("getBusinessResponse - 사업장 정보 조회")
    class GetBusinessResponse {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Place place = createTestPlace(10L);
            Business business = createTestBusiness(1L, user, place);

            given(businessRepository.findByUserId(userId)).willReturn(Optional.of(business));

            // when
            BusinessResponse result = businessService.getBusinessResponse(userId);

            // then
            assertThat(result.getBusinessId()).isEqualTo(1L);
            assertThat(result.getManagerName()).isEqualTo("매니저");
        }
    }

    @Nested
    @DisplayName("updateBusiness - 사업장 수정")
    class UpdateBusiness {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Place place = createTestPlace(10L);
            Business business = createTestBusiness(1L, user, place);

            BusinessUpdateRequest request = BusinessUpdateRequest.builder()
                    .businessName("수정된 사업장")
                    .email("new@test.com")
                    .managerName("새매니저")
                    .build();

            given(businessRepository.findByUserId(userId)).willReturn(Optional.of(business));
            given(placeService.resolvePlaceForUpdate(eq(business), eq(request), isNull())).willReturn(place);

            // when
            BusinessResponse result = businessService.updateBusiness(userId, request, null);

            // then
            assertThat(result.getEmail()).isEqualTo("new@test.com");
            assertThat(result.getManagerName()).isEqualTo("새매니저");
        }

        @Test
        @DisplayName("사업장 없음 예외")
        void 사업장_없음_예외() {
            // given
            BusinessUpdateRequest request = BusinessUpdateRequest.builder()
                    .businessName("사업장")
                    .email("biz@test.com")
                    .managerName("매니저")
                    .build();

            given(businessRepository.findByUserId(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> businessService.updateBusiness(999L, request, null))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.BUSINESS_NOT_FOUND);
        }

        @Test
        @DisplayName("소유자 아님 예외")
        void 소유자_아님_예외() {
            // given
            Long userId = 2L;
            User owner = createTestUser(1L);
            Place place = createTestPlace(10L);
            Business business = createTestBusiness(1L, owner, place);

            BusinessUpdateRequest request = BusinessUpdateRequest.builder()
                    .businessName("수정")
                    .email("new@test.com")
                    .managerName("매니저")
                    .build();

            given(businessRepository.findByUserId(userId)).willReturn(Optional.of(business));

            // when & then
            assertThatThrownBy(() -> businessService.updateBusiness(userId, request, null))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.NOT_BUSINESS_OWNER);
        }
    }

    @Nested
    @DisplayName("getBusinessPromotion - 사업장 프로모션 조회")
    class GetBusinessPromotion {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            Promotion promotion = mock(Promotion.class);
            given(promotion.getId()).willReturn(1L);
            given(promotion.getTitle()).willReturn("프로모션");
            given(promotion.getStartDate()).willReturn(LocalDate.of(2020, 1, 1));
            given(promotion.getEndDate()).willReturn(LocalDate.of(2030, 12, 31));
            given(promotion.getDiscountType()).willReturn(DiscountType.PERCENT);
            given(promotion.getDiscountValue()).willReturn(BigDecimal.TEN);
            given(promotion.getTotalQuantity()).willReturn(100L);
            given(promotion.getCode()).willReturn("ABC123");
            given(promotion.getDescription()).willReturn("설명");
            given(promotion.getValidPeriod()).willReturn(30L);

            SliceImpl<Promotion> slice = new SliceImpl<>(List.of(promotion));

            given(promotionRepository.findPromotionNoOffsetByUserId(eq(userId), isNull(), any(Pageable.class)))
                    .willReturn(slice);
            given(couponRepository.countCouponUsageByPromotionIds(List.of(1L)))
                    .willReturn(List.of(new CouponUsageCount(1L, 10L)));

            // when
            CursorResponse<BusinessPromotionResponse> result = businessService.getBusinessPromotion(userId, null, 10);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("프로모션");
        }

        @Test
        @DisplayName("빈 목록")
        void 빈_목록() {
            // given
            Long userId = 1L;
            SliceImpl<Promotion> emptySlice = new SliceImpl<>(List.of());

            given(promotionRepository.findPromotionNoOffsetByUserId(eq(userId), isNull(), any(Pageable.class)))
                    .willReturn(emptySlice);

            // when
            CursorResponse<BusinessPromotionResponse> result = businessService.getBusinessPromotion(userId, null, 10);

            // then
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getBusinessByUser - 사용자별 사업장 조회")
    class GetBusinessByUser {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Place place = createTestPlace(10L);
            Business business = createTestBusiness(1L, user, place);

            given(businessRepository.findByUserId(userId)).willReturn(Optional.of(business));

            // when
            Business result = businessService.getBusinessByUser(userId);

            // then
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("없음 예외")
        void 없음_예외() {
            // given
            given(businessRepository.findByUserId(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> businessService.getBusinessByUser(999L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.BUSINESS_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("returnBusinessPlaceId - 사업장 장소ID 반환")
    class ReturnBusinessPlaceId {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Place place = createTestPlace(10L);
            Business business = createTestBusiness(1L, user, place);

            given(businessRepository.findByUserId(userId)).willReturn(Optional.of(business));

            // when
            Long placeId = businessService.returnBusinessPlaceId(userId);

            // then
            assertThat(placeId).isEqualTo(10L);
        }
    }
}
