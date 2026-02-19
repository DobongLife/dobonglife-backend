package com.umust.dobonglife.domain.courseLike.service;

import com.umust.dobonglife.domain.courseLike.controller.dto.request.CourseLikeResponse;
import com.umust.dobonglife.domain.courseLike.domain.entity.CourseLike;
import com.umust.dobonglife.domain.courseLike.domain.repository.CourseLikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CourseLikeServiceTest {

    @Mock
    CourseLikeRepository courseLikeRepository;

    @InjectMocks
    CourseLikeService courseLikeService;

    @Nested
    @DisplayName("isCourseFavorite - 코스 좋아요 여부")
    class IsCourseFavorite {

        @Test
        @DisplayName("좋아요 상태 true")
        void 좋아요_상태_true() {
            // given
            given(courseLikeRepository.existsByUserIdAndCourseId(1L, 10L)).willReturn(true);

            // when
            boolean result = courseLikeService.isCourseFavorite(1L, 10L);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("좋아요 상태 false")
        void 좋아요_상태_false() {
            // given
            given(courseLikeRepository.existsByUserIdAndCourseId(1L, 10L)).willReturn(false);

            // when
            boolean result = courseLikeService.isCourseFavorite(1L, 10L);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("updateCourseLike - 코스 좋아요 토글")
    class UpdateCourseLike {

        @Test
        @DisplayName("이미 좋아요면 삭제(해제)")
        void 이미_좋아요면_삭제() {
            // given
            CourseLike existing = new CourseLike(1L, 10L);
            given(courseLikeRepository.findByCourseIdAndUserId(10L, 1L)).willReturn(Optional.of(existing));

            // when
            CourseLikeResponse result = courseLikeService.updateCourseLike(10L, 1L);

            // then
            assertThat(result.isFavorite()).isFalse();
            then(courseLikeRepository).should().delete(existing);
        }

        @Test
        @DisplayName("좋아요 없으면 추가(설정)")
        void 좋아요_없으면_추가() {
            // given
            given(courseLikeRepository.findByCourseIdAndUserId(10L, 1L)).willReturn(Optional.empty());

            // when
            CourseLikeResponse result = courseLikeService.updateCourseLike(10L, 1L);

            // then
            assertThat(result.isFavorite()).isTrue();
            then(courseLikeRepository).should().save(any(CourseLike.class));
        }
    }

    @Nested
    @DisplayName("getFavoriteCourseIds - 좋아요 코스 ID 목록")
    class GetFavoriteCourseIds {

        @Test
        @DisplayName("목록 반환")
        void 목록_반환() {
            // given
            List<Long> courseIds = List.of(1L, 2L, 3L);
            given(courseLikeRepository.findLikedCourseIdsByUserIdAndCourseIds(1L, courseIds))
                    .willReturn(Set.of(1L, 3L));

            // when
            Set<Long> result = courseLikeService.getFavoriteCourseIds(1L, courseIds);

            // then
            assertThat(result).containsExactlyInAnyOrder(1L, 3L);
        }
    }

    @Nested
    @DisplayName("getLikedCourseCount - 좋아요 코스 수")
    class GetLikedCourseCount {

        @Test
        @DisplayName("수 반환")
        void 수_반환() {
            // given
            given(courseLikeRepository.countByUserId(1L)).willReturn(5L);

            // when
            Long result = courseLikeService.getLikedCourseCount(1L);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("0 반환")
        void 영_반환() {
            // given
            given(courseLikeRepository.countByUserId(1L)).willReturn(0L);

            // when
            Long result = courseLikeService.getLikedCourseCount(1L);

            // then
            assertThat(result).isZero();
        }
    }
}
