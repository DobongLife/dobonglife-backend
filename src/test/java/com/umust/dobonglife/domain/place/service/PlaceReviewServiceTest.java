package com.umust.dobonglife.domain.place.service;

import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceDetailResponse;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.repository.PlaceLikeRepository;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.model.BaseStatus;
import com.umust.dobonglife.global.common.model.constant.Category;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PlaceReviewServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ReviewService reviewService;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private PlaceLikeRepository placeLikeRepository;

    @InjectMocks
    private PlaceReviewService placeReviewService;

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

    private CursorResponse<ReviewSummaryResponse> createMockReviews() {
        List<ReviewSummaryResponse> reviews = List.of(
                new ReviewSummaryResponse(10L, "리뷰어A", 5.0, "좋은 장소입니다!",
                        List.of(), LocalDateTime.of(2025, 6, 1, 10, 0), false)
        );
        return new CursorResponse<>(reviews, 10L, false);
    }

    // =========================================================================
    // getPlaceDetail
    // =========================================================================

    @Test
    @DisplayName("getPlaceDetail - 좋아요한 장소 상세 조회 성공")
    void getPlaceDetail_success() {
        // given
        Place place = createTestPlace();
        User user = createTestUser();
        CursorResponse<ReviewSummaryResponse> reviews = createMockReviews();

        given(placeRepository.findById(1L)).willReturn(Optional.of(place));
        given(userService.findById(1L)).willReturn(user);
        given(placeLikeRepository.existsByUserIdAndPlaceIdAndStatus(1L, 1L, BaseStatus.ACTIVE))
                .willReturn(true);
        given(reviewService.getPlaceReviews(1L, 1L, null, 2)).willReturn(reviews);

        // when
        PlaceDetailResponse result = placeReviewService.getPlaceDetail(1L, 1L, null, 2);

        // then
        assertThat(result.getPlaceId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("도봉산 카페");
        assertThat(result.isLiked()).isTrue();
        assertThat(result.getReviews().getContent()).hasSize(1);
    }

    @Test
    @DisplayName("getPlaceDetail - 좋아요하지 않은 장소 상세 조회")
    void getPlaceDetail_notLiked() {
        // given
        Place place = createTestPlace();
        User user = createTestUser();
        CursorResponse<ReviewSummaryResponse> reviews = createMockReviews();

        given(placeRepository.findById(1L)).willReturn(Optional.of(place));
        given(userService.findById(1L)).willReturn(user);
        given(placeLikeRepository.existsByUserIdAndPlaceIdAndStatus(1L, 1L, BaseStatus.ACTIVE))
                .willReturn(false);
        given(reviewService.getPlaceReviews(1L, 1L, null, 2)).willReturn(reviews);

        // when
        PlaceDetailResponse result = placeReviewService.getPlaceDetail(1L, 1L, null, 2);

        // then
        assertThat(result.isLiked()).isFalse();
        assertThat(result.getName()).isEqualTo("도봉산 카페");
    }

    @Test
    @DisplayName("getPlaceDetail - 존재하지 않는 장소 예외")
    void getPlaceDetail_placeNotFound() {
        // given
        given(placeRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> placeReviewService.getPlaceDetail(999L, 1L, null, 2))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.PLACE_NOT_FOUND));
    }
}
