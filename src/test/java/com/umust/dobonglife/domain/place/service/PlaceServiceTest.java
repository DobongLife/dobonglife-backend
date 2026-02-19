package com.umust.dobonglife.domain.place.service;

import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceSummaryListResponse;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceSummaryResponse;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.entity.PlaceLike;
import com.umust.dobonglife.domain.place.domain.repository.PlaceLikeRepository;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.common.model.BaseStatus;
import com.umust.dobonglife.global.common.model.constant.Category;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.external.s3.S3Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlaceLikeRepository placeLikeRepository;

    @Mock
    private S3Utils s3Utils;

    @InjectMocks
    private PlaceService placeService;

    // =========================================================================
    // Helper
    // =========================================================================

    private Place createTestPlace() {
        return Place.builder()
                .id(1L)
                .name("도봉산 카페")
                .subName("자연 속 카페")
                .content("아름다운 카페")
                .address("서울 도봉구")
                .operatingHour("09:00~21:00")
                .contact("02-123-4567")
                .category(Category.CAFE)
                .latitude(37.6898)
                .longitude(127.0472)
                .thumbnailUrl("https://example.com/thumb.jpg")
                .imageUrls(List.of("https://example.com/img1.jpg"))
                .themes(List.of(CourseTheme.NATURE))
                .build();
    }

    private User createTestUser() {
        return User.builder()
                .id(1L)
                .email("test@example.com")
                .name("테스터")
                .build();
    }

    // =========================================================================
    // toggleLikes
    // =========================================================================

    @Test
    @DisplayName("toggleLikes - 첫 좋아요 시 새 PlaceLike 생성")
    void toggleLikes_newLike() {
        // given
        User user = createTestUser();
        Place place = createTestPlace();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(placeRepository.findById(1L)).willReturn(Optional.of(place));
        given(placeLikeRepository.findByUserIdAndPlaceId(1L, 1L)).willReturn(Optional.empty());

        // when
        placeService.toggleLikes(1L, 1L);

        // then
        verify(placeLikeRepository).save(any(PlaceLike.class));
    }

    @Test
    @DisplayName("toggleLikes - INACTIVE 상태 좋아요 복원")
    void toggleLikes_restore() {
        // given
        User user = createTestUser();
        Place place = createTestPlace();
        PlaceLike placeLike = PlaceLike.builder().id(1L).user(user).place(place).build();
        placeLike.softDelete(); // INACTIVE

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(placeRepository.findById(1L)).willReturn(Optional.of(place));
        given(placeLikeRepository.findByUserIdAndPlaceId(1L, 1L)).willReturn(Optional.of(placeLike));

        // when
        placeService.toggleLikes(1L, 1L);

        // then
        assertThat(placeLike.getStatus()).isEqualTo(BaseStatus.ACTIVE);
        verify(placeLikeRepository, never()).save(any());
    }

    @Test
    @DisplayName("toggleLikes - ACTIVE 상태 좋아요 취소")
    void toggleLikes_softDelete() {
        // given
        User user = createTestUser();
        Place place = createTestPlace();
        PlaceLike placeLike = PlaceLike.builder().id(1L).user(user).place(place).build();
        placeLike.restore(); // ACTIVE

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(placeRepository.findById(1L)).willReturn(Optional.of(place));
        given(placeLikeRepository.findByUserIdAndPlaceId(1L, 1L)).willReturn(Optional.of(placeLike));

        // when
        placeService.toggleLikes(1L, 1L);

        // then
        assertThat(placeLike.getStatus()).isEqualTo(BaseStatus.INACTIVE);
        verify(placeLikeRepository, never()).save(any());
    }

    @Test
    @DisplayName("toggleLikes - 존재하지 않는 유저 예외")
    void toggleLikes_userNotFound() {
        // given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> placeService.toggleLikes(999L, 1L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.USER_NOT_FOUND));
    }

    @Test
    @DisplayName("toggleLikes - 존재하지 않는 장소 예외")
    void toggleLikes_placeNotFound() {
        // given
        User user = createTestUser();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(placeRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> placeService.toggleLikes(1L, 999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.PLACE_NOT_FOUND));
    }

    // =========================================================================
    // getLikedPlace
    // =========================================================================

    @Test
    @DisplayName("getLikedPlace - 커서 기반 좋아요 장소 조회 성공")
    void getLikedPlace_success() {
        // given
        Place place = createTestPlace();
        List<Place> places = List.of(place);
        Slice<Place> slice = new SliceImpl<>(places, PageRequest.of(0, 2), false);

        given(placeRepository.findLikedPlaceSummaries(eq(1L), isNull(), any()))
                .willReturn(slice);

        // when
        CursorResponse<PlaceSummaryResponse> result = placeService.getLikedPlace(1L, 2, null);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPlaceId()).isEqualTo(1L);
        assertThat(result.isHasNext()).isFalse();
    }

    // =========================================================================
    // getAllPlace
    // =========================================================================

    @Test
    @DisplayName("getAllPlace - 전체 장소 목록 조회 성공")
    void getAllPlace_success() {
        // given
        List<PlaceSummaryResponse> responses = List.of(
                PlaceSummaryResponse.builder()
                        .placeId(1L)
                        .placeName("도봉산 카페")
                        .category("카페")
                        .thumbnailUrl("https://example.com/thumb.jpg")
                        .averageRating(4.5)
                        .reviewCount(10L)
                        .isLiked(true)
                        .latitude(37.6898)
                        .longitude(127.0472)
                        .themes(List.of("NATURE"))
                        .build()
        );

        given(placeRepository.findPlaceSummaries(1L)).willReturn(responses);

        // when
        PlaceSummaryListResponse result = placeService.getAllPlace(1L);

        // then
        assertThat(result.getResponseList()).hasSize(1);
        assertThat(result.getResponseList().get(0).getPlaceId()).isEqualTo(1L);
    }

    // =========================================================================
    // findById
    // =========================================================================

    @Test
    @DisplayName("findById - 장소 조회 성공")
    void findById_success() {
        // given
        Place place = createTestPlace();
        given(placeRepository.findById(1L)).willReturn(Optional.of(place));

        // when
        Place result = placeService.findById(1L);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("도봉산 카페");
    }

    @Test
    @DisplayName("findById - 존재하지 않는 장소 예외")
    void findById_notFound() {
        // given
        given(placeRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> placeService.findById(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.PLACE_NOT_FOUND));
    }

    // =========================================================================
    // updatePlaceRatingAndCount
    // =========================================================================

    @Test
    @DisplayName("updatePlaceRatingAndCount - 리뷰 생성 시 평점 업데이트")
    void updatePlaceRatingAndCount_create() {
        // given
        Place place = createTestPlace(); // averageRating=0.0, reviewCount=0
        given(placeRepository.findById(1L)).willReturn(Optional.of(place));

        // when
        placeService.updatePlaceRatingAndCount(1L, 0.0, 4.5, "create");

        // then
        assertThat(place.getAverageRating()).isEqualTo(4.5);
        assertThat(place.getReviewCount()).isEqualTo(1L);
        verify(placeRepository).save(place);
    }

    // =========================================================================
    // deletePlaceReview
    // =========================================================================

    @Test
    @DisplayName("deletePlaceReview - 리뷰 삭제 시 평점 업데이트")
    void deletePlaceReview_success() {
        // given
        Place place = createTestPlace();
        place.applyNewReview(4.0); // averageRating=4.0, reviewCount=1
        given(placeRepository.findById(1L)).willReturn(Optional.of(place));

        // when
        placeService.deletePlaceReview(1L, 4.0);

        // then
        assertThat(place.getAverageRating()).isEqualTo(0.0);
        assertThat(place.getReviewCount()).isEqualTo(0L);
        verify(placeRepository).save(place);
    }
}
