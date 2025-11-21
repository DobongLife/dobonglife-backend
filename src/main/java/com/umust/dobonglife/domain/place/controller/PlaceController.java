package com.umust.dobonglife.domain.place.controller;

import com.umust.dobonglife.domain.place.dto.request.PlaceRegisterRequest;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/place")
public class PlaceController {
    private final PlaceService placeService;

    @PostMapping
    public BaseResponse<Void> registerPlace(@RequestBody PlaceRegisterRequest request,
                                            @RequestPart(value = "file", required = false) List<MultipartFile> placeImageList){
        placeService.registerPlace(request, placeImageList);
        return BaseResponse.ok(null);
    }

}
