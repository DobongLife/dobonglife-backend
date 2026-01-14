package com.umust.dobonglife.domain.place.service;

import com.umust.dobonglife.domain.place.controller.dto.response.PlaceDetailResponse;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.entity.PlaceLike;
import com.umust.dobonglife.domain.place.domain.repository.PlaceLikeRepository;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.model.BaseStatus;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceReviewService {
    private final UserService userService;
    private final ReviewService reviewService;
    private final PlaceRepository placeRepository;
    private final PlaceLikeRepository placeLikeRepository;

    @Transactional(readOnly = true)
    public PlaceDetailResponse getPlaceDetail(Long placeId, Long userId, Long lastId, int size) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));
        User user = userService.findById(userId);
        boolean isLiked = placeLikeRepository.existsByUserIdAndPlaceIdAndStatus(userId, placeId, BaseStatus.ACTIVE);
        CursorResponse<ReviewSummaryResponse> reviews = reviewService.getPlaceReviews(placeId, userId, lastId, size);
        return PlaceDetailResponse.from(place, reviews, isLiked);
    }
}