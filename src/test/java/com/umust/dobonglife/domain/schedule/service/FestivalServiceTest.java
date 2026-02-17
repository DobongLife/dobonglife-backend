package com.umust.dobonglife.domain.schedule.service;

import com.umust.dobonglife.domain.schedule.controller.dto.response.UpcomingFestivalResponse;
import com.umust.dobonglife.domain.schedule.domain.entity.Festival;
import com.umust.dobonglife.domain.schedule.domain.repository.FestivalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FestivalServiceTest {

    @InjectMocks
    private FestivalService festivalService;

    @Mock
    private FestivalRepository festivalRepository;

    private Festival createTestFestival(Long id, String title) {
        Festival festival = Festival.builder()
                .title(title)
                .startDateTime(LocalDateTime.of(2025, 4, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2025, 4, 5, 18, 0))
                .placeName("도봉산")
                .url("https://example.com/festival/" + id)
                .category("축제")
                .build();
        ReflectionTestUtils.setField(festival, "id", id);
        return festival;
    }

    @Nested
    @DisplayName("getUpcomingTop3 - 다가오는 축제 조회")
    class GetUpcomingTop3 {

        @Test
        @DisplayName("다가오는 축제 3개 조회 성공")
        void success() {
            // given
            List<Festival> festivals = List.of(
                    createTestFestival(1L, "도봉구 벚꽃 축제"),
                    createTestFestival(2L, "도봉 문화 한마당"),
                    createTestFestival(3L, "도봉산 등산 축제")
            );

            given(festivalRepository.findByEndDateTimeGreaterThanEqualOrderByStartDateTimeAsc(any(), any()))
                    .willReturn(festivals);

            // when
            List<UpcomingFestivalResponse> result = festivalService.getUpcomingTop3();

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getTitle()).isEqualTo("도봉구 벚꽃 축제");
            assertThat(result.get(0).getFestivalId()).isEqualTo(1L);
            assertThat(result.get(0).getPlaceName()).isEqualTo("도봉산");
        }

        @Test
        @DisplayName("다가오는 축제가 없으면 빈 리스트 반환")
        void emptyResult() {
            // given
            given(festivalRepository.findByEndDateTimeGreaterThanEqualOrderByStartDateTimeAsc(any(), any()))
                    .willReturn(List.of());

            // when
            List<UpcomingFestivalResponse> result = festivalService.getUpcomingTop3();

            // then
            assertThat(result).isEmpty();
        }
    }
}
