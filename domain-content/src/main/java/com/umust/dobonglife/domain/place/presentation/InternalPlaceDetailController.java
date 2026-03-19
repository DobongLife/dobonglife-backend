package com.umust.dobonglife.domain.place.presentation;

import com.umust.dobonglife.domain.place.application.PlaceReviewService;
import com.umust.dobonglife.domain.place.application.dto.PlaceDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/place")
@RequiredArgsConstructor
public class InternalPlaceDetailController {

    private final PlaceReviewService placeReviewService;

    @GetMapping("/{placeId}/detail")
    public PlaceDetailResponse getPlaceDetail(
            @PathVariable Long placeId,
            @RequestParam Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam int size) {
        return placeReviewService.getPlaceDetail(placeId, userId, lastId, size);
    }
}
