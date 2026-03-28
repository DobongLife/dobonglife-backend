package com.umust.dobonglife.domain.place.application;

import com.umust.dobonglife.domain.like.application.LikeService;
import com.umust.dobonglife.domain.place.application.dto.PlaceDetailResponse;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.review.application.ReviewService;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceReviewService {

    private final PlaceService placeService;
    private final LikeService likeService;
    private final ReviewService reviewService;

    public PlaceDetailResponse getPlaceDetail(Long placeId, Long userId, Long lastId, int size) {
        Place place = placeService.getPlace(placeId);

        boolean isLiked = likeService.isLiked(userId, TargetType.PLACE, placeId);

        CursorResponse<ReviewSummaryResponse> reviews = reviewService.getPlaceReviews(placeId, lastId, size);

        return PlaceDetailResponse.of(place, isLiked, reviews);
    }
}
