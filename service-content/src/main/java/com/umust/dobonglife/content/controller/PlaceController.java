package com.umust.dobonglife.content.controller;

import com.umust.dobonglife.content.facade.PlaceListFacade;
import com.umust.dobonglife.domain.place.application.PlaceReviewService;
import com.umust.dobonglife.domain.place.application.dto.PlaceDetailResponse;
import com.umust.dobonglife.domain.place.application.dto.PlaceSummaryResponse;
import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/place")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceReviewService placeReviewService;
    private final PlaceListFacade placeListFacade;

    @GetMapping
    public BaseResponse<List<PlaceSummaryResponse>> getAllPlaces(@CurrentUserId Long userId) {
        return BaseResponse.ok(placeListFacade.getAllPlaces(userId));
    }

    @GetMapping("/{placeId}")
    public BaseResponse<PlaceDetailResponse> getPlaceDetail(
            @PathVariable Long placeId, @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "2") int size) {
        return BaseResponse.ok(placeReviewService.getPlaceDetail(placeId, userId, lastId, size));
    }
}
