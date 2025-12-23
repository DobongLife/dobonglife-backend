package com.umust.dobonglife.domain.place.controller;

import com.umust.dobonglife.domain.place.controller.dto.request.PlaceRegisterRequest;
import com.umust.dobonglife.domain.place.controller.dto.request.ThemeRequest;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceListResponse;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "장소 API", description = "장소 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/place")
public class PlaceController {
    private final PlaceService placeService;

    @Operation(summary = "장소 등록", description = "장소 정보를 입력하여 등록합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "장소 등록에 성공하였습니다."
    )
    @PostMapping
    public BaseResponse<Void> registerPlace(@RequestPart("request") @Valid PlaceRegisterRequest request,
                                            @RequestPart("images") List<MultipartFile> images){
        placeService.registerPlace(request, images);
        return BaseResponse.ok(null);
    }

    @Operation(summary = "주간 테마별 장소 조회", description = "주간 테마별 장소를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "주간 테마별 장소 조회에 성공하였습니다."
    )
    @GetMapping
    public BaseResponse<PlaceListResponse> getPlaceByTheme(@RequestBody ThemeRequest request){
        return BaseResponse.ok(placeService.getPlaceByTheme(request));
    }

    @Operation(summary = "장소 좋아요", description = "장소를 좋아요합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "장소 좋아요 에 성공하였습니다."
    )
    @PostMapping("/{placeId}/like")
    public BaseResponse<Void> likePlace(@PathVariable("placeId") Long placeId){
        placeService.toggleLikes(1L, placeId);
        return BaseResponse.ok(null);
    }

    @Operation(summary = "좋아요 장소 조회", description = "내가 좋아용한 장소를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "좋아요 장소 조회에 성공하였습니다."
    )
    @GetMapping("/like/my")
    public BaseResponse<PlaceListResponse> getMyLikedPlace(){
        return BaseResponse.ok(placeService.getLikedPlace(1L));
    }
}
