package com.umust.dobonglife.domain.like.presentation;

import com.umust.dobonglife.domain.like.application.LikeService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/place/{placeId}")
    public ResponseEntity<BaseResponse<SuccessCode>> likePlace(@CurrentUserId Long userId,
                                                               @PathVariable Long placeId) {
        likeService.toggleLike(userId, TargetType.PLACE, placeId);
        return ResponseEntity.ok(BaseResponse.ok(SuccessCode.LIKE_SUCCESS));
    }

}
