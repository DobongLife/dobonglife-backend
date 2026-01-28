package com.umust.dobonglife.domain.place.controller;

import com.umust.dobonglife.domain.place.controller.dto.request.PlaceRegisterRequest;
import com.umust.dobonglife.domain.place.controller.dto.request.ThemeRequest;
import com.umust.dobonglife.domain.place.controller.dto.response.*;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.place.service.PlaceReviewService;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "장소 API", description = "장소 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/places")
public class PlaceController {
    private final PlaceService placeService;
    private final PlaceReviewService placeReviewService;



    @Operation(summary = "장소 좋아요", description = "장소를 좋아요합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "장소 좋아요에 성공하였습니다."
    )
    @PostMapping("/{placeId}/like")
    public BaseResponse<Void> likePlace(@CurrentUserId Long userId,
                                        @PathVariable("placeId") Long placeId){
        placeService.toggleLikes(userId, placeId);
        return BaseResponse.ok(null);
    }

    @Operation(summary = "좋아요 장소 조회", description = "내가 좋아요한 장소를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "좋아요 장소 조회에 성공하였습니다."
    )
    @GetMapping("/like/my")
    public BaseResponse<CursorResponse<PlaceSummaryResponse>> getMyLikedPlace(@CurrentUserId Long userId,
                                                                @RequestParam(required = false) Long lastId,
                                                                @RequestParam(defaultValue = "2") int size){

        CursorResponse<PlaceSummaryResponse> response = placeService.getLikedPlace(userId, size, lastId);
        return BaseResponse.ok(response);
    }

    @Operation(summary = "장소 상세 조회", description = "장소를 상세 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "장소 상세 조회에 성공하였습니다."
    )
    @GetMapping("/{placeId}")
    public BaseResponse<PlaceDetailResponse> getPlaceDetail(@PathVariable Long placeId,
                                                            @CurrentUserId Long userId,
                                                            @RequestParam(required = false) Long lastReviewId,
                                                            @RequestParam(defaultValue = "2") int size) {
        return BaseResponse.ok(placeReviewService.getPlaceDetail(placeId, userId, lastReviewId, size));
    }

    @Operation(summary = "장소 전체 조회", description = "장소를 전체 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "장소 전체 조회에 성공하였습니다."
    )
    @GetMapping()
    public BaseResponse<PlaceSummaryListResponse> getAllPlace(@CurrentUserId Long userId) {
        return BaseResponse.ok(placeService.getAllPlace(userId));
    }

    @Operation(summary = "장소 등록", description = "장소 정보를 입력하여 등록합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "장소 등록에 성공하였습니다."
    )
    @PostMapping("/{address}")
    public BaseResponse<Void> registerPlace(@PathVariable String address
                                            //@RequestPart("request") @Valid PlaceRegisterRequest request
                                            //@RequestPart("images") List<MultipartFile> images
                                            ) throws Exception {
        placeService.registerPlace(address);
        return BaseResponse.ok(null);
    }
}
