package com.umust.dobonglife.domain.place.controller;

import com.umust.dobonglife.domain.place.controller.dto.request.PlaceRegisterRequest;
import com.umust.dobonglife.domain.place.controller.dto.request.ThemeRequest;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceListResponse;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/place")
public class PlaceController {
    private final PlaceService placeService;

    @PostMapping
    public BaseResponse<Void> registerPlace(@RequestBody PlaceRegisterRequest request){
        placeService.registerPlace(request);
        return BaseResponse.ok(null);
    }

    @GetMapping
    public BaseResponse<PlaceListResponse> getPlaceByTheme(@RequestBody ThemeRequest request){
        return BaseResponse.ok(placeService.getPlaceByTheme(request));
    }

    @PostMapping("/{placeId}/like")
    public BaseResponse<Void> likePlace(@PathVariable("placeId") Long placeId){
        placeService.toggleLikes(1L, placeId);
        return BaseResponse.ok(null);
    }

    @GetMapping("/like/my")
    public BaseResponse<PlaceListResponse> getMyLikedPlace(){
        return BaseResponse.ok(placeService.getLikedPlace(1L));
    }
}
