package com.umust.dobonglife.domain.place.presentation;

import com.umust.dobonglife.domain.place.application.PlaceReviewService;
import com.umust.dobonglife.domain.place.application.dto.PlaceDetailResponse;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/place")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceReviewService placeReviewService;

    @GetMapping("/{placeId}")
    public BaseResponse<PlaceDetailResponse> getPlaceDetail(
            @PathVariable Long placeId,
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "2") int size) {
        return BaseResponse.ok(placeReviewService.getPlaceDetail(placeId, userId, lastId, size));
    }
}
