package com.umust.dobonglife.domain.coupon.service;

import com.umust.dobonglife.domain.business.service.BusinessService;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PresetResponse;
import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.domain.repository.PresetRepository;
import com.umust.dobonglife.domain.preset.domain.Preset;
import com.umust.dobonglife.global.common.model.constant.Category;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PreSetServiceTest {

    @Mock
    PresetRepository presetRepository;

    @Mock
    BusinessService businessService;

    @InjectMocks
    PreSetService preSetService;

    private Preset createTestPreset(Long id) {
        Preset preset = mock(Preset.class);
        lenient().when(preset.getId()).thenReturn(id);
        lenient().when(preset.getCategory()).thenReturn(Category.RESTAURANT);
        lenient().when(preset.getDescription()).thenReturn("프리셋 설명");
        lenient().when(preset.getImg()).thenReturn("preset.jpg");
        lenient().when(preset.getDiscountType()).thenReturn(DiscountType.PERCENT);
        lenient().when(preset.getDiscountValue()).thenReturn(BigDecimal.TEN);
        lenient().when(preset.getMinPrice()).thenReturn(1000L);
        lenient().when(preset.getMaxPrice()).thenReturn(5000L);
        lenient().when(preset.getPoint()).thenReturn(100L);
        lenient().when(preset.getValidPeriod()).thenReturn(30L);
        return preset;
    }

    @Nested
    @DisplayName("getPreset - 프리셋 조회")
    class GetPreset {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            Preset preset = createTestPreset(1L);

            given(businessService.getCategoryUserId(userId)).willReturn(Category.RESTAURANT);
            given(presetRepository.findByCategory(Category.RESTAURANT)).willReturn(List.of(preset));

            // when
            PresetResponse result = preSetService.getPreset(userId);

            // then
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.category()).isEqualTo("RESTAURANT");
        }

        @Test
        @DisplayName("프리셋 없음 예외")
        void 프리셋_없음_예외() {
            // given
            Long userId = 1L;

            given(businessService.getCategoryUserId(userId)).willReturn(Category.RESTAURANT);
            given(presetRepository.findByCategory(Category.RESTAURANT)).willReturn(List.of());

            // when & then
            assertThatThrownBy(() -> preSetService.getPreset(userId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("첫 번째 프리셋만 반환")
        void 첫_번째_프리셋만_반환() {
            // given
            Long userId = 1L;
            Preset preset1 = createTestPreset(1L);
            Preset preset2 = createTestPreset(2L);

            given(businessService.getCategoryUserId(userId)).willReturn(Category.RESTAURANT);
            given(presetRepository.findByCategory(Category.RESTAURANT)).willReturn(List.of(preset1, preset2));

            // when
            PresetResponse result = preSetService.getPreset(userId);

            // then
            assertThat(result.id()).isEqualTo(1L);
        }
    }
}
