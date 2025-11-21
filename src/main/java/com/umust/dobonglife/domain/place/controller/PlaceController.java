package com.umust.dobonglife.domain.place.controller;

import com.umust.dobonglife.domain.place.dto.request.PlaceRegisterRequest;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
