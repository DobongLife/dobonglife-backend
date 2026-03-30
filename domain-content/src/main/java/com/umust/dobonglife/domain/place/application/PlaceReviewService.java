package com.umust.dobonglife.domain.place.application;

import com.umust.dobonglife.domain.like.application.port.in.GetLikeUseCase;
import com.umust.dobonglife.domain.place.application.dto.PlaceDetailResponse;
import com.umust.dobonglife.domain.place.application.port.in.GetPlaceDetailUseCase;
import com.umust.dobonglife.domain.place.application.port.in.GetPlaceUseCase;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.review.application.port.in.GetReviewUseCase;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceReviewService implements GetPlaceDetailUseCase {

    private final GetPlaceUseCase getPlaceUseCase;
    private final GetLikeUseCase getLikeUseCase;
    private final GetReviewUseCase getReviewUseCase;

    @Override
    public PlaceDetailResponse getPlaceDetail(Long placeId, Long userId, Long lastId, int size) {
        Place place = getPlaceUseCase.getPlace(placeId);

        boolean isLiked = getLikeUseCase.isLiked(userId, TargetType.PLACE, placeId);

        CursorResponse<ReviewSummaryResponse> reviews = getReviewUseCase.getReviews(TargetType.PLACE, placeId, lastId, size);

        return PlaceDetailResponse.of(place, isLiked, reviews);
    }
}
