package com.umust.dobonglife.domain.place.presentation;

import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.composition.PlaceListFacade;
import com.umust.dobonglife.global.port.content.PlacePort;
import com.umust.dobonglife.global.port.dto.content.PlaceSummaryInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/place")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceListFacade placeListFacade;
    private final PlacePort placePort;

    @GetMapping
    public BaseResponse<List<PlaceSummaryInfo>> getAllPlaces(@CurrentUserId Long userId) {
        return BaseResponse.ok(placeListFacade.getAllPlaces(userId));
    }

    @GetMapping("/{placeId}")
    public BaseResponse<Map<String, Object>> getPlaceDetail(
            @PathVariable Long placeId,
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.REVIEW_PREVIEW) int size) {
        return BaseResponse.ok(placePort.getPlaceDetail(placeId, userId, lastId, size));
    }
}
