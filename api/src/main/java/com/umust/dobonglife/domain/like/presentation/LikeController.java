package com.umust.dobonglife.domain.like.presentation;

import com.umust.dobonglife.domain.like.application.LikeService;
import com.umust.dobonglife.domain.like.application.dto.MyLikedPlaceResponse;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/place/{placeId}")
    public ResponseEntity<BaseResponse<Void>> likePlace(@CurrentUserId Long userId,
                                                        @PathVariable Long placeId) {
        likeService.toggleLike(userId, TargetType.PLACE, placeId);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }

    @GetMapping("/place/my")
    public ResponseEntity<BaseResponse<CursorResponse<MyLikedPlaceResponse>>> getMyLikedPlaces(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.PLACE) int size) {
        CursorResponse<MyLikedPlaceResponse> response = likeService.getMyLikedPlaces(userId, lastId, size);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }
}
